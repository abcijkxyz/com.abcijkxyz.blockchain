package com.abcijkxyz.blockchain.test;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapmergeExample2 {
	public static void main(String[] args) {
		ConcurrentHashMap<Integer, String> conmap = new ConcurrentHashMap<Integer, String>();

		conmap.put(1, "A");
		conmap.put(2, "B");
		conmap.put(3, "C");
		conmap.put(5, "E");

		ConcurrentHashMap<Integer, String> conmap2 = new ConcurrentHashMap<Integer, String>();

		conmap2.put(1, "F");
		conmap2.put(2, "G");
		conmap2.put(3, "H");
		conmap2.put(5, "I");

		conmap2.forEach((key, value) -> conmap.merge(key, value, (v1, v2) -> v1.equalsIgnoreCase(v2) ? v1 : v1 + "," + v2));

		System.out.println(conmap);

	}
}