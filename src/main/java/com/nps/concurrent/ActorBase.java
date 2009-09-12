package com.nps.concurrent;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Abstract base class for implementing an Erlang actor.  Each actor runs
 * in a separate thread, but how actors are mapped to threads is left up to
 * the derived class:  implement <code>register()</code>,
 * <code>notifyMessageAvailable()</code>, and <code>run()</code>.
 * 
 * Concrete classes must implement <code>process()</code> to process
 * messages and can optionally install a <code>MessageFilter</code> to
 * pre-filter messages.
 * 
 * @author John Lindal
 */
abstract class SimpleActor
	implements Runnable
{
	private List<Object>	itsMessageQueue;
	private MessageFilter	itsPrefilter;

	protected SimpleActor()
	{
		itsMessageQueue = new LinkedList<Object>();

		register();
	}

	protected interface MessageFilter
	{
		/**
		 * This is useful if static rules, e.g., class type, determine what
		 * messages are accepted.  Dynamic rules, e.g., B only accepted after
		 * A, must be implemented in process().
		 * 
		 * @param msg	the message
		 * @return		true if the message is acceptable
		 */
		public boolean acceptMessage(Object msg);
	}

	/**
	 * Returns true if this actor has unprocessed messages.
	 * 
	 * @return	true if this actor has unprocessed messages
	 */
	public final boolean hasPendingMessages()
	{
		synchronized (itsMessageQueue)
		{
			return (itsMessageQueue.size() > 0);
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
		if (itsPrefilter != null && !itsPrefilter.acceptMessage(msg))
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
	protected final Object next()
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
	protected final Object get(
		final Class clazz)
	{
		return get(new MessageFilter()
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
	protected final Object get(
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
	 * Register this actor with the system.
	 */
	abstract protected void register();

	/**
	 * Notify the actor that it has received a message.
	 */
	abstract protected void notifyMessageAvailable();

	/**
	 * Process a message.  This function is allowed to call get() to
	 * attempt to retrieve additional messages.
	 * 
	 * @param msg	the message
	 * @return		false to terminate the actor
	 */
	abstract protected boolean process(Object msg);
}
