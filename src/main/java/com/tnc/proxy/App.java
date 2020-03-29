package com.tnc.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tnc.proxy.netty.server.ServerBootstrapper;
import com.tnc.proxy.util.ConfigManager;


/**
 * <pre>
 * T-PROXY-C 메인 Class
 * </pre>
 * @author MJ
 * @since 2020-03-22
 */
public class App {
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(App.class);
		
		// 앱에서 사용할 Config 파일을 설정하지 않으면 종료된다.
		if (args.length < 1) {
			logger.error("Config File Name Not found");
			System.exit(0);
		}

		// 설정파일을 읽는다. CONFIG_X는 XML파일, CONFIG_P는 Properties파일이다. ConfigManager는 싱글톤이다.
		ConfigManager configManager = ConfigManager.getInstance();
		configManager.load(ConfigManager.CONFIG_X, args[0]);

		// 서버 역할을 하는 Netty Server Bootstrap을 생성한다.
		ServerBootstrapper serverBootstrapper = new ServerBootstrapper();
		serverBootstrapper.init();
	}
}

