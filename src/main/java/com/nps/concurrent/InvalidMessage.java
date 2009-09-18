package com.nps.concurrent;

/**
 * Thrown when an actor does not want to accept a message.
 * 
 * @author John Lindal
 */
public class InvalidMessage
	extends RuntimeException
{
	public InvalidMessage()
	{
	}
}
