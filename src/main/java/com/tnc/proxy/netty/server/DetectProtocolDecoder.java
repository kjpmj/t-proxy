package com.tnc.proxy.netty.server;

import java.util.List;

import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tnc.proxy.netty.common.NettyConstant;
import com.tnc.proxy.netty.common.NettyException;
import com.tnc.proxy.netty.common.NettyUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

public class DetectProtocolDecoder extends ByteToMessageDecoder{
	Logger logger = LoggerFactory.getLogger(DetectProtocolDecoder.class);
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		
		// 읽을 수 있는 byte가 5개 이상이여야 Https인지 아닌지 확인 가능함
		if (in.readableBytes() < 5) {
            return;
        }
		
		String protocol = getProtocol(in);
		
		logger.info("+-+-+-+-+-+-+-+-+-+-+-+-+ 요청 프로토콜: " + protocol + " +-+-+-+-+-+-+-+-+-+-+-+-+");
		
		switch (protocol) {
		case NettyConstant.PROTOCOL_HTTPS:
			SslContext context = NettyUtil.getSSLContext();
			SSLEngine engine = context.newEngine(ctx.pipeline().channel().alloc());
			ctx.pipeline().addAfter("detectProtocolDecoder", "sslHandler", new SslHandler(engine));
			ctx.pipeline().addAfter("sslHandler", "httpServerCodec", new HttpServerCodec());							
			ctx.pipeline().addAfter("httpServerCodec", "httpObjectAggregator", new HttpObjectAggregator(512 * 1024));
			break;

		case NettyConstant.PROTOCOL_HTTP:
			ctx.pipeline().addAfter("detectProtocolDecoder", "httpServerCodec", new HttpServerCodec());							
			ctx.pipeline().addAfter("httpServerCodec", "httpObjectAggregator", new HttpObjectAggregator(512 * 1024));
			break;
			
		case NettyConstant.PROTOCOL_TCP:
			break;
			
		default: 
			throw new NettyException("알 수 없는 프로토콜입니다.");
		}

		ctx.pipeline().remove(this);

	}

	private String getProtocol(ByteBuf in) {
		// HTTPS
		if(SslHandler.isEncrypted(in)) return NettyConstant.PROTOCOL_HTTPS;
		// HTTP
		if(isHTTP(in)) return NettyConstant.PROTOCOL_HTTP;
		
		// TCP
		return NettyConstant.PROTOCOL_TCP;
	}
	
	private boolean isHTTP(ByteBuf buf) {
		String method = buf.toString(0,8, CharsetUtil.UTF_8);
		if (method.startsWith("GET")     ) return true;
		if (method.startsWith("POST")    ) return true;
		if (method.startsWith("PUT")     ) return true;
		if (method.startsWith("PATCH")   ) return true;
		if (method.startsWith("DELETE")  ) return true;
		if (method.startsWith("COPY")    ) return true;
		if (method.startsWith("HEAD")    ) return true;
		if (method.startsWith("OPTIONS") ) return true;
		if (method.startsWith("LINK")    ) return true;
		if (method.startsWith("UNLINK")  ) return true;
		if (method.startsWith("PURGE")   ) return true;
		if (method.startsWith("LOCK")    ) return true;
		if (method.startsWith("UNLOCK")  ) return true;
		if (method.startsWith("PROPFIND")) return true;
		if (method.startsWith("VIEW")    ) return true;
		
        return false;
	}
}
