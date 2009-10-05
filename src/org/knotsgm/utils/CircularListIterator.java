package org.knotsgm.utils;

import java.util.ListIterator;

public class CircularListIterator<E extends CircularListElement<E>> implements ListIterator<E>
{
	private CircularList<E> list;
	private E next_element;
	private E last_returned = null;
	private int num = 0;
	private boolean descending;
	
	public CircularListIterator(CircularList<E> parent_list, E first, boolean descending)
	{
		list = parent_list;
		next_element = first;
		this.descending = descending;
	}
	
	@Override
	public void add(E arg0)
	{
		last_returned = null;
		if(num == 0) list.addFirst(arg0);
		else list.addBefore(arg0, next_element);
		num++;
	}
	
	@Override
	public boolean hasNext()
	{
		//System.out.println(num + " " + (!descending && num == list.size()?"false":"true"));
		if(descending && num == 0 || !descending && num == list.size()) return false;
		return true;
	}
	
	@Override
	public boolean hasPrevious()
	{
		if(!descending && num == 0 || descending && num == list.size()) return false;
		return true;
	}
	
	@Override
	public E next()
	{
		//System.out.println("coucou");
		if(descending) return nativePrevious();
		else return nativeNext();
	}
	
	@Override
	public int nextIndex()
	{
		return descending?num-1:num+1;
	}
	
	@Override
	public E previous()
	{
		if(descending) return nativeNext();
		else return nativePrevious();
	}
	
	private E nativeNext()
	{
		num++;
		if(num == list.size()+1) num = 1;
		last_returned = next_element;
		next_element = next_element.next;
		return next_element.previous;
	}
	
	
	private E nativePrevious()
	{
		num--;
		if(num == -1) num = list.size()-1;
		next_element = next_element.previous;
		last_returned = next_element;
		return next_element;
	}
	
	@Override
	public int previousIndex()
	{
		return descending?num+1:num-1;
	}
	
	@Override
	public void remove()
	{
		if(last_returned == null)
			throw new IllegalStateException("Neither next nor previous have been called, or remove or add have been called after the last call to next or previous.");
		if(last_returned == next_element.previous) num--;
		list.remove(last_returned);
		last_returned = null;
	}
	
	@Override
	public void set(E arg0)
	{
		//FIXME
		if(last_returned == null)
			throw new IllegalStateException("Neither next nor previous have been called, or remove or add have been called after the last call to next or previous.");
		last_returned = arg0;
	}
	
}
