package com.nps.concurrent;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

/**
 * Manages a thread pool for ThreadPoolActors.
 * 
 * @author John Lindal
 */
public class ActorThreadPool
{
	private long					itsPoolSize;
	private Set<ThreadPoolActor>	itsSleepingActors  =  new HashSet<ThreadPoolActor>();
	private Set<ThreadPoolActor>	itsWaitingActorSet =  new HashSet<ThreadPoolActor>();
	private List<ThreadPoolActor>	itsWaitingActors   =  new LinkedList<ThreadPoolActor>();
	private Set<ThreadPoolActor>	itsRunningActors   =  new HashSet<ThreadPoolActor>();

	public ActorThreadPool(
		long size)
	{
		itsPoolSize = size;
	}

	/**
	 * Add an actor to this thread pool.
	 * 
	 * @param actor	the ThreadPoolActor
	 */
	/* package */ synchronized final void add(
		ThreadPoolActor actor)
	{
		itsSleepingActors.add(actor);
	}

	/**
	 * Add an actor to this thread pool.
	 * 
	 * @param actor	the ThreadPoolActor
	 */
	/* package */ synchronized final void queue(
		ThreadPoolActor actor)
	{
		itsSleepingActors.remove(actor);
		itsRunningActors.remove(actor);
		if (!itsWaitingActorSet.contains(actor))
		{
			itsWaitingActors.add(actor);
		}
	}

	/**
	 * Add an actor to this thread pool.
	 * 
	 * @param actor	the ThreadPoolActor
	 */
	/* package */ synchronized final void remove(
		ThreadPoolActor actor)
	{
		itsSleepingActors.remove(actor);
		itsWaitingActorSet.remove(actor);
		itsWaitingActors.remove(actor);
		itsRunningActors.remove(actor);
	}
}
