package com.cl.data;

public class Load {
	private String utctime; //yyyyMMDDhhmm
	private String servername;
	private short cpu; //1234 means 12.34%
	private short memory; //same as cpu
	
	public Load(String servername){
		this.servername = servername;
	}

	public String getServername() {
		return servername;
	}

	public short getCpu() {
		return cpu;
	}

	public short getMemory() {
		return memory;
	}

	public void setCpu(short cpu) {
		this.cpu = cpu;
	}

	public void setMemory(short memory) {
		this.memory = memory;
	}

	public String getUtctime() {
		return utctime;
	}

	public void setUtctime(String utctime) {
		this.utctime = utctime;
	}

}
