/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package indi.atlantis.framework.tridenter.multiprocess;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.springdessert.reditools.common.SharedLatch;
import com.github.paganini2008.springdessert.reditools.messager.RedisMessageSender;

import indi.atlantis.framework.tridenter.multicast.ApplicationMulticastGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ProcessPoolExecutor
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
public class ProcessPoolExecutor implements ProcessPool {

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.application.cluster.multiprocess.latch.timeout:-1}")
	private int timeout;

	@Autowired
	private SharedLatch sharedLatch;

	@Autowired
	private ApplicationMulticastGroup applicationMulticastGroup;

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Autowired
	private DelayQueue delayQueue;

	private final AtomicBoolean running = new AtomicBoolean(true);

	@Override
	public void execute(Invocation invocation) {
		checkIfRunning();
		boolean acquired = timeout > 0 ? sharedLatch.acquire(timeout, TimeUnit.SECONDS) : sharedLatch.acquire();
		if (acquired) {
			if (log.isTraceEnabled()) {
				log.trace("Now processPool's concurrency is " + sharedLatch.cons());
			}
			applicationMulticastGroup.unicast(applicationName, ProcessPoolTaskListener.class.getName(), invocation);
		} else {
			delayQueue.offer(invocation);
			log.info("Invocation: {} go into the pending queue.", invocation);
		}

	}

	@Override
	public TaskPromise submit(Invocation invocation) {
		checkIfRunning();
		boolean acquired = timeout > 0 ? sharedLatch.acquire(timeout, TimeUnit.SECONDS) : sharedLatch.acquire();
		if (acquired) {
			if (log.isTraceEnabled()) {
				log.trace("Now processPool's concurrency is " + sharedLatch.cons());
			}
			applicationMulticastGroup.unicast(applicationName, ProcessPoolTaskListener.class.getName(), invocation);
		} else {
			delayQueue.offer(invocation);
			log.info("Invocation: {} go into the pending queue.", invocation);
		}
		MultiProcessingTaskPromise promise = new MultiProcessingTaskPromise(invocation.getId());
		redisMessageSender.subscribeChannel(invocation.getId(), promise);
		return promise;
	}

	private void checkIfRunning() {
		if (!running.get()) {
			throw new IllegalStateException("ProcessPool is shutdown now.");
		}
	}

	@Override
	public int getQueueSize() {
		return delayQueue.size();
	}

	@Override
	public void shutdown() {
		if (!running.get()) {
			return;
		}
		running.set(false);
		delayQueue.waitForTermination();
		sharedLatch.join();
	}

}
