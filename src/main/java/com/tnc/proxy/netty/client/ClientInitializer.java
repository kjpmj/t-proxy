package com.tnc.proxy.netty.client;

import javax.net.ssl.SSLEngine;

import com.tnc.proxy.netty.common.NettyConstant;
import com.tnc.proxy.netty.common.NettyException;
import com.tnc.proxy.netty.common.NettyUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

public class ClientInitializer extends ChannelInitializer<Channel> {
	private ChannelHandlerContext channelHandlerContext;
	private Object msg;
	private String destPath;
	private String protocol;

	public ClientInitializer(ChannelHandlerContext channelHandlerContext, Object msg, String destPath, String protocol) {
		this.channelHandlerContext = channelHandlerContext;
		this.msg = msg;
		this.destPath = destPath;
		this.protocol = protocol;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		switch (protocol) {
		case NettyConstant.PROTOCOL_HTTPS:
			SslContext context = NettyUtil.getSSLContext();
			SSLEngine engine = context.newEngine(pipeline.channel().alloc());
			pipeline.addLast("sslHandler", new SslHandler(engine));
			pipeline.addLast("httpClientCodec", new HttpClientCodec());							
			pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(512 * 1024));
			break;

		case NettyConstant.PROTOCOL_HTTP:
			pipeline.addLast("httpClientCodec", new HttpClientCodec());							
			pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(512 * 1024));
			break;
			
		case NettyConstant.PROTOCOL_TCP:
			break;
			
		default: 
			throw new NettyException("알 수 없는 프로토콜입니다.");
		}
		
		pipeline.addLast("clientOutboundHandler", new ClientOutboundHandler(destPath));
		pipeline.addLast("clientInboundHandler", new ClientInboundHandler(channelHandlerContext, msg));

	}
}
