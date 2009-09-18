package com.nps.concurrent;

class RingMessage
{
	private long	itsCount;

	public RingMessage(
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
