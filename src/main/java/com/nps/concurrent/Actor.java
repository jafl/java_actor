package com.nps.concurrent;

/**
 * Abstract base class for implementing an Erlang actor.  Each actor runs
 * in a separate thread, but how actors are mapped to threads is left up to
 * the agent.
 * 
 * Derived classes must implement <code>act()</code> to respond to messages
 * and can optionally install a <code>MessageFilter</code> into the
 * agent to filter messages.
 * 
 * @author John Lindal
 */
public abstract class Actor
{
	private Agent	itsAgent;

	protected Actor(
		Agent agent)
	{
		if (agent.hasActor())
		{
			itsAgent = agent.dup();			
		}
		else
		{
			itsAgent = agent;
		}

		itsAgent.setActor(this);
	}

	/**
	 * Returns true if this actor has unprocessed messages.
	 * 
	 * @return	true if this actor has unprocessed messages
	 */
	public final boolean hasPendingMessages()
	{
		return itsAgent.hasPendingMessages();
	}

	/**
	 * Receive a message.
	 * 
	 * @param msg				the message to receive
	 * @throws InvalidMessage	if the pre-filter rejects the message
	 */
	public final void recv(
		Object  msg)
	{
		itsAgent.recv(msg);
	}

	/**
	 * Retrieve the next message in the queue.
	 * 
	 * @return	the next message
	 */
	protected final Object next()
	{
		return itsAgent.next();
	}

	/**
	 * Retrieve the next message of the specified type.
	 * 
	 * @return	the next message of the specified type or null if no such message
	 */
	protected final Object next(
		Class clazz)
	{
		return itsAgent.next(clazz);
	}

	/**
	 * Retrieve the first message matching the specified filter.
	 * 
	 * @return	the first matching message or null if no such message
	 */
	protected final Object next(
		MessageFilter f)
	{
		return itsAgent.next(f);
	}

	/**
	 * Unregister this actor with the thread management system.
	 */
	protected void retire()
	{
		itsAgent.retire();
	}

	/**
	 * Process a message.  This function is allowed to call next() to
	 * attempt to retrieve additional messages.
	 * 
	 * @param msg	the message
	 */
	protected abstract void act(Object msg);
}
