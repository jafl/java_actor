package com.nps.concurrent;

/**
 * Abstract class implementing an Erlang actor.  When a message arrives,
 * the actor queues itself up for access to a thread pool.
 * 
 * @author John Lindal
 */
public class ThreadPoolAgent
	extends TransientThreadAgent
{
	private ActorThreadPool	itsThreadPool;

	public ThreadPoolAgent(
		ActorThreadPool	pool)
	{
		itsThreadPool = pool;
	}

	/**
	 * Duplicate this agent for use by another actor.
	 */
	/* package */ Agent dup()
	{
		return new ThreadPoolAgent(itsThreadPool);
	}

	/**
	 * Finalize this function.
	 */
	@Override
	protected final void retire()
	{
		super.retire();
	}

	/**
	 * Notify the actor that it has received a message.
	 */
	protected final void notifyMessageAvailable()
	{
		if (!isRetired())
		{
			itsThreadPool.execute(this);
		}
	}
}
