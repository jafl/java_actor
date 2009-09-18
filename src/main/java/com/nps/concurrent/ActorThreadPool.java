package com.nps.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A thread pool for executing ThreadPoolActors.
 * 
 * @author John Lindal
 */
public class ActorThreadPool
	extends java.util.concurrent.ThreadPoolExecutor
{
	private int	itsPriority = Thread.NORM_PRIORITY;

	public ActorThreadPool(
		int			corePoolSize,
		int			maximumPoolSize,
		long		keepAliveTime,
		TimeUnit	unit)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit,
			  new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * Get the priority of all threads in this pool.
	 * 
	 * @return	thread priority
	 */
	public int getPriority()
	{
		return itsPriority;
	}

	/**
	 * Set the priority of all new thread executions.
	 * 
	 * @param priority	new thread priority
	 */
	public void setPriority(
		int priority)
	{
		itsPriority = priority;
	}

	/**
	 * Set the thread priority before it executes.
	 * 
	 * @param t	thread that will run
	 * @param r runnable that will execute
	 */
	@Override
	protected void beforeExecute(
		Thread		t,
		Runnable	r)
	{
		t.setPriority(itsPriority);
		super.beforeExecute(t, r);
	}
}
