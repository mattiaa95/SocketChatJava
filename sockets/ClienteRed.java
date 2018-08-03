package sockets;

import java.io.Serializable;

class ClienteRed implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4683430919350576071L;
	private String ip;
	private String nick;
	
	public ClienteRed(String ip, String nick) {
		super();
		this.ip = ip;
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
}
