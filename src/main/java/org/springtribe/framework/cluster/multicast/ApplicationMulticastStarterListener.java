package org.springtribe.framework.cluster.multicast;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springtribe.framework.cluster.ApplicationInfo;
import org.springtribe.framework.cluster.Constants;
import org.springtribe.framework.cluster.InstanceId;
import org.springtribe.framework.cluster.multicast.ApplicationMulticastEvent.MulticastEventType;
import org.springtribe.framework.reditools.messager.RedisMessageHandler;
import org.springtribe.framework.reditools.messager.RedisMessageSender;

/**
 * 
 * ApplicationMulticastStarterListener
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ApplicationMulticastStarterListener implements RedisMessageHandler, ApplicationContextAware {

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Autowired
	private ApplicationMulticastGroup applicationMulticastGroup;

	@Autowired
	private InstanceId instanceId;

	private ApplicationContext applicationContext;

	@Override
	public void onMessage(String channel, Object message) {
		final ApplicationInfo applicationInfo = (ApplicationInfo) message;
		if (!applicationMulticastGroup.hasRegistered(applicationInfo)) {
			applicationMulticastGroup.registerCandidate(applicationInfo);
			redisMessageSender.sendMessage(getChannel(), instanceId.getApplicationInfo());
			applicationContext
					.publishEvent(new ApplicationMulticastEvent(applicationContext, applicationInfo, MulticastEventType.ON_ACTIVE));
		}
	}

	@Override
	public String getChannel() {
		return Constants.APPLICATION_CLUSTER_NAMESPACE + clusterName + ":active";
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
