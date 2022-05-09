package com.abcijkxyz.blockchain.deamon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class T {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int cpus = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = new ThreadPoolExecutor(cpus * 10, cpus * 20, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardPolicy());

		long start = System.currentTimeMillis();
		for (int i = 0; i < 2000; i++) {

			threadPool.execute(() -> {
				try {
					Thread.sleep(150);
					System.out.println(2000 - (System.currentTimeMillis() - start));
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			});

		}

		threadPool.shutdown();

	}

}
