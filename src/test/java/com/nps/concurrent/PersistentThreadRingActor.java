package com.nps.concurrent;

public class PersistentThreadRingActor
	extends PersistentThreadActor
{
	private long					itsIndex;
	private PersistentThreadActor	itsNextActor;

	public PersistentThreadRingActor(
		long index)
	{
		itsIndex = index;
	}

	public void setNext(
		PersistentThreadActor a)
	{
		itsNextActor = a;
	}

	protected boolean process(
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

		return true;
	}
}
