package com.nps.concurrent;

/**
 * A spy who prints out every time the message has done N hops.
 * 
 * @author John Lindal
 */
class RingSpy
	implements MessageSpy
{
	private long	itsModulus;

	public RingSpy(
		long modulus)
	{
		itsModulus = modulus;
	}

	/**
	 * @param msg		the message
	 * @param accepted	true if the message was accepted
	 */
	public void observeMessage(
		Object	msg,
		boolean	accepted)
	{
		TTLMessage rmsg = (TTLMessage) msg;

		long count = rmsg.remaining();
		if (count % 10000 == 0)
		{
			System.out.println(count);
		}
	}
}
