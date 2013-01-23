package util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ArraySet<T> implements Set<T> {

	Object[] data;
	
	public ArraySet() {
		this.data = new Object[0];
	}
	public ArraySet(T[] data) {
		this.data = data;
	}
	
	
	@Override
	public boolean add(T e) {
		Object[] temp = new Object[data.length + 1];
		for(int i=0; i<data.length; i++) temp[i] = data[i];
		temp[temp.length - 1] = e;
		this.data = temp;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		Object[] temp = new Object[data.length + c.size()];
		for(int i=0; i<data.length; i++) temp[i] = data[i];
		int i=data.length;
		for(T elem : c) {
			temp[i] = elem;
		}
		this.data = temp;
		return true;
	}

	@Override
	public void clear() {
		this.data = new Object[0];
	}

	@Override
	public boolean contains(Object o) {
		for(int i=0; i<data.length; i++) if(data[i].equals(o)) return true;
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o : c) {
			boolean result = false;
			for(int i=0; i<data.length; i++) if(data[i].equals(o)) { result = true; break; }
			if(!result) return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return data.length > 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			int index = 0;
			
			@Override
			public boolean hasNext() {
				return data.length - index > 2;
			}

			@SuppressWarnings("unchecked")
			@Override
			public T next() {
				index += 1;
				return (T)data[index];
			}

			@Override
			public void remove() {
				Object[] temp = new Object[data.length-1];
				int skip = 0;
				for(int i=0; i<data.length; i++) {
					if(i == index) skip = -1;
					else temp[i+skip] = data[i];
				}
				data = temp;
			}
		};
	}

	@Override
	public boolean remove(Object o) {
		Object[] temp = new Object[data.length-1];
		int skip = 0;
		for(int i=0; i<data.length; i++) {
			if(data[i].equals(o)) skip = -1;
			else temp[i+skip] = data[i];
		}
		data = temp;
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Object[] temp = new Object[data.length-c.size()];
		int skip = 0;
		for(Object o : c) {
			for(int i=0; i<data.length; i++) {
				if(data[i].equals(o)) skip -= 1;
				else temp[i+skip] = data[i];
			}
		}
		data = temp;
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public int size() {
		return data.length;
	}

	@Override
	public Object[] toArray() {
		Object[] value = new Object[data.length];
		for(int i=0; i<data.length; i++) value[i] = data[i];
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Q> Q[] toArray(Q[] a) {
		Q[] value = (Q[]) Array.newInstance(a.getClass(), 0);
		for(int i=0; i<data.length; i++) value[i] = (Q)data[i];
		return value;
	}

}
