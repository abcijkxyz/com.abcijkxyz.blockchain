package com.abcijkxyz.blockchain.test;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;

public class ConcurrentHashMapTest {
	public static void main(String[] args) {

		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
		// 添加元素
		map.put("语文", 92);
		map.put("数学", 95);
		map.put("英语", 100);
		map.put("生物", 85);
		map.put("化学", 95);
		map.put("物理", 85);

		// 遍历方法一：entrySet
		System.out.println("————————entrySet方式遍历————————");
		Set<Entry<String, Integer>> entrySet = map.entrySet();
		for (Entry<String, Integer> entry : entrySet) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
		// 键值相同则替代value，并返回原来的Value
		map.put("数学", 110);
		// 存入键相同的键值对相当于替换
		map.replace("语文", 112);
		map.replace("物理", 85, 98);// 使用新值替换老值
		// 存储键和值都是null值的元素

		// 删除
		map.remove("生物");// 直接通过键来删除映射关系
		map.remove("化学", 95);// 通过完整的键值对删除映射关系
		map.merge("语文", 1, Integer::sum);
		// 遍历方法二：KeySet
		System.out.println("————————KeySet方式遍历————————");
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			System.out.println(key + "\t" + map.get(key));
		}
	}
}
