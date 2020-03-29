package com.tnc.proxy.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * <pre>
 * 서버 역할을 하는 Channel에 요청과 응답을 하는 Hadler를 모아 놓은 Class
 * </pre>
 * @author MJ
 * @since 2020-03-22
 */
public class ServerInitializer extends ChannelInitializer<Channel> {
	private Logger logger = LoggerFactory.getLogger(ServerInitializer.class);

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("detectProtocolDecoder", new DetectProtocolDecoder());
		pipeline.addLast("serverOutboundHandler", new ServerOutboundHandler());		// 응답을 처리하는 Handler
		pipeline.addLast("serverInboundHandler", new ServerInboundHandler());		// 요청을 처리하는 Handler
	}


}
