package com.nps.concurrent;

/**
 * Abstract class implementing an Erlang actor.  When a message arrives,
 * the actor queues itself up for access to a thread pool.
 * 
 * @author John Lindal
 */
abstract class ThreadPoolActor
	extends ThreadPoolActorBase
{
	private ActorThreadPool	itsActorPool;

	protected ThreadPoolActor(
		ActorThreadPool	pool)
	{
		itsActorPool = pool;
		itsActorPool.add(this);
	}

	/**
	 * Unregister this actor from the system.
	 */
	protected final void die()
	{
		super.die();

		itsActorPool.remove(this);
	}

	/**
	 * Notify the actor that it has received a message.
	 */
	protected final void notifyMessageAvailable()
	{
		if (isAlive())
		{
			itsActorPool.queue(this);
		}
	}
}
