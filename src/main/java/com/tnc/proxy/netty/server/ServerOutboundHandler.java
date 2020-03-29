package com.tnc.proxy.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;

/**
 * <pre>
 * 요청한 클라이언트에 응답 하는 Hadler
 * </pre>
 * @author MJ
 * @since 2020-03-22
 */
public class ServerOutboundHandler extends ChannelOutboundHandlerAdapter{
	Logger logger = LoggerFactory.getLogger(ServerOutboundHandler.class);
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		logger.info("ServerOutboundHandler > write");
		
		FullHttpResponse response = null;
		
		if(msg instanceof FullHttpResponse) {
			response = (FullHttpResponse) msg; 
		}
		
		ChannelFuture future = ctx.writeAndFlush(response);
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				logger.info("ServerOutboundHandler > isSuccess: " + channelFuture.isSuccess());
				channelFuture.channel().close();
				ReferenceCountUtil.release(msg);
			}
		});

	}
}
