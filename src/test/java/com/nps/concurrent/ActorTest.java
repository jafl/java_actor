package com.nps.concurrent;

public class ActorTest
	extends junit.framework.TestCase
{
	public void testRing()
	{
		RingActor a[] = new RingActor[503];
		for (int i=0; i<503; i++)
		{
			a[i] = new RingActor(i+1);
		}

		for (int i=0; i<503; i++)
		{
			a[i].setNext(a[ (i+1) % 503 ]);
		}

		RingMessage msg = new RingMessage(1000000);
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
