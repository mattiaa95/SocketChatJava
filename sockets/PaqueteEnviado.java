package sockets;

import java.io.Serializable;

class PaqueteEnviado implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 249125452092785838L;
	private String nick,ip,msg;
	
	public PaqueteEnviado(String nick, String ip, String msg) {
		this.nick = nick;
		this.ip = ip;
		this.msg = msg;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}