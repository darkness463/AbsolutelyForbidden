/**
 * 
 */
package com.darkness463.absolutelyforbidden.common.concurrent;

/**
 * @author chenyanmin
 * @time 2014-2-16 上午12:24:15
 */
public interface Priority {
	public static final int PRIORITY_TRIVIAL = 100;
	public static final int PRIORITY_MINOR = 200;
	public static final int PRIORITY_MAJOR = 300;
	public static final int PRIORITY_CRITICAL = 400;
	public static final int PRIORITY_BLOCKER = 500;
	
	public static final int PRIORITY_DEFAULT = PRIORITY_MAJOR;
	
	int getPriority();
}
