package com.nps.concurrent;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An actor who throws messages around on a trading floor.
 * 
 * @author John Lindal
 */
class StockExchangeActor
	extends Actor
{
	private static List<Actor>	theActors         = new ArrayList<Actor>();
	private static List<Actor>	theActorsWithMsgs = new ArrayList<Actor>();
	private static Random		theRNG            = new Random();
	private static Timer		theTimer          = new Timer();
	private static AtomicLong	theMessageCount   = new AtomicLong(0);

	private static final int MAX_MSG_DELAY = 100;	// milliseconds
	private static final int MAX_MSG_TTL   = 1000;	// bounces

	private Random	itsRNG = new Random(theRNG.nextLong());
	private long	itsMessageCount;

	public StockExchangeActor(
		Agent	agent,
		int		maxMessageCount)
	{
		super(agent);
		theActors.add(this);
		theActorsWithMsgs.add(this);

		itsMessageCount = itsRNG.nextInt(maxMessageCount);

		scheduleNextMessage(100);	// wait for all actors to be created
	}

	protected void act(
		Object msg)
	{
		TTLMessage rmsg = (TTLMessage) msg;

		long count = rmsg.decrement();
		if (count > 0)
		{
			theActors.get(randomActor()).recv(msg);
		}
		else
		{
			long liveMsgCount = theMessageCount.decrementAndGet();
			long liveActors   = theActorsWithMsgs.size();
//			System.out.println("Dropping a message; " + liveMsgCount + " msgs; " + liveActors + " actors");
			if (liveMsgCount <= 0 && liveActors == 0)
			{
				synchronized (StockExchangeTest.theTestLock)
				{
					StockExchangeTest.theTestLock.notify();
				}
			}
		}
	}

	private int randomActor()
	{
		return itsRNG.nextInt(theActors.size());
	}

	private void scheduleNextMessage(
		int minDelay)
	{
		theTimer.schedule(
			new SendMessageTask(this),
			1 + minDelay + itsRNG.nextInt(MAX_MSG_DELAY));
	}

	class SendMessageTask
		extends java.util.TimerTask
	{
		StockExchangeActor itsActor;

		public SendMessageTask(
			StockExchangeActor actor)
		{
			itsActor = actor;
		}

		public final void run()
		{
			long liveMsgCount = theMessageCount.incrementAndGet();
			StockExchangeTest.theMaxLiveMsgCount =
				Math.max(StockExchangeTest.theMaxLiveMsgCount, liveMsgCount);
			itsActor.itsMessageCount--;

			TTLMessage msg = new TTLMessage(5 + itsRNG.nextInt(MAX_MSG_TTL-5));
			theActors.get(randomActor()).recv(msg);

			if (itsMessageCount > 0)
			{
				itsActor.scheduleNextMessage(0);
			}
			else
			{
				System.out.println("Actor #" + theActors.indexOf(itsActor) + " done generating messages");
				theActorsWithMsgs.remove(itsActor);
			}
		}
	}

	public static void flushActorList()
	{
		theActors.clear();
	}
}
