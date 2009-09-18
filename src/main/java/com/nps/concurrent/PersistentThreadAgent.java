package com.nps.concurrent;

/**
 * Abstract class implementing an Erlang actor.  Each actor runs in a
 * separate, persistent thread.
 * 
 * @author John Lindal
 */
public class PersistentThreadAgent
	extends Agent
{
	private boolean	itsRetiredFlag = false;

	public PersistentThreadAgent()
	{
		new Thread(this).start();
	}

	/**
	 * Duplicate this agent for use by another actor.
	 */
	/* package */ Agent dup()
	{
		return new PersistentThreadAgent();
	}

	/**
	 * Unregister this actor with the system.
	 */
	protected final void retire()
	{
		itsRetiredFlag = true;
	}

	/**
	 * Process messages.
	 */
	public final void run()
	{
		while (!itsRetiredFlag)
		{
			waitForMessage();

			// Since the public API only allows adding messages, this
			// doesn't need to be synchronized between hasPendingMessages()
			// and next(), because the number of messages can only
			// increase.

			while (!itsRetiredFlag && hasPendingMessages())
			{
				act(next());
			}
		}
	}

	/**
	 * Wait for a message to arrive.
	 */
	private final void waitForMessage()
	{
		synchronized (itsMessageQueue)
		{
			if (!hasPendingMessages())
			{
				try
				{
					itsMessageQueue.wait();
				}
				catch (InterruptedException ex)
				{
				}
			}
		}
	}

	/**
	 * Notify the actor that it has received a message.
	 */
	protected final void notifyMessageAvailable()
	{
		synchronized (itsMessageQueue)
		{
			itsMessageQueue.notify();
		}
	}
}
