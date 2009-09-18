package com.nps.concurrent;

/**
 * Abstract class implementing an Erlang actor.  When a message arrives,
 * the actor starts a thread to process the message.
 * 
 * @author John Lindal
 */
public class JITThreadActorExecution
	extends TransientThreadActorExecution
{
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
			new Thread(this).start();
		}
	}
}
