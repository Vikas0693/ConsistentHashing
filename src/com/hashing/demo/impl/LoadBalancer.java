package com.hashing.demo.impl;

import com.hashing.demo.AllocatorI;

public class LoadBalancer {
	AllocatorI allocator;
	
	public LoadBalancer(AllocatorI requestAllocator){
		allocator = requestAllocator;
	}
	
	public int getServerToAllocat(int requestId) {
		return allocator.getServerId(requestId);
	}
}
