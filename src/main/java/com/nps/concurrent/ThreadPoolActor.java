package com.nps.concurrent;

/**
 * Abstract class implementing an Erlang actor.  Each actor runs in a
 * separate, transient thread.
 * 
 * @author John Lindal
 */
abstract class ThreadPoolActor
	extends ActorBase
{
	private ActorThreadPool	itsThreadPool;

	protected ThreadPoolActor(
		ActorThreadPool	pool)
	{
		itsThreadPool = pool;
		itsThreadPool.add(this);
	}

	/**
	 * Unregister this actor with the system.
	 */
	protected final void die()
	{
		itsThreadPool.remove(this);
	}

	/**
	 * Process messages.
	 */
	public final void run()
	{
		// Since the public API only allows adding messages, this
		// doesn't need to be synchronized between hasPendingMessages()
		// and next(), because the number of messages can only
		// increase.

		while (hasPendingMessages())
		{
			if (!process(next()))
			{
				break;
			}
		}
	}

	/**
	 * Notify the actor that it has received a message.
	 */
	protected final void notifyMessageAvailable()
	{
		itsThreadPool.queue(this);
	}
}
