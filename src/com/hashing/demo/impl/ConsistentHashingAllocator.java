package com.hashing.demo.impl;

import java.util.Arrays;

import com.hashing.demo.AllocatorI;

public class ConsistentHashingAllocator extends AllocatorI {
	
	int[] ring = null;
	/*
	 * hashRange is the maxNumber of elements that can come into ring
	 * and the hash function we use that generates hashed value, should be within the range of this hashRange 
	 */
	int hashRange = 0;
	/*
	 * there will only be 1 physical server per server and n virtual server
	 * we will never place physical server into ring instead we will place n virtual servers into the ring
	 * more virtuals , more the load will be evenly distributed in case any server goes down or not
	 */
	int virtualsPerServer = 0;
	
	public ConsistentHashingAllocator(int totalServers, int hashRange, int virtualsPerServer){
		this.totalServers = totalServers;
		this.hashRange = hashRange;
		this.virtualsPerServer = virtualsPerServer;
		ring = new int[hashRange];
		Arrays.fill(ring, -1);
		placeServersInRing();
	}
	
	void placeServersInRing() {
		//every server will be separated by this much distance in the ring
		//300 hashRange will have 3 servers placed at equal distant and repeat that combination as many as number of virtuals times
		int distanceBetweenTwoServers = hashRange / (virtualsPerServer*totalServers);
		int startIndex = 0;
		for(int i=0;i<virtualsPerServer;i++) {
			
			//for each i=physicalServerIndex, we have 3 virtualServer which we will place at equal distance in ring
			for(int j=0;j<totalServers;j++) {				
				ring[startIndex] = j;
				//we should get startIndex by hashing the server id
				//at next j we want to place server j, at distant equal to other distant between any two servers so that load can be balanced
				startIndex+=distanceBetweenTwoServers;
				
			}
		}
	}
	/*
	 * when new server is added we need to make sure that , no existing servers move from their index in ring
	 */
	public void addServer() {
		System.out.println("Adding server in consistentHashinAllocator.");
		totalServers+=1;
		
		int newServerId = totalServers-1;
		//find any two servers in the ring and place them new server in between them
		int a=-1,b=-1;
		for(int i=0;i<ring.length;i++) {
			if(ring[i]!=-1) {
				if(a==-1) {
					a = i;
				}else if(b==-1) {
					b=i;
					break;
				}
			}
		}
		int diffBetweenTwoServers = b-a;
		int distanceFromAToNewServerId = diffBetweenTwoServers/2;
		int aServerId = ring[a];
		int placed=0;
		for(int k=0;k<ring.length;k++) {
			
			if(placed < virtualsPerServer && ring[k] == aServerId) {
				placed++;
				int newServerIdIndex = k+distanceFromAToNewServerId;
				if(newServerIdIndex >= ring.length)
					break;
				ring[newServerIdIndex] = newServerId;
			}
		}
	}
	
	@Override
	public int getServerId(int requestId)  {
		int ringIndex = requestId % hashRange;
		int rounds=1;
		//System.out.println(Arrays.toString(this.ring));
		//any server present after this ringIndex will server the request
		for(int i=ringIndex;i<ring.length && rounds<3 ;i++) {
			if(ring[i]!=-1) {
				return ring[i];
			}
			if(i == ring.length-1) {
				i=0;
				rounds++;
			}
		}
		return -1;
	}
	
	public static void main(String[] args) {
		ConsistentHashingAllocator allocator = new ConsistentHashingAllocator(2,50,3);
		System.out.println(Arrays.toString(allocator.ring));
		allocator.addServer();
		System.out.println(Arrays.toString(allocator.ring));
	}

}