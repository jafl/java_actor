package com.nps.concurrent;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Abstract class implementing an Erlang actor.
 * 
 * Derived classes must implement <code>process()</code> to process
 * messages and can optionally install a <code>MessageFilter</code> to
 * pre-filter messages.
 * 
 * @author John Lindal
 */
abstract class Actor
	implements Runnable
{
	private Thread			itsThread;
	private List<Object>	itsMessageQueue;
	private MessageFilter	itsPrefilter;

	protected Actor()
	{
		itsMessageQueue = new LinkedList<Object>();

		itsThread = new Thread(this);
		itsThread.start();
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
			itsMessageQueue.notify();
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
	 * Process messages.
	 */
	public final void run()
	{
		while (true)
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

			// Since the public API only allows adding messages, this
			// doesn't need to be synchronized, because the number of
			// messages can only increase.

			while (hasPendingMessages())
			{
				if (!process(next()))
				{
					break;
				}
			}
		}
	}

	/**
	 * Process a message.  This function is allowed to call get() to
	 * attempt to retrieve additional messages.
	 * 
	 * @param msg	the message
	 * @return		false to terminate the actor
	 */
	abstract protected boolean process(Object msg);
}
