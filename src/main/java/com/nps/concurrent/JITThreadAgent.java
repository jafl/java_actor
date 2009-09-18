package com.nps.concurrent;

/**
 * Abstract class implementing an Erlang actor.  When a message arrives,
 * the actor starts a thread to process the message.
 * 
 * @author John Lindal
 */
public class JITThreadAgent
	extends TransientThreadAgent
{
	/**
	 * Duplicate this execution context for use by another actor.
	 */
	/* package */ Agent dup()
	{
		return new JITThreadAgent();
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
			new Thread(this).start();
		}
	}
}
