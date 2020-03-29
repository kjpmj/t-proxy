package com.tnc.proxy.netty.common;

public class NettyConstant {
	// 외부 연동 Tnc Netty Clinet Util Class의 package명 (Java Reflection에 필요)
	public static final String TNC_NETTY_CLIENT_CLASS = "com.tnc.client.ClientBootstrapper";
	public static final String TNC_NETTY_CLIENT_METHOD = "init";
	
	// 비지니스 Error
	public static final String BUSINESS_ERROR_000 = "올바른 요청이 아닙니다. 관리자에게 문의하세요";
	public static final String BUSINESS_ERROR_001 = "Http RequestBody가 비어있습니다.";
	public static final String BUSINESS_ERROR_002 = "Http RequestBody에 허용되지 않은 파라미터가 존재합니다.";
	public static final String BUSINESS_ERROR_003 = "HttpMethod는 POST만 가능합니다.";
	public static final String BUSINESS_ERROR_004 = "해당 클래스가 존재하지 않습니다.";
	public static final String BUSINESS_ERROR_005 = "해당 메서드가 존재하지 않습니다.";
	public static final String BUSINESS_ERROR_006 = "해당 메서드의 파라미터정보가 잘못되었습니다.";
	public static final String BUSINESS_ERROR_007 = "해당 클래스의 생성자 정보가 잘못되었습니다.";
	public static final String BUSINESS_ERROR_008 = "요청 데이터 JSON 형식 오류";
	
	
	// 프로토콜
	public static final String PROTOCOL_HTTP = "HTTP";
	public static final String PROTOCOL_HTTPS = "HTTPS";
	public static final String PROTOCOL_TCP = "TCP";

}
