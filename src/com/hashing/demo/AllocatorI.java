package com.hashing.demo;

public abstract class AllocatorI {
	/*
	 * Maintains number of server that might increase while request keep coming
	 */
	protected int totalServers=0;
	
	public AllocatorI(){
	}
	
	public AllocatorI(int totalServers) {
		this.totalServers = totalServers;
	}

	public void addServer() {
		totalServers+=1;
	}
	
	public void removeServer() throws Exception {
		if(totalServers == 1)
			throw new Exception("Number of servers are 1 so cannot remove.");
		totalServers-=1;
	}
	
	abstract public int getServerId(int requestId) ;
}
