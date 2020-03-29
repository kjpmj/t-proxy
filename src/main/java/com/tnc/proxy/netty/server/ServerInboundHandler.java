package com.tnc.proxy.netty.server;

import java.io.EOFException;
import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tnc.proxy.netty.client.ExternalReqeustHandler;
import com.tnc.proxy.netty.common.NettyConstant;
import com.tnc.proxy.netty.common.NettyException;
import com.tnc.proxy.netty.common.NettyUtil;
import com.tnc.proxy.netty.common.NettyVO;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * <pre>
 * 클라이언트의 요청을 처리하는 Hadler
 * </pre>
 * 
 * @author MJ
 * @since 2020-03-22
 */
public class ServerInboundHandler extends ChannelInboundHandlerAdapter {
	Logger logger = LoggerFactory.getLogger(ServerInboundHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		logger.info("ServerInboundHandler > channelActive");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("ServerInboundHandler > channelRead");
		HttpRequest req = null;
		HttpContent reqContent = null;
		
		if(msg instanceof FullHttpRequest) {
			req = (HttpRequest) msg;
			reqContent = (HttpContent) msg;
		}

		NettyVO vo = null;

		try {
			// POST method만 허용
//			if(!NettyUtil.verifyHttpMethod(req.method())) {
//				throw new NettyException(NettyConstant.BUSINESS_ERROR_003);
//			}

			// ProxyRequestVO 생성
			vo = NettyUtil.createProxyRequestVO(reqContent);

			// msg 설정
			vo.setMsg(msg);
			vo.setProtocol(NettyUtil.getProtocolByHandler(ctx.pipeline()));

			// 외부 연동을 해서 응답까지 한다.
			ExternalReqeustHandler externalReqeustHandler = new ExternalReqeustHandler(vo, ctx);
			externalReqeustHandler.request();

		} catch (NettyException e) {
			e.printStackTrace();
			FullHttpResponse response = NettyUtil.createFullHttpResponse(HttpResponseStatus.BAD_REQUEST,
					e.getMessage());
			ctx.write(response.retain()).addListener(ChannelFutureListener.CLOSE);
		} catch (EOFException e) {
			e.printStackTrace();
			FullHttpResponse response = NettyUtil.createFullHttpResponse(HttpResponseStatus.BAD_REQUEST,
					NettyConstant.BUSINESS_ERROR_001);
			ctx.write(response.retain()).addListener(ChannelFutureListener.CLOSE);
		} catch (UnrecognizedPropertyException e) {
			e.printStackTrace();
			FullHttpResponse response = NettyUtil.createFullHttpResponse(HttpResponseStatus.BAD_REQUEST,
					NettyConstant.BUSINESS_ERROR_002, "허용되지 않은 파라미터 : " + e.getUnrecognizedPropertyName());
			ctx.write(response.retain()).addListener(ChannelFutureListener.CLOSE);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
