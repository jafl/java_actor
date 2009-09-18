package com.nps.concurrent;

/**
 * Abstract class implementing an Erlang actor.  When a message arrives,
 * the actor queues itself up for access to a thread pool.
 * 
 * @author John Lindal
 */
/* package */ abstract class TransientThreadActorExecution
	extends ActorExecution
{
	private boolean	itsAliveFlag   = true;
	private Boolean	itsRunningFlag = Boolean.FALSE;

	/**
	 * @return	true if the actor is still alive
	 */
	public boolean isAlive()
	{
		return itsAliveFlag;
	}

	/**
	 * Derived classes must unregister this actor from the system.
	 * 
	 * Only process() can call die(), so itsRunningFlag is set, so we don't
	 * need to synchronize with other threads.
	 */
	protected void die()
	{
		itsAliveFlag = false;
	}

	/**
	 * Process messages.
	 */
	public final void run()
	{
		boolean hasMessage = false;
		synchronized (itsRunningFlag)
		{
			if (itsRunningFlag)
			{
				return;
			}

			hasMessage = hasPendingMessages();
			if (!hasMessage)
			{
				return;
			}

			itsRunningFlag = Boolean.TRUE;
		}

		// Since the public API only allows adding messages, this
		// doesn't need to be synchronized between hasPendingMessages()
		// and next(), because the number of messages can only
		// increase.

		while (hasMessage)
		{
			process(next());

			synchronized (itsRunningFlag)
			{
				hasMessage = (itsAliveFlag ? hasPendingMessages() : false);

				// If die() was called, leave itsRunningFlag set to short
				// circuit all pending calls.  No new calls will be queued
				// since die() must remove us from the actor pool.

				if (itsAliveFlag && !hasMessage)
				{
					itsRunningFlag = Boolean.FALSE;
				}
			}
		}
	}
}
