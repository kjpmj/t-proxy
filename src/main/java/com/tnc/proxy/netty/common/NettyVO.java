package com.tnc.proxy.netty.common;

import java.util.List;

public class NettyVO {
	/**
	 * Client -> Proxy로 요청할 때 수신하는 항목  
	 * 요청 httpMethod는 POST만 가능
	 * */
	
	private String className;
	private String method;
	private List<String> parameters;
		
	/**
	 * 외부 연동으로 요청할 때 연결할 Host, Port, Path 
	 * */
	
	private String destHost;
	private String destPort;
	private String destPath;
	private String protocol;
	
	private Object msg;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public String getDestHost() {
		return destHost;
	}

	public void setDestHost(String destHost) {
		this.destHost = destHost;
	}

	public String getDestPort() {
		return destPort;
	}

	public void setDestPort(String destPort) {
		this.destPort = destPort;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}

	public String getDestPath() {
		return destPath;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
}
