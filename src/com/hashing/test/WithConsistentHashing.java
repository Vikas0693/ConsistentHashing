package com.hashing.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;

import com.hashing.demo.AllocatorI;
import com.hashing.demo.impl.ConsistentHashingAllocator;
import com.hashing.demo.impl.LoadBalancer;

public class WithConsistentHashing {
	static int numberOfRequest = 100;
	static int[] requests = IntStream.rangeClosed(1, numberOfRequest).toArray();
	public static void main(String[] args) {
		Map<Integer, Integer> requestServerMapping1 = getMappingWithNoAdditionOfServers(3, 50, 3);
		Map<Integer, Integer> requestServerMapping2 = getMappingWithOneDynamicAdditionOfServers(3, 50, 3);
		//Map<Integer, Integer> requestServerMapping2 = new HashMap();//getMappingWithOneDynamicAdditionOfServers(3, 50, 3);
		
		//find no of request that changed their mapping, when new server was added in requestServerMapping2
		int count=0;
		int totalRequest = requests.length;
		Iterator<Integer> keyIterator = requestServerMapping1.keySet().iterator();
		while(keyIterator.hasNext()) {
			int requestId = keyIterator.next();
			Integer server1 = requestServerMapping1.get(requestId);
			Integer server2 = requestServerMapping2.get(requestId);
			System.out.println(requestId+"::"+server1+"::"+server2);
			if(server1!=null && server2!=null && server1.intValue()!=server2.intValue()) {
				count++;
			}
		}
		//the % change should be minimum in case of consistent hashing
		//bcoz when we add a server we only decides location of that server and we dont touch location of others
		//this way this new server will handle some request that were supposed to go to its next server
		//hence change should be minimal
		//as compare to simpleHashing which had 72% change when 3 servers were being used 
		//consistent hashing helped us to limit that change to only 28% when new server was added.
		//28% can still be improved to upto 20%
		System.out.println("%change:"+((count*100/totalRequest)));
	}
	
	static private Map<Integer, Integer> getMappingWithOneDynamicAdditionOfServers(int initialServers, int hashRange, int virtuals){
		AllocatorI alloc = new ConsistentHashingAllocator(initialServers,hashRange,virtuals);
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
	
	static private Map<Integer, Integer> getMappingWithNoAdditionOfServers(int initialServers, int hashRange, int virtuals){
		AllocatorI alloc = new ConsistentHashingAllocator(initialServers,hashRange,virtuals);
		Map<Integer, Integer> requestServerMapping = new HashMap<Integer, Integer>();
		LoadBalancer lb = new LoadBalancer(alloc);
		for(int i=0;i<requests.length;i++) {
			int serverId = lb.getServerToAllocat(requests[i]);
			requestServerMapping.put(requests[i], serverId);
		}
		return requestServerMapping;
	}
}
