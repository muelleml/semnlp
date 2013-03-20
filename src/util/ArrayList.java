package util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import model.Sentence;

public class ArrayList<T> implements List<T>, Iterable<T>
{
	T[] data;
	int size;
	int initial;
	
	@SuppressWarnings("unchecked")
	public ArrayList(int initial) {
		this.initial = initial;
		this.size = 0;
		this.data = (T[])new Object[initial];
	}

	public ArrayList(T[] arr)
	{
		this.data = arr;
		initial = arr.length;
		size = data.length;
	}

	@Override
	public Iterator<T> iterator()
	{
		return new ArrayListIterator<>(this);
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public boolean isEmpty()
	{
		return size>0;
	}

	@Override
	public boolean contains(Object o)
	{
		for(int i=0; i<size; i++) if(data[i].equals(o)) return true;
		return false;
	}

	@Override
	public Object[] toArray()
	{
		return Arrays.copyOf(data, size);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(E[] a)
	{
		return (E[]) Arrays.copyOf(data, size, a.getClass());
	}

	@Override
	public boolean add(T e)
	{
		try {
			if(size == data.length) {
				grow();
			}
			data[size++] = e;
			
			return true;
		}
		catch(OutOfMemoryError ex) {
			return false;
		}
	}
	void grow() {
		grow(1);
	}
	void grow(int min) {
		if(initial > 0)
			data = Arrays.copyOf(data, data.length+((1 + min/initial) * initial));
		else if(data.length < Integer.MAX_VALUE)
			data = Arrays.copyOf(data, data.length+(data.length >> 1));
		else throw new OutOfMemoryError();
	}
	@Override
	public boolean remove(Object o)
	{
		boolean found = false;
		for(int i=0; i<size; i++) {
			if(data[i].equals(o))
				found = true;
			data[i] = data[i+(found?1:0)];
		}
		size -= (found?1:0);
		return found;
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		for(Object o : c)
			if(!contains(o))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{	
		try {
			if(size+c.size() < data.length)
				grow(size+c.size() - data.length);

			for(T t : c) {
				data[size] = t;
				size+=1;
			}
			return true;
		}
		catch(OutOfMemoryError e) {
			return false;
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		throw new RuntimeException("So eine Scheiﬂe gibts hier nicht!");
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		for(Object o : c) remove(o);
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear()
	{
		data = (T[])new Object[initial==0?10:initial];
		size = 0;
	}

	@Override
	public T get(int index)
	{
		return data[index];
	}

	@Override
	public T set(int index, T element)
	{
		data[index] = element;
		return element;
	}

	@Override
	public void add(int index, T element)
	{
		throw new RuntimeException("Hier werden keine indices geadded!!!11einseinself");
	}

	@Override
	public T remove(int index)
	{
		T elem = null;
		for(int i=0; i<size; i++) {
			if(i == index) elem = data[i];
			data[i] = data[i+(i>=index?1:0)];
		}
		size -= index<size?1:0;
		return elem;
	}

	@Override
	public int indexOf(Object o)
	{
		for(int i=0; i<size; i++)
			if(data[i].equals(o)) 
				return i;
		return -1;
	}

	@Override
	public int lastIndexOf(Object o)
	{
		for(int i=size-1; i>=0; i--)
			if(data[i].equals(o)) 
				return i;
		return -1;
	}

	@Override
	public ListIterator<T> listIterator()
	{
		return new ArrayListIterator<T>(this);
	}

	@Override
	public ListIterator<T> listIterator(int index)
	{
		return new ArrayListIterator<T>(this, index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> subList(int fromIndex, int toIndex)
	{
		T[] sub =(T[])new Object[toIndex-fromIndex];
		for(int i=0; i<sub.length; i++)
			sub[i] = data[i+toIndex];
		
		return new ArrayList<T>(sub);
	}

	public T[] getData()
	{
		return data;
	}
}
