package org.springtribe.framework.cluster.pool;

/**
 * 
 * CallParallelization
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface Parallelization {

	Object[] slice(Object argument);

	Object merge(Object[] results);

}
