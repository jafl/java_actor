package com.nps.concurrent;

import java.util.concurrent.TimeUnit;

public class RingTest
	extends junit.framework.TestCase
{
	private final int ACTOR_COUNT = 503;

	public void testPersistentThreadRing()
	{
		System.out.println("PersistentThreadActorExecution");

		ring(new PersistentThreadActorExecution(), 1000000);
	}

	public void testTransientThreadRing()
	{
		System.out.println("JITThreadActorExecution");

		ring(new JITThreadActorExecution(), 10000);
	}

	public void testThreadPoolRing()
	{
		System.out.println("ThreadPoolActorExecution");

		ActorThreadPool pool = new ActorThreadPool(25, 100, 1, TimeUnit.SECONDS);
		ring(new ThreadPoolActorExecution(pool), 1000000);
	}

	private void ring(
		ActorExecution	exec,
		long			timeToLive)
	{
		RingActor a[] = new RingActor[ACTOR_COUNT];
		for (int i=0; i<ACTOR_COUNT; i++)
		{
			a[i] = new RingActor(exec, i+1);
		}

		for (int i=0; i<ACTOR_COUNT; i++)
		{
			a[i].setNext(a[ (i+1) % ACTOR_COUNT ]);
		}

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
	}
}
