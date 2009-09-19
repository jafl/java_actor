package com.nps.concurrent;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Abstract base class for implementing an Erlang actor.  Each actor runs
 * in a separate thread, but how actors are mapped to threads is left up to
 * the derived class:  implement <code>run()</code>, <code>retire()</code>,
 * <code>notifyMessageAvailable()</code>.
 * 
 * To be useful, an Actor must installed on which to call
 * <code>act()</code>, so messages will be processed.  The Actor can
 * optionally install a <code>MessageFilter</code> to filter messages.
 * 
 * @author John Lindal
 */
/* package */ abstract class Agent
	implements Runnable
{
	private static List<MessageSpy>	theMessageSpies;

	protected List<Object>	itsMessageQueue = new LinkedList<Object>();		// derived classes need to synchronize on this
	private MessageFilter	itsMessageFilter;
	private Actor			itsActor;

	/**
	 * Add a MessageSpy to watch the flow of messages.
	 * 
	 * @param spy	the spy to install
	 */
	public static void addMessageSpy(
		MessageSpy spy)
	{
		if (theMessageSpies == null)
		{
			theMessageSpies = new LinkedList<MessageSpy>();
		}

		synchronized (theMessageSpies)
		{
			theMessageSpies.add(spy);
		}
	}

	/**
	 * Remove all MessageSpy objects.
	 */
	public static void removeAllMessageSpies()
	{
		theMessageSpies = null;
	}

	/**
	 * Get the current filter which determines which messages to accept.
	 * 
	 * @return	the installed filter, or null if there is no filter
	 */
	public final MessageFilter getMessageFilter()
	{
		return itsMessageFilter;
	}

	/**
	 * Set the filter which determines which messages to accept.
	 * 
	 * @param filter	the filter to install
	 */
	public final void setMessageFilter(
		MessageFilter filter)
	{
		itsMessageFilter = filter;
	}

	/**
	 * Returns true if we have an actor and it has unprocessed messages.
	 * 
	 * @return	true if this actor has unprocessed messages
	 */
	public final boolean hasPendingMessages()
	{
		synchronized (itsMessageQueue)
		{
			return (itsActor != null && itsMessageQueue.size() > 0);
		}
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
		boolean accepted = (itsMessageFilter == null || itsMessageFilter.acceptMessage(msg));

		if (theMessageSpies != null)
		{
			synchronized (theMessageSpies)
			{
				for (MessageSpy spy : theMessageSpies)
				{
					spy.observeMessage(msg, accepted);
				}
			}
		}

		if (!accepted)
		{
			throw new InvalidMessage();
		}

		synchronized (itsMessageQueue)
		{
			itsMessageQueue.add(msg);
			notifyMessageAvailable();
		}
	}

	/**
	 * Retrieve the next message in the queue.
	 * 
	 * @return	the next message
	 */
	/* package */ final Object next()
	{
		Object msg;
		synchronized (itsMessageQueue)
		{
			msg = itsMessageQueue.remove(0);
		}

		return msg;
	}

	/**
	 * Retrieve the next message of the specified type.
	 * 
	 * @return	the next message of the specified type or null if no such message
	 */
	/* package */ final Object next(
		final Class clazz)
	{
		return next(new MessageFilter()
		{
			public boolean acceptMessage(
				Object msg)
			{
				return clazz.isInstance(msg);
			}
		});
	}

	/**
	 * Retrieve the first message matching the specified filter.
	 * 
	 * @return	the first matching message or null if no such message
	 */
	/* package */ final Object next(
		MessageFilter f)
	{
		synchronized (itsMessageQueue)
		{
			Iterator iter = itsMessageQueue.iterator();
			while (iter.hasNext())
			{
				Object msg = iter.next();
				if (f.acceptMessage(msg))
				{
					iter.remove();
					return msg;
				}
			}
		}

		return null;
	}

	/**
	 * Duplicate this agent for use by another actor.  This does not copy
	 * the message filter.
	 */
	abstract /* package */ Agent dup();

	/**
	 * Unregister this actor with the thread management system.
	 */
	abstract /* package */ void retire();

	/**
	 * Notify the thread management system that this actor has received a
	 * message.
	 */
	abstract protected void notifyMessageAvailable();

	/**
	 * @return	true if this agent already has an actor
	 */
	/* package */ final boolean hasActor()
	{
		return (itsActor != null);
	}

	/**
	 * Sets the actor.
	 * 
	 * @param actor	the actor to execute
	 */
	/* package */ final void setActor(
		Actor actor)
	{
		itsActor = actor;
	}

	/**
	 * Process a message.  This function is allowed to call next() to
	 * attempt to retrieve additional messages.  This function should never
	 * be called unless we have an actor.
	 * 
	 * @param msg	the message
	 */
	protected final void act(
		Object msg)
	{
		itsActor.act(msg);
	}
}
