package org.springtribe.framework.cluster.multicast;

import org.springframework.context.ApplicationContext;
import org.springtribe.framework.cluster.ApplicationClusterEvent;
import org.springtribe.framework.cluster.ApplicationInfo;
import org.springtribe.framework.cluster.HealthState;

/**
 * 
 * ApplicationMulticastEvent
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ApplicationMulticastEvent extends ApplicationClusterEvent {

	private static final long serialVersionUID = -2482108960259276628L;

	public ApplicationMulticastEvent(ApplicationContext source, ApplicationInfo applicationInfo, MulticastEventType eventType) {
		super(source, HealthState.UNLEADABLE);
		this.applicationInfo = applicationInfo;
		this.multicastEventType = eventType;
	}

	private final ApplicationInfo applicationInfo;
	private final MulticastEventType multicastEventType;
	private Object message;

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public ApplicationInfo getApplicationInfo() {
		return applicationInfo;
	}

	public MulticastEventType getMulticastEventType() {
		return multicastEventType;
	}

	public static enum MulticastEventType {
		ON_ACTIVE, ON_INACTIVE, ON_MESSAGE;
	}

}
