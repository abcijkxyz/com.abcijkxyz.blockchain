package com.abcijkxyz.blockchain.test;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest12 {
	public static void main(String[] args) {
		ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
		for (int i = 1; i <= 10; i++) {
			concurrentHashMap.put(i, i);
		}
		System.out.println("now concurrentHashMap: " + concurrentHashMap);
		System.out.println("====================================");
		System.out.println("concurrentHashMap.size(): " + concurrentHashMap.size());
		System.out.println("concurrentHashMap.mappingCount(): " + concurrentHashMap.mappingCount());
		System.out.println("concurrentHashMap.isEmpty(): " + concurrentHashMap.isEmpty());
		System.out.println("concurrentHashMap.containsKey(10): " + concurrentHashMap.containsKey(10));
		System.out.println("concurrentHashMap.containsKey(100): " + concurrentHashMap.containsKey(100));
		System.out.println("concurrentHashMap.containsValue(10): " + concurrentHashMap.containsValue(10));
		System.out.println("concurrentHashMap.containsValue(100): " + concurrentHashMap.containsValue(100));
		System.out.println("concurrentHashMap.contains(10): " + concurrentHashMap.contains(10));
		System.out.println("concurrentHashMap.contains(100): " + concurrentHashMap.contains(100));
		System.out.println("====================================");
		System.out.println("now concurrentHashMap: " + concurrentHashMap);
		System.out.println("concurrentHashMap.merge(1, 11, (key, value) -> value)：" + concurrentHashMap.merge(1, 11, (key, value) -> value));
		System.out.println("now concurrentHashMap: " + concurrentHashMap);
		System.out.println("concurrentHashMap.merge(2, 12, (key, value) -> value)：" + concurrentHashMap.merge(2, 12, (key, value) -> null));
		System.out.println("now concurrentHashMap: " + concurrentHashMap);
		System.out.println("concurrentHashMap.merge(13, 13, (key, value) -> value)：" + concurrentHashMap.merge(13, 13, (key, value) -> value));
		System.out.println("now concurrentHashMap: " + concurrentHashMap);
		System.out.println("concurrentHashMap.merge(14, 14, (key, value) -> value)：" + concurrentHashMap.merge(14, 14, (key, value) -> null));
		System.out.println("now concurrentHashMap: " + concurrentHashMap);
		System.out.println("====================================");
	}
}
