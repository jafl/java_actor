package com.nps.concurrent;

/**
 * Abstract class implementing an Erlang actor.  When a message arrives,
 * the actor queues itself up for access to a thread pool.
 * 
 * @author John Lindal
 */
public class ThreadPoolActorExecution
	extends TransientThreadActorExecution
{
	private ActorThreadPool	itsThreadPool;

	public ThreadPoolActorExecution(
		ActorThreadPool	pool)
	{
		itsThreadPool = pool;
	}

	/**
	 * Duplicate this execution context for use by another actor.
	 */
	/* package */ ActorExecution dup()
	{
		return new ThreadPoolActorExecution(itsThreadPool);
	}

	/**
	 * Finalize this function.
	 */
	@Override
	protected final void die()
	{
		super.die();
	}

	/**
	 * Notify the actor that it has received a message.
	 */
	protected final void notifyMessageAvailable()
	{
		if (isAlive())
		{
			itsThreadPool.execute(this);
		}
	}
}
