package com.kibou.juc;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.kibou.juc.task.IdentifiedTaskGroup.Builder;
import com.kibou.tuple.Pair;
import com.kibou.utils.RenderUtils;

public class JUCHelperTest {

	@Test
	public void testWaitUnInterruptibly() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(2);

		List<Pair<String, String>> invokeAndWait = new Builder<String, String>("taskgroup2", 3)
				.addTask("1", new Callable<String>() {
					public String call() throws InterruptedException {
						Thread.sleep(600);
						countDownLatch.countDown();
						return Thread.currentThread().getName();
					}
				}).addTask("2", new Callable<String>() {
					public String call() {
						countDownLatch.countDown();
						return Thread.currentThread().getName();
					}
				}).addTask("3", new Callable<String>() {// uncancellable task
					public String call() {
						JUCHelper.waitUnInterruptibly(700);
						countDownLatch.countDown();
						return Thread.currentThread().getName();
					}
				})//.timeout(500)
				// .executor(Executors.newFixedThreadPool(10,new
				// NamedThreadFactory("IdTaskThread", true)))
				.build().invokeAndWait();

		RenderUtils.printCollectionWithMultiline(invokeAndWait);

		countDownLatch.await();
	}
}
