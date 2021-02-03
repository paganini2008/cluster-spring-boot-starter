package org.springtribe.framework.cluster.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springtribe.framework.cluster.http.RoutingAllocator;

/**
 * 
 * DefaultRouterCustomizer
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class DefaultRouterCustomizer implements RouterCustomizer {

	@Value("${spring.application.name}")
	private String applicationName;

	@Override
	public void customize(RouterManager rm) {
		rm.route("/application/cluster/**").provider(RoutingAllocator.ALL);
		rm.route("/**").provider(applicationName).timeout(60);
	}

}
