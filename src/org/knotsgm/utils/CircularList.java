package org.knotsgm.utils;

import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class CircularList<E extends CircularListElement<E>> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Serializable
{
	private static final long serialVersionUID = 6166516632663672080L;
	private int element_num = 0;
	private E first = null;
	
	void addBefore(E new_element, E before)
	{
		//	System.out.println("Add before : " + new_element + " " + before);
		
		if(element_num == 0)
		{
			addFirst(new_element);
			return;
		}
		
		new_element.next = before;
		new_element.previous = before.previous;
		
		before.previous.next = new_element;
		before.previous = new_element;
		element_num++;
	}
	
	void remove(CircularListElement<E> element)
	{
		element.next.previous = element.previous;
		element.previous.next = element.next;
		//element.element = null;
		element.next = null;
		element.previous = null;
		element_num--;
	}
	
	@Override
	public ListIterator<E> listIterator(int index)
	{
		CircularListIterator<E> iterator = new CircularListIterator<E>(this, first, false);
		for(int i = 0; i<index; i++) iterator.next();
		return iterator;
	}
	
	@Override
	public ListIterator<E> descendingIterator()
	{
		return new CircularListIterator<E>(this, first, true);
	}
	
	@Override
	public int size()
	{
		return element_num;
	}
	
	@Override
	public void addFirst(E arg0)
	{
		/*CircularListElement<E> list_el = new CircularListElement<E>();
		list_el.element = arg0;*/
		E list_el = arg0;
		
		if(element_num == 0)
		{
			list_el.next = list_el;
			list_el.previous = list_el;
		}
		else
		{
			list_el.next = first;
			list_el.previous = first.previous;
			
			first.previous.next = list_el;
			first.previous = list_el;
		}
		
		first = arg0;
		element_num++;
	}
	
	@Override
	public void addLast(E arg0)
	{
		addBefore(arg0, first); 
	}
	
	@Override
	public E element() 
	{
		if(element_num == 0) throw new NoSuchElementException("Empty LoopList");
		return first;
	}
	
	@Override
	public E getFirst()
	{
		if(element_num == 0) throw new NoSuchElementException("Empty LoopList");
		return first;
	}
	
	@Override
	public E getLast()
	{
		if(element_num == 0) throw new NoSuchElementException("Empty LoopList");
		return first.previous;
	}
	
	@Override
	public boolean offer(E arg0)
	{
		addFirst(arg0);
		return true;
	}
	
	@Override
	public boolean offerFirst(E arg0)
	{
		addFirst(arg0);
		return true;
	}
	
	@Override
	public boolean offerLast(E arg0)
	{
		addLast(arg0);
		return true;
	}
	
	@Override
	public E peek()
	{
		return first;
	}
	
	@Override
	public E peekFirst()
	{
		return first;
	}
	
	@Override
	public E peekLast()
	{
		return first.previous;
	}
	
	@Override
	public E poll()
	{
		return pollFirst();
	}
	
	@Override
	public E pollFirst()
	{
		if(element_num == 0) return null;
		E elmt = first;
		remove(first);
		return elmt;
	}
	
	@Override
	public E pollLast()
	{
		if(element_num == 0) return null;
		E elmt = first.previous;
		remove(first.previous);
		return elmt;
	}
	
	@Override
	public E pop()
	{
		if(element_num == 0) throw new NoSuchElementException("Empty LoopList");
		return pollFirst();
	}
	
	@Override
	public void push(E arg0)
	{
		addFirst(arg0);
	}
	
	@Override
	public E remove()
	{
		return removeFirst();
	}
	
	@Override
	public E removeFirst()
	{
		if(element_num == 0) throw new NoSuchElementException("Empty LoopList");
		return pollFirst();
	}
	
	@Override
	public boolean removeFirstOccurrence(Object arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public E removeLast()
	{
		if(element_num == 0) throw new NoSuchElementException("Empty LoopList");
		return pollLast();
	}
	
	@Override
	public boolean removeLastOccurrence(Object arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
}


