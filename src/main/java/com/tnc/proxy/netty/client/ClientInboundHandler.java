package com.tnc.proxy.netty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ClientInboundHandler extends ChannelInboundHandlerAdapter {
	Logger logger = LoggerFactory.getLogger(ClientInboundHandler.class);
	
	private ChannelHandlerContext channelHandlerContext;
	private Object msg;

	public ClientInboundHandler(ChannelHandlerContext channelHandlerContext, Object msg) {
		this.channelHandlerContext = channelHandlerContext;
		this.msg = msg;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("ClientInboundHandler > channelActive");
		ctx.write(msg);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("ClientInboundHandler > channelRead");
		
		ReferenceCountUtil.retain(msg);
		
		channelHandlerContext.write(msg);
		ChannelFuture future = ctx.close();

		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				logger.info("ClientInboundHandler > channelRead > isSuccess : " + channelFuture.isSuccess());
			}
		});
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
