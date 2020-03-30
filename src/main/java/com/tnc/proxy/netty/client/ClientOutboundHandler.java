package com.tnc.proxy.netty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;

public class ClientOutboundHandler extends ChannelOutboundHandlerAdapter{
	Logger logger = LoggerFactory.getLogger(ClientOutboundHandler.class);
	private String destPath;
	
	public ClientOutboundHandler(String destPath) {
		this.destPath = destPath;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		logger.info("ClientOutboundHandler > write");
		
		FullHttpRequest fhr = null;
		if(msg instanceof FullHttpRequest) {
			fhr = (FullHttpRequest) msg;
			fhr.setUri(destPath);
		}
		
        ChannelFuture future = ctx.writeAndFlush(msg);
        future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				logger.info("ClientOutboundHandler > isSuccess: " + channelFuture.isSuccess());
			}
		});
	}
}
