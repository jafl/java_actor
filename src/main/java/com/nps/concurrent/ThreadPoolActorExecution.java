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
	 * Finalize this function.
	 */
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
