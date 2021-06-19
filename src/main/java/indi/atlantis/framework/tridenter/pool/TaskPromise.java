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
package indi.atlantis.framework.tridenter.pool;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 
 * TaskPromise
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface TaskPromise {

	Object get(Supplier<Object> defaultValue);

	Object get(long timeout, TimeUnit timeUnit, Supplier<Object> defaultValue);

	void cancel();

	boolean isCancelled();

	boolean isDone();

}
