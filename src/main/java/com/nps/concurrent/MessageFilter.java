package com.nps.concurrent;

/**
 * MessageFilter is useful if static rules, e.g., class type or content,
 * determine what messages are accepted.  Dynamic rules, e.g., B only
 * accepted after A, must be implemented by the Actor in process().
 * 
 * @author John Lindal
 */
public interface MessageFilter
{
	/**
	 * @param msg	the message
	 * @return		true if the message is acceptable
	 */
	public boolean acceptMessage(Object msg);
}
