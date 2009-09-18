package com.nps.concurrent;

/**
 * A simple message that expires after a given number of bounces.
 * 
 * @author John Lindal
 */
class TTLMessage
{
	private long	itsCount;

	public TTLMessage(
		long initCount)
	{
		itsCount = initCount;
	}

	public long remaining()
	{
		return itsCount;
	}

	public long decrement()
	{
		return --itsCount;
	}
}
