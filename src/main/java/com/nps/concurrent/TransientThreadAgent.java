package com.nps.concurrent;

/**
 * Abstract class implementing an Erlang actor.  When a message arrives,
 * the actor queues itself up for access to a thread pool.
 * 
 * @author John Lindal
 */
/* package */ abstract class TransientThreadAgent
	extends Agent
{
	private boolean itsRetiredFlag = false;
	private Object	itsRunningLock = new Object();	// can't use itsRunningFlag, because it changes
	private Boolean itsRunningFlag = Boolean.FALSE;

	/**
	 * @return	true if the actor is still alive
	 */
	public boolean isRetired()
	{
		return itsRetiredFlag;
	}

	/**
	 * Derived classes must unregister this actor from the system.
	 * 
	 * Only act() can call retire(), so itsRunningFlag is set, so we don't
	 * need to synchronize with other threads.
	 */
	protected void retire()
	{
		itsRetiredFlag = true;
	}

	/**
	 * Process messages.
	 */
	public final void run()
	{
		boolean hasMessage = false;
		synchronized (itsRunningLock)
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
			act(next());

			synchronized (itsRunningLock)
			{
				hasMessage = (!itsRetiredFlag ? hasPendingMessages() : false);

				// If retire() was called, leave itsRunningFlag set to
				// short circuit all pending calls.  No new calls will be
				// queued since retire() must remove us from the actor
				// pool.

				if (!itsRetiredFlag && !hasMessage)
				{
					itsRunningFlag = Boolean.FALSE;
				}
			}
		}
	}
}
