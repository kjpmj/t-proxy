package com.tnc.proxy.netty.server;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tnc.proxy.util.ConfigManager;
import com.tnc.proxy.util.DefineCode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * <pre>
 * 서버 역할을 하는 Channel을 열기 위한 Netty ServerBootstrap을 생성하는 Class
 * </pre>
 * @author MJ
 * @since 2020-03-22
 */
public class ServerBootstrapper {
	Logger logger = LoggerFactory.getLogger(ServerBootstrapper.class);
	
	public void init() {
		ConfigManager configManager = ConfigManager.getInstance();
		int port = Integer.parseInt(configManager.getValue(DefineCode.LISTEN_PORT));
		int bossGroupThreadNo = Integer.parseInt(configManager.getValue(DefineCode.BOSS_GROUP_THREAD_NO));
		int workerGroupThreadNo = Integer.parseInt(configManager.getValue(DefineCode.WORKER_GROUP_THREAD_NO));
		int connectTimeoutMills = Integer.parseInt(configManager.getValue(DefineCode.CONNECT_TIMEOUT_MILLIS));
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupThreadNo);
		EventLoopGroup workerGroup = new NioEventLoopGroup(workerGroupThreadNo);
		
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ServerInitializer());	// channel에 요청을 받고 응답을 하는 Handler를 추가한다.
			
			// 타임아웃 설정
			serverBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMills);
			
			// 설정한 포트로 바인드한다.
			ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(port)).sync();
			future.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				bossGroup.shutdownGracefully().sync();
				workerGroup.shutdownGracefully().sync();
				logger.info("EventLoopGroup shutdownGracefully");
			} catch (InterruptedException e) {
				logger.info("EventLoopGroup shutdown Error");
				e.printStackTrace();
			}
		}
	}
}