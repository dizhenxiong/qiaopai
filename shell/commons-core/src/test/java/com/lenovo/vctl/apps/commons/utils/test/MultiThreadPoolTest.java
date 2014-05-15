package com.lenovo.vctl.apps.commons.utils.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;

import com.lenovo.vctl.apps.commons.utils.concurrent.NotifyingBlockingThreadPoolExecutor;

@Ignore
public class MultiThreadPoolTest {
	
	private static final Logger LOGGER = Logger.getLogger(MultiThreadPoolTest.class.getName());
	
	@Test
	public void testMultiThreadPool() {
		
		int taskCount = 200;
		int executorSize = 4;
		
		//-----------------------------------------------------------------
		// NotifyingBlockingThreadPoolExecutor initialization parameters
		//-----------------------------------------------------------------
		int poolSize = 4;
		int queueSize = 40; // recommended - twice the size of the poolSize
		int threadKeepAliveTime = 15;
		TimeUnit threadKeepAliveTimeUnit = TimeUnit.SECONDS;
		int maxBlockingTime = 1000;
		TimeUnit maxBlockingTimeUnit = TimeUnit.MILLISECONDS;
//		int maxBlockingTime = 1;
//		TimeUnit maxBlockingTimeUnit = TimeUnit.SECONDS;
		Callable<Boolean> blockingTimeoutCallback = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
//				LOGGER.info("*** Still waiting for task insertion... ***");
				return true; // keep waiting
			}
		};
		
		//threadPool list, size 4
		List<NotifyingBlockingThreadPoolExecutor> executors = new ArrayList<NotifyingBlockingThreadPoolExecutor>();
		for (int i=0; i<executorSize; i++) {
			NotifyingBlockingThreadPoolExecutor threadPoolExecutor =
				new NotifyingBlockingThreadPoolExecutor(poolSize, queueSize,
												threadKeepAliveTime, threadKeepAliveTimeUnit,
												maxBlockingTime, maxBlockingTimeUnit, blockingTimeoutCallback);
			executors.add(threadPoolExecutor);
		}
		
		//-----------------------------------------------------------------
		// Create the tasks for the NotifyingBlockingThreadPoolExecutor
		//-----------------------------------------------------------------
		for (int i = 0; i < taskCount; i++) {
			int index = i % executorSize;
			try {
				executors.get(index).execute(new MyNaiveTask("Task_" + (i+1)));
			} catch (Throwable e){
				LOGGER.info("==== ERROR : " + "An exception when sending task: " + (i+1));
				continue; // waive rejected or failed tasks
			}
			LOGGER.info("A task was sent successfully: " + (i+1) + ", to executor : " + index);
		}
		
		//-----------------------------------------------------------------
		// All tasks were sent...
		//-----------------------------------------------------------------
		LOGGER.info("Almost Done...");

		//-----------------------------------------------------------------
		// Wait for the last tasks to be done
		//-----------------------------------------------------------------
		try {
			for (int i=0; i<executorSize; i++) {
				LOGGER.info("i : " + i + " await().");
				executors.get(i).await();
			}
		} catch (Exception e) {
			LOGGER.info("==== ERROR : " + e.getMessage());
		}

		LOGGER.info("DONE!");
	}
	
	//================================================================
	// a static inner class for the example task we want to perform
	//================================================================
	static private class MyNaiveTask implements Runnable {  

		private static final Logger LOGGER = Logger.getLogger(MyNaiveTask.class.getName());

		private String name;

		public MyNaiveTask(String name) {
			this.name = name;
		}

		@Override
		public void run() {

			LOGGER.info(name + " --- STARTED");
			Random random = new Random();
			int millis = random.nextInt(51 * 100);
			try {
				Thread.sleep(millis);
				LOGGER.info(name + " --- FINISHED");
			}
			catch (InterruptedException e) {
				LOGGER.warning(name + " sleep was interrupted...");
			}            

		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	//================================================================
	
}
