package com.tnc.proxy.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {
	private Logger logger = LoggerFactory.getLogger(ConfigManager.class);
	
	public static final String CONFIG_P = "properties";
	public static final String CONFIG_X = "xml";
	
	private ConfigManager(){}
	
	private HashMap<String, String> configMap = new HashMap<String, String>();
	
	private static class LazyHolodr {
		public static final ConfigManager INSTANCE = new ConfigManager();
	}
	
	public static ConfigManager getInstance() {
		return LazyHolodr.INSTANCE;
	}
	
	private void setValue(String key, String value) {
		configMap.put(key, value);
	}
	public String getValue(String key) {
		return configMap.get(key);
	}
	
	/**
	 * <pre>
	 * 설정 파일 로드 및 타입 별 파싱 메소드 호출
	 * </pre>
	 * 
	 * @param type
	 *            ( XML, DB, PROPERTIES )
	 * @param path
	 *            설정파일경로
	 */
	public void load(String type, String path) {
		loadBanner();

		logger.debug("TYPE:" + type + " " + "PATH:" + path);

		if (CONFIG_P.equals(type)) {
			loadProperties(path);
		} else if (CONFIG_X.equals(type)) {
			loadXML(path);
		}
	}
	
	/**
	 * <pre>
	 * PROPERTIES 처리 메소드
	 * </pre>
	 * 
	 * @param path
	 *            설정파일경로
	 */
	public void loadProperties(String path) {
		Properties properties = new Properties();
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream fis = null;

		try {
			fis = classLoader.getResourceAsStream(path);
			properties.load(fis);
			Iterator<Object> iter = properties.keySet().iterator();
			while(iter.hasNext()) {
				String key = (String)iter.next();
				String value = properties.getProperty(key);
				
				setValue(key, value);
				logger.info("KEY:" + key + " " + "VALUE:" + value);
			}

		} catch (InvalidPropertiesFormatException e) {
			logger.error("ConfigManager loadProperties 실패. InvalidPropertiesFormat 예외");
			logger.error("",e);
		} catch (IOException e) {
			logger.error("ConfigManager loadProperties 실패. IOException");
			logger.error("",e);
		} finally {
			try {
				if(fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				logger.error("InputStream Close 실패. IOException");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <pre>
	 * XML 설정처리 메소드
	 * </pre>
	 * 
	 * @param path
	 *            설정파일경로
	 */
	public void loadXML(String path) {
		logger.info("======================Load Configuration from XML======================");
		
		Properties properties = new Properties();
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream fis = null;
		
		try {
			fis = classLoader.getResourceAsStream(path);
			properties.loadFromXML(fis);
			Iterator<Object> iter = properties.keySet().iterator();
			while(iter.hasNext()) {
				String key = (String)iter.next();
				String value = properties.getProperty(key);
				
				setValue(key, value);
				logger.info("KEY:" + key + " " + "VALUE:" + value);
			}

		} catch (InvalidPropertiesFormatException e) {
			logger.error("ConfigManager loadXml 실패. InvalidPropertiesFormat 예외");
			logger.error("",e);
		} catch (IOException e) {
			logger.error("ConfigManager loadXml 실패. IOException");
			logger.error("",e);
		} finally {
			try {
				if(fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				logger.error("InputStream Close 실패. IOException");
				e.printStackTrace();
			}
		}
	}
	
	public void loadBanner() {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream fis = null;
		
		try {
			fis = classLoader.getResourceAsStream("banner.txt");
			
			if(fis == null || fis.available() == 0) {
				return;
			}
			
			byte[] bytes = new byte[fis.available()];
			
			fis.read(bytes);
			
			for(byte b : bytes) {
				System.out.print((char) b);
			}
			
		} catch (Exception e) {
			return;
		}
				
	}
}
