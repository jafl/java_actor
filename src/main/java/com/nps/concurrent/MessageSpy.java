package com.nps.concurrent;

/**
 * MessageSpy is useful for logging or other types of monitoring.  Every
 * spy sees every messages that is sent, even those that are rejected.
 * 
 * @author John Lindal
 */
public interface MessageSpy
{
	/**
	 * @param msg		the message
	 * @param accepted	true if the message was accepted
	 */
	public void observeMessage(Object msg, boolean accepted);
}
