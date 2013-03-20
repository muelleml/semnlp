package util;

import java.util.ListIterator;

public class ArrayListIterator<T> implements ListIterator<T>
{

	int index;
	private ArrayList<T> al;
	public ArrayListIterator(ArrayList<T> arrayList)
	{
		this.al = arrayList;
		this.index = 0;
	}
	public ArrayListIterator(ArrayList<T> arrayList, int index)
	{
		this.al = arrayList;
		this.index = index;
	}

	@Override
	public boolean hasNext()
	{
		return index < al.size()-1;
	}

	@Override
	public T next()
	{
		return al.get(++index);
	}

	@Override
	public void remove()
	{
		al.remove(index);
	}

	@Override
	public boolean hasPrevious()
	{
		return index > 0;
	}

	@Override
	public T previous()
	{
		return al.get(--index);
	}

	@Override
	public int nextIndex()
	{
		return index+1;
	}

	@Override
	public int previousIndex()
	{
		return index-1;
	}

	@Override
	public void set(T e)
	{
		al.set(index, e);
	}

	@Override
	public void add(T e)
	{
		al.add(e);

	}
}
