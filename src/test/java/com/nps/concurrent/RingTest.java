package com.nps.concurrent;

import java.util.Date;

import java.util.concurrent.TimeUnit;

public class RingTest
	extends junit.framework.TestCase
{
	private final int ACTOR_COUNT = 503;

	public void testPersistentThreadRing()
	{
		System.out.println("PersistentThreadActorExecution");

		RingActor a[] = new RingActor[ACTOR_COUNT];
		for (int i=0; i<ACTOR_COUNT; i++)
		{
			a[i] = new RingActor(new PersistentThreadActorExecution(), i+1);
		}

		ring(a, 1000000);
	}

	public void testTransientThreadRing()
	{
		System.out.println("JITThreadActorExecution");

		RingActor a[] = new RingActor[ACTOR_COUNT];
		for (int i=0; i<ACTOR_COUNT; i++)
		{
			a[i] = new RingActor(new JITThreadActorExecution(), i+1);
		}

		ring(a, 10000);
	}

	public void testThreadPoolRing()
	{
		System.out.println("ThreadPoolActorExecution");

		ActorThreadPool pool = new ActorThreadPool(25, 100, 1, TimeUnit.SECONDS);

		RingActor a[] = new RingActor[ACTOR_COUNT];
		for (int i=0; i<ACTOR_COUNT; i++)
		{
			a[i] = new RingActor(new ThreadPoolActorExecution(pool), i+1);
		}

		ring(a, 1000000);
	}

	private void ring(
		RingActor[]	a,
		long		timeToLive)
	{
		for (int i=0; i<ACTOR_COUNT; i++)
		{
			a[i].setNext(a[ (i+1) % ACTOR_COUNT ]);
		}

		Date t1 = new Date();

		RingMessage msg = new RingMessage(timeToLive);
		a[0].recv(msg);

		while (true)
		{
			synchronized (msg)
			{
				try
				{
					msg.wait();
				}
				catch (InterruptedException ex)
				{
				}

				if (msg.remaining() <= 0)
				{
					break;
				}
			}
		}

		Date t2 = new Date();
		System.out.println("Time elapsed: " + (t2.getTime() - t1.getTime())/1000.0 + " sec");
	}
}
