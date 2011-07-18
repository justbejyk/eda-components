/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nereuschen.eda.channel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.nereuschen.eda.Message;
import com.nereuschen.eda.core.MessageHandler;
import com.nereuschen.eda.dispatcher.RoundRobinLoadBalancingStrategy;
import com.nereuschen.eda.message.GenericMessage;

/**
 * @author Mark Fisher
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public class ExecutorChannelTests {

	@Test
	public void verifyDifferentThread() throws Exception {
		Executor taskExecutor = Executors.newFixedThreadPool(2);
		ExecutorChannel channel = new ExecutorChannel(taskExecutor);
		CountDownLatch latch = new CountDownLatch(1);
		TestHandler handler = new TestHandler(latch);
		channel.subscribe(handler);
		channel.send(new GenericMessage<String>("test"));
		latch.await(1000, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertNotNull(handler.thread);
		assertFalse(Thread.currentThread().equals(handler.thread));
	}

	@Test
	public void roundRobinLoadBalancing() throws Exception {
		int numberOfMessages = 11;
		Executor taskExecutor = Executors.newFixedThreadPool(2);
		ExecutorChannel channel = new ExecutorChannel(taskExecutor,
				new RoundRobinLoadBalancingStrategy());
		CountDownLatch latch = new CountDownLatch(numberOfMessages);
		TestHandler handler1 = new TestHandler(latch);
		TestHandler handler2 = new TestHandler(latch);
		TestHandler handler3 = new TestHandler(latch);
		channel.subscribe(handler1);
		channel.subscribe(handler2);
		channel.subscribe(handler3);
		for (int i = 0; i < numberOfMessages; i++) {
			channel.send(new GenericMessage<String>("test-" + i));
		}
		latch.await(3000, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertNotNull(handler1.thread);
		assertFalse(Thread.currentThread().equals(handler1.thread));
		assertNotNull(handler2.thread);
		assertFalse(Thread.currentThread().equals(handler2.thread));
		assertNotNull(handler3.thread);
		assertFalse(Thread.currentThread().equals(handler3.thread));
		assertEquals(4, handler1.count.get());
		assertEquals(4, handler2.count.get());
		assertEquals(3, handler3.count.get());
	}

	@Test
	public void verifyFailoverWithLoadBalancing() throws Exception {
		int numberOfMessages = 11;
		Executor taskExecutor = Executors.newFixedThreadPool(2);
		ExecutorChannel channel = new ExecutorChannel(taskExecutor,
				new RoundRobinLoadBalancingStrategy());
		CountDownLatch latch = new CountDownLatch(numberOfMessages);
		TestHandler handler1 = new TestHandler(latch);
		TestHandler handler2 = new TestHandler(latch);
		TestHandler handler3 = new TestHandler(latch);
		channel.subscribe(handler1);
		channel.subscribe(handler2);
		channel.subscribe(handler3);
		handler2.shouldFail = true;
		for (int i = 0; i < numberOfMessages; i++) {
			channel.send(new GenericMessage<String>("test-" + i));
		}
		latch.await(3000, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertNotNull(handler1.thread);
		assertFalse(Thread.currentThread().equals(handler1.thread));
		assertNotNull(handler2.thread);
		assertFalse(Thread.currentThread().equals(handler2.thread));
		assertNotNull(handler3.thread);
		assertFalse(Thread.currentThread().equals(handler3.thread));
		assertEquals(0, handler2.count.get());
		assertEquals(4, handler1.count.get());
		assertEquals(7, handler3.count.get());
	}

	@Test
	public void verifyFailoverWithoutLoadBalancing() throws Exception {
		int numberOfMessages = 11;
		Executor taskExecutor = Executors.newFixedThreadPool(2);
		ExecutorChannel channel = new ExecutorChannel(taskExecutor, null);
		CountDownLatch latch = new CountDownLatch(numberOfMessages);
		TestHandler handler1 = new TestHandler(latch);
		TestHandler handler2 = new TestHandler(latch);
		TestHandler handler3 = new TestHandler(latch);
		channel.subscribe(handler1);
		channel.subscribe(handler2);
		channel.subscribe(handler3);
		handler1.shouldFail = true;
		for (int i = 0; i < numberOfMessages; i++) {
			channel.send(new GenericMessage<String>("test-" + i));
		}
		latch.await(3000, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertNotNull(handler1.thread);
		assertFalse(Thread.currentThread().equals(handler1.thread));
		assertNotNull(handler2.thread);
		assertFalse(Thread.currentThread().equals(handler2.thread));
		assertNull(handler3.thread);
		assertEquals(0, handler1.count.get());
		assertEquals(0, handler3.count.get());
		assertEquals(numberOfMessages, handler2.count.get());
	}

	private static class TestHandler implements MessageHandler {

		private final CountDownLatch latch;

		private final AtomicInteger count = new AtomicInteger();

		private volatile Thread thread;

		private volatile boolean shouldFail;

		public TestHandler(CountDownLatch latch) {
			this.latch = latch;
		}

		public void handleMessage(Message<?> message) {
			this.thread = Thread.currentThread();
			if (this.shouldFail) {
				throw new RuntimeException("intentional test failure");
			}
			this.count.incrementAndGet();
			this.latch.countDown();
		}
	}

}