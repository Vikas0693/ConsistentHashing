package com.hashing.demo.impl;

import com.hashing.demo.AllocatorI;

public class SimpleHashingAllocator extends AllocatorI {

	public SimpleHashingAllocator(int totalServers){
		this.totalServers = totalServers;
	}
	
	@Override
	public int getServerId(int requestId) {
		return requestId%totalServers;
	}

}
