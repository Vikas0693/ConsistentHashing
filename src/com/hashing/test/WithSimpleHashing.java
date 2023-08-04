package com.hashing.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;

import com.hashing.demo.AllocatorI;
import com.hashing.demo.impl.LoadBalancer;
import com.hashing.demo.impl.SimpleHashingAllocator;

public class WithSimpleHashing {
	static int numberOfRequest = 100;
	static int[] requests = IntStream.rangeClosed(1, numberOfRequest).toArray(); //{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28,29,30,31,32,33,34,35,36};
	
	public static void main(String[] args) {
		
		Map<Integer, Integer> requestServerMapping1 = getMappingWithNoAdditionOfServers(3);
		
		Map<Integer, Integer> requestServerMapping2 = getMappingWithOneDynamicAdditionOfServers(3);
		
		//find no of request that changed their mapping
		int count=0;
		int totalRequest = requests.length;
		Iterator<Integer> keyIterator = requestServerMapping1.keySet().iterator();
		while(keyIterator.hasNext()) {
			int requestId = keyIterator.next();
			int server1 = requestServerMapping1.get(requestId);
			int server2 = requestServerMapping2.get(requestId);
			System.out.println(requestId+"::"+server1+"::"+server2);
			if(server1!=server2) {
				count++;
			}
		}
		//Issues consistent hashing tries to solves
		//1)this % change shows that key mappings are changed , hence request will go to different server each time server is added/removed
		//we are adding 1 server ofter requestId==3, and 70% of request change their target server
		
		//2)since each request id is some user specific data like his name, in real world this could cause the skewed load on servers
		//to see point 2) in action, we can replace request id 4,7,9,14,19 with 3 and will see that only single server handles those request
		System.out.println("%change:"+((count*100/totalRequest)));
	}
	
	static private Map<Integer, Integer> getMappingWithOneDynamicAdditionOfServers(int initialServers){
		AllocatorI alloc = new SimpleHashingAllocator(initialServers);
		Map<Integer, Integer> requestServerMapping = new HashMap<Integer, Integer>();
		LoadBalancer lb = new LoadBalancer(alloc);
		for(int i=0;i<requests.length;i++) {
			int serverId = lb.getServerToAllocat(requests[i]);
			if(i==3) {
				alloc.addServer();
			}
			requestServerMapping.put(requests[i], serverId);
		}
		return requestServerMapping;
	
	}
	
	static private Map<Integer, Integer> getMappingWithNoAdditionOfServers(int initialServers){
		AllocatorI alloc = new SimpleHashingAllocator(initialServers);
		Map<Integer, Integer> requestServerMapping = new HashMap<Integer, Integer>();
		LoadBalancer lb = new LoadBalancer(alloc);
		for(int i=0;i<requests.length;i++) {
			int serverId = lb.getServerToAllocat(requests[i]);
			requestServerMapping.put(requests[i], serverId);
		}
		return requestServerMapping;
	}

}
