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
	private ActorExecution	itsExecution;

	protected Actor(
		ActorExecution exec)
	{
		itsExecution = exec;
		itsExecution.setActor(this);
	}

	/**
	 * Returns true if this actor has unprocessed messages.
	 * 
	 * @return	true if this actor has unprocessed messages
	 */
	public final boolean hasPendingMessages()
	{
		return itsExecution.hasPendingMessages();
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
		itsExecution.recv(msg);
	}

	/**
	 * Retrieve the next message in the queue.
	 * 
	 * @return	the next message
	 */
	protected final Object next()
	{
		return itsExecution.next();
	}

	/**
	 * Retrieve the next message of the specified type.
	 * 
	 * @return	the next message of the specified type or null if no such message
	 */
	protected final Object next(
		Class clazz)
	{
		return itsExecution.next(clazz);
	}

	/**
	 * Retrieve the first message matching the specified filter.
	 * 
	 * @return	the first matching message or null if no such message
	 */
	protected final Object next(
		MessageFilter f)
	{
		return itsExecution.next(f);
	}

	/**
	 * Unregister this actor with the thread management system.
	 */
	protected void die()
	{
		itsExecution.die();
	}

	/**
	 * Process a message.  This function is allowed to call next() to
	 * attempt to retrieve additional messages.
	 * 
	 * @param msg	the message
	 */
	abstract protected void process(Object msg);
}
