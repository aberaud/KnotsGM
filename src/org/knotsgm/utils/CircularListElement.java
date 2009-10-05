package org.knotsgm.utils;

abstract public class CircularListElement<E>
{
	//E element = null;
	E previous = null;
	E next = null;
	
	public E next()
	{
		return next;
	}	
	
	public E previous()
	{
		return previous;
	}
}