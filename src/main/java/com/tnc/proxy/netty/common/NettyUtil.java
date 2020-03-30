package com.tnc.proxy.netty.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tnc.proxy.util.ConfigManager;
import com.tnc.proxy.util.DefineCode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.CharsetUtil;

public class NettyUtil {
	
	private static Logger logger = LoggerFactory.getLogger(NettyUtil.class);
	
	/**
	 * ProxyRequestVO 생성
	 * @param httpContent
	 * @return
	 */
	public static NettyVO createProxyRequestVO(HttpContent httpContent) throws Exception{
		NettyVO nettyVO = null;
		ObjectMapper objectMapper = new ObjectMapper();
		
		// vo에 등록되어있지 않은 항목 허용
		objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// null 혹은 비어있는 항목은 변환하지 않는다.
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		
		ByteBuf buf = httpContent.content();
		byte[] bytes = ByteBufUtil.getBytes(buf);

		// HttpRequestBody가 비어있으면 Exception
		if(bytes.length == 0) {
			
		}
		// HttpRequestBody에 포함된 항목으로 VO 생성 VO에 선언 안된 항목이 넘어올 시 UnrecognizedPropertyException
		nettyVO = objectMapper.readValue(bytes, NettyVO.class);
		
		return nettyVO;
	}
	
	/**
	 * ProxyRequestVO TCP용 생성
	 * @param msg
	 * @return
	 */
	public static NettyVO createProxyRequestVO(Object msg) throws Exception{
		NettyVO nettyVO = null;
		ObjectMapper objectMapper = new ObjectMapper();
		
		// vo에 등록되어있지 않은 항목 허용
		objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// null 혹은 비어있는 항목은 변환하지 않는다.
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		
		ByteBuf buf = (ByteBuf) msg;
		byte[] bytes = ByteBufUtil.getBytes(buf);
		nettyVO = objectMapper.readValue(bytes, NettyVO.class);
		
		return nettyVO;
	}
	
	/**
	 * HttpMethod 체크
	 * POST면 true
	 * 아니면 false
	 * */
	public static boolean verifyHttpMethod(HttpMethod httpMethod) {
		if(HttpMethod.POST.equals(httpMethod)) {
			return true;
		}
		
		return false; 
	}
	
	public static FullHttpResponse createFullHttpResponse(HttpResponseStatus status, String msg) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(msg.getBytes(CharsetUtil.UTF_8)));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + "; " + HttpHeaderValues.CHARSET + "=utf-8");
		return response;
	}
	
	public static FullHttpResponse createFullHttpResponse(HttpResponseStatus status, String msg, String detailMsg) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer((msg + "\r\n" + detailMsg).getBytes(CharsetUtil.UTF_8)));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + "; " + HttpHeaderValues.CHARSET + "=utf-8");
		return response;
	}
	
	/**
	 * SslContext 반환
	 * */
	public static SslContext getSSLContext() throws SSLException {
		ConfigManager configManager = ConfigManager.getInstance();
		SslContext context = null;

		// file 등록
		File certFile = new File(configManager.getValue(DefineCode.HTTP_SSL_CERT_NAME));
		File keyFile = new File(configManager.getValue(DefineCode.HTTP_SSL_KEY_NAME));
		
		List<File> files = new ArrayList<File>();
		files.add(certFile);
		files.add(keyFile);

		// file 존재하는지 확인
		for (File file : files) {
			if (file.exists() == false) {
				logger.error("SSL 파일 미존재(" + file.getName() + ")");
//				throw new UserDefineException("", "SSL 파일 미존재(" + file.getName() + ")");
			}
		}
		
		context = SslContextBuilder.forServer(files.get(0), files.get(1)).sslProvider(SslProvider.JDK).build();

		return context;
	}
	
	/**
	 * 현재 파이프라인에 있는 핸들러를 보고 프로토콜 반환
	 * 핸들러 이름은 나중에 공통으로 빼서 server쪽과 client쪽에 jar로 제공한다.
	 * */
	public static String getProtocolByHandler(ChannelPipeline pipeline) {
		List<String> handlerNames = pipeline.names();
		
		for(String handlerName : handlerNames) {
			if("sslHandler".equals(handlerName)) {
				return NettyConstant.PROTOCOL_HTTPS;
			}
			
			if("httpServerCodec".equals(handlerName)) {
				return NettyConstant.PROTOCOL_HTTP;
			}
		}
		
		return NettyConstant.PROTOCOL_TCP;
	}
}
