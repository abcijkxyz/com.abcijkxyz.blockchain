package com.abcijkxyz.blockchain.test;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeTest {
	public static void main(String[] args) throws InterruptedException {
//		Vector<E>
//		ConcurrentMap<Integer, Integer> hashMap = new ConcurrentHashMap<>();
		Map<Integer, Integer> hashMap =  new ConcurrentHashMap<>();
//		Map<Integer, Integer> hashMap =  Collections.synchronizedMap(new HashMap<>());
//		Map<Integer, Integer> hashMap = new HashMap<>();
		CountDownLatch countDownLatch = new CountDownLatch(10);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 100, 0L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

		for (int i = 1; i <= 10; i++) {
			int finalI = i;
			threadPoolExecutor.execute(() -> {
				for (int j = 1; j <= 10; j++) {
					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					hashMap.put(finalI * 10 + j, j);
				}
				;
				countDownLatch.countDown();
			});
		}
		countDownLatch.await(); 
		threadPoolExecutor.shutdown();
		System.out.println("hashMap.size(): " + hashMap.size());
		AtomicInteger size = new AtomicInteger();
		hashMap.forEach((key, value) -> {
			System.out.print(key + " = " + value + "; \t");
			if (size.incrementAndGet() >= 10) {
				size.set(0);
				System.out.println();
			}
		});
	}
}
