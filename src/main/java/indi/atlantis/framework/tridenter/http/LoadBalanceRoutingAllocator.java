package indi.atlantis.framework.tridenter.http;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.StringUtils;

import indi.atlantis.framework.tridenter.ApplicationInfo;
import indi.atlantis.framework.tridenter.LoadBalancer;
import indi.atlantis.framework.tridenter.election.ApplicationClusterRefreshedEvent;
import indi.atlantis.framework.tridenter.election.LeaderNotFoundException;
import indi.atlantis.framework.tridenter.multicast.RegistryCenter;

/**
 * 
 * LoadBalanceRoutingAllocator
 *
 * @author Jimmy Hoff
 * 
 * @since 1.0
 */
public class LoadBalanceRoutingAllocator implements RoutingAllocator, ApplicationListener<ApplicationClusterRefreshedEvent> {

	@Autowired
	private RegistryCenter registryCenter;

	@Qualifier("applicationClusterLoadBalancer")
	@Autowired
	private LoadBalancer loadBalancer;

	private ApplicationInfo leaderInfo;

	@Override
	public String allocateHost(String provider, String path, Request request) {
		if (StringUtils.isBlank(provider)) {
			return path;
		}
		ApplicationInfo selectedApplication = null;
		List<ApplicationInfo> candidates = null;
		switch (provider) {
		case LEADER:
			if (leaderInfo == null) {
				throw new LeaderNotFoundException(LEADER);
			}
			selectedApplication = leaderInfo;
			break;
		case ALL:
			candidates = registryCenter.getApplications();
			selectedApplication = loadBalancer.select(path, candidates);
			break;
		default:
			candidates = registryCenter.getApplications(provider);
			selectedApplication = loadBalancer.select(path, candidates);
			break;
		}
		if (selectedApplication == null) {
			throw new RoutingPolicyException("Invalid provider name: " + provider);
		}
		return selectedApplication.getApplicationContextPath() + path;
	}

	@Override
	public void onApplicationEvent(ApplicationClusterRefreshedEvent event) {
		this.leaderInfo = event.getLeaderInfo();
	}

}