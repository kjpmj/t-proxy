package com.tnc.proxy.netty.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tnc.proxy.netty.common.NettyConstant;
import com.tnc.proxy.netty.common.NettyException;
import com.tnc.proxy.netty.common.NettyUtil;
import com.tnc.proxy.netty.common.NettyVO;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * <pre>
 * RequestBody로 들어온 Class와 Method를 실행하고 결과를 OutboundHandler에게 넘겨주는 Class
 * </pre>
 * @author MJ
 * @since 2020-03-22
 */
public class ExternalReqeustHandler {
	Logger logger = LoggerFactory.getLogger(ExternalReqeustHandler.class);
	
	private NettyVO nettyVO;
	private ChannelHandlerContext ctx;
	private boolean isTncClient;
	
	public ExternalReqeustHandler(NettyVO nettyVO, ChannelHandlerContext ctx) {
		this.nettyVO = nettyVO;
		this.ctx = ctx;
		this.isTncClient = NettyConstant.TNC_NETTY_CLIENT_CLASS.equals(nettyVO.getClassName()) && NettyConstant.TNC_NETTY_CLIENT_METHOD.equals(nettyVO.getMethod());
	}

	public void request() {
		String resMsg = "";
		
		try {
//			Class<?> c = Class.forName(nettyVO.getClassName());
			Class<?> c = ClientBootstrapper.class;
			Method methods[] = c.getDeclaredMethods();
			Method invokeMethod = null;
			
			for(Method method : methods) {
				if(method.getName().equals(nettyVO.getMethod())) {
					invokeMethod = method;
					break;
				}
			}
			
			if(invokeMethod == null) {
				throw new  NettyException(NettyConstant.BUSINESS_ERROR_005);
			}
			
			// request로 넘어온 className이 NettyConstant.TNC_NETTY_CLIENT_CLASS과 같고 method명이 NettyConstant.TNC_NETTY_CLIENT_METHOD과 같으면 TNC NettyClient로 요청
			// 그렇지 않으면 외부 유틸로 요청
			// 나중에는 server쪽과 Client쪽 공통으로 필요한 것들을 빼서 jar로 제공한다. 고로 map이 아니라 NettyVO 자체를 파라미터로 전달할 것이다.
			if(isTncClient) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("destHost", nettyVO.getDestHost());
				map.put("destPort", nettyVO.getDestPort());
				map.put("destPath", nettyVO.getDestPath());
				map.put("protocol", nettyVO.getProtocol());
//				invokeMethod.invoke(c.newInstance(), ctx, map, nettyVO.getMsg());
				ClientBootstrapper bootstrapper = new ClientBootstrapper();
				bootstrapper.init(ctx, map, nettyVO.getMsg());
				
			}else {
				resMsg = (String) invokeMethod.invoke(c.newInstance(), nettyVO.getParameters().toArray());
			}
		} catch (ClassNotFoundException e) {
			resMsg = NettyConstant.BUSINESS_ERROR_004;
			e.printStackTrace();
		} catch (NettyException e) {
			resMsg = NettyConstant.BUSINESS_ERROR_005;
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			resMsg = NettyConstant.BUSINESS_ERROR_000;
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			resMsg = NettyConstant.BUSINESS_ERROR_006;
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			resMsg = NettyConstant.BUSINESS_ERROR_000;
			e.printStackTrace();
		} catch (SecurityException e) {
			resMsg = NettyConstant.BUSINESS_ERROR_007;
			e.printStackTrace();
		} catch (InstantiationException e) {
			resMsg = NettyConstant.BUSINESS_ERROR_007;
			e.printStackTrace();
		} catch (JsonParseException e) {
			resMsg = NettyConstant.BUSINESS_ERROR_008;
			e.printStackTrace();
		} catch (Exception e){
			resMsg = NettyConstant.BUSINESS_ERROR_000;
			e.printStackTrace();
		} finally {
			logger.info(resMsg);
			
			// TncClient로 요청이 아닐 때만 응답 결과를 HtttResponse로 만들고 write한다.
			// TncClient로 요청한 것은 TncClient에서 write 할 것이다.
			if(!isTncClient) {
				FullHttpResponse response = NettyUtil.createFullHttpResponse(HttpResponseStatus.OK, resMsg);
				ctx.write(response.retain());
			}
		}
	}
}
