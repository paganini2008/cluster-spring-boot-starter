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

import java.io.Serializable;
import java.util.UUID;

import com.github.paganini2008.devtools.beans.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * MethodInvocation
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
public class MethodInvocation implements Serializable, Invocation {

	private static final long serialVersionUID = -5401293046063974728L;

	private String id;
	private Signature signature;
	private Object[] arguments;
	private long timestamp;

	MethodInvocation(Signature signature, Object... arguments) {
		this.id = UUID.randomUUID().toString();
		this.signature = signature;
		this.arguments = arguments;
		this.timestamp = System.currentTimeMillis();
	}

	public MethodInvocation() {
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
