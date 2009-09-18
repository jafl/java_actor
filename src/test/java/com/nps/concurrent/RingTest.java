package com.nps.concurrent;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Unit tests for messages being passed around a ring.
 * 
 * @author John Lindal
 */
public class RingTest
	extends junit.framework.TestCase
{
	private final int ACTOR_COUNT = 503;

	public void testPersistentThreads()
	{
		System.out.println("PersistentThreadAgent");

//		ring(new PersistentThreadAgent(), 1000000);
	}

	public void testTransientThreads()
	{
		System.out.println("JITThreadAgent");

//		ring(new JITThreadAgent(), 10000);
	}

	public void testThreadPools()
	{
		System.out.println("ThreadPoolAgent (100)");

		ActorThreadPool pool = new ActorThreadPool(25, 100, 1, TimeUnit.SECONDS);
//		ring(new ThreadPoolAgent(pool), 1000000);

		System.out.println("ThreadPoolAgent (10)");

		pool = new ActorThreadPool(1, 10, 1, TimeUnit.SECONDS);
//		ring(new ThreadPoolAgent(pool), 1000000);
	}

	private void ring(
		Agent	agent,
		long	timeToLive)
	{
		RingActor a[] = new RingActor[ACTOR_COUNT];
		for (int i=0; i<ACTOR_COUNT; i++)
		{
			a[i] = new RingActor(agent, i+1);
		}

		for (int i=0; i<ACTOR_COUNT; i++)
		{
			a[i].setNext(a[ (i+1) % ACTOR_COUNT ]);
		}

		Date t1 = new Date();

		TTLMessage msg = new TTLMessage(timeToLive);
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
