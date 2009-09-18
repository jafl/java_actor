package com.nps.concurrent;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Abstract base class for implementing an Erlang actor.  Each actor runs
 * in a separate thread, but how actors are mapped to threads is left up to
 * the execution model.
 * 
 * Derived classes must implement <code>process()</code> to process
 * messages and can optionally install a <code>MessageFilter</code> into
 * the execution model to filter messages.
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
		throws  InvalidMessage
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
	protected void die()
	{
		itsAgent.die();
	}

	/**
	 * Process a message.  This function is allowed to call next() to
	 * attempt to retrieve additional messages.
	 * 
	 * @param msg	the message
	 */
	abstract protected void process(Object msg);
}
