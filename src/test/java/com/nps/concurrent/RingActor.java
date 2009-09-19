package com.nps.concurrent;

/**
 * An actor who passes a single message around a ring.
 * 
 * @author John Lindal
 */
class RingActor
	extends Actor
{
	private long	itsIndex;
	private Actor	itsNextActor;

	public RingActor(
		Agent	agent,
		long	index)
	{
		super(agent);
		itsIndex = index;
	}

	public void setNext(
		Actor a)
	{
		itsNextActor = a;
	}

	protected void act(
		Object msg)
	{
		TTLMessage rmsg = (TTLMessage) msg;

		long count = rmsg.decrement();
		if (count > 0)
		{
			itsNextActor.recv(msg);
		}
		else
		{
			System.out.println(itsIndex);
			synchronized (msg)
			{
				msg.notify();
			}
		}
	}
}
