package es.ull.etsii.eanor.prolog.impl;
import java.util.LinkedList;

import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;
import es.ull.etsii.eanor.prolog.interfaces.KeySet;


public class KeyListSet implements KeySet{
	
	private LinkedList<AttributeSet> atlist;
	private int index;
	
	public KeyListSet() {
		atlist = new LinkedList<AttributeSet>();
		index = 0;
	}
	
	public boolean add(AttributeSet attr) {
		return contains(attr) ? false : atlist.add(attr);
	}
	
	public boolean contains (AttributeSet atr) {
		return atlist.contains(atr);
	}

	public boolean remove(AttributeSet attr) {
		return atlist.remove(attr);
	}
	
	public void clear() {
		atlist.clear();
	}

	public int getSize() {
		return atlist.size();
	}
	
	public boolean isEmpty() {
		return atlist.size() == 0;
	}
	
	public void reset() {
		index = 0;
	}
	
	public AttributeSet getNext() {
		return atlist.get(index++);
	}

	public boolean hasNext() {
		return index < atlist.size();
	}

	public String toString() {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("{ ");
		for (int i = 0; i < getSize(); i++) {
			strbuf.append("{"+atlist.get(i)+"}");
			if (i+1 < getSize())
				strbuf.append(",");
		}
		strbuf.append(" }");
		return strbuf.toString();
	}
	
	public boolean equals(Object obj2) {
		if (obj2 == null)
			return false;
		if (!(obj2 instanceof KeyListSet)) 
			return false;
		KeyListSet cjtoatobj = (KeyListSet)obj2;
		for (int i = 0; i < atlist.size(); i++) {
			if (!cjtoatobj.atlist.contains(atlist.get(i)))
				return false;
		}
		for (int i = 0; i < cjtoatobj.atlist.size(); i++) {
			if (!atlist.contains(cjtoatobj.atlist.get(i)))
				return false;
		}
		return true;
	}

}
