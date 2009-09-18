package com.nps.concurrent;

class RingActor
	extends Actor
{
	private long	itsIndex;
	private Actor	itsNextActor;

	public RingActor(
		ActorExecution	exec,
		long			index)
	{
		super(exec);
		itsIndex = index;
	}

	public void setNext(
		Actor a)
	{
		itsNextActor = a;
	}

	protected void process(
		Object msg)
	{
		RingMessage rmsg = (RingMessage) msg;

		long count = rmsg.decrement();
		if (count > 0)
		{
			if (count % 10000 == 0)
			{
				System.out.println(count);
			}
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
