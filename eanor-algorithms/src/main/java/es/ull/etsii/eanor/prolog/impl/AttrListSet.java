package es.ull.etsii.eanor.prolog.impl;
import java.util.LinkedList;

import es.ull.etsii.eanor.prolog.Attribute;
import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;


public class AttrListSet implements AttributeSet{
	
	private LinkedList<Attribute> atlist;
	private int index;
	
	public AttrListSet() {
		atlist = new LinkedList<Attribute>();
		index = 0;
	}
	
	public boolean add(Attribute attr) {
		return contains(attr) ? false : atlist.add(attr);
	}
	
	public void addAll(AttributeSet attrSet) {
		while (attrSet.hasNext()) {
			atlist.add(attrSet.getNext());
		}
	}
	
	public boolean contains (Attribute atr) {
		return atlist.contains(atr);
	}
	
	public Attribute get(String attrnam) {	
		int ind = atlist.indexOf(new Attribute(attrnam));
		return ind >= 0 ? atlist.get(ind):null;
	}

	public boolean remove(Attribute attr) {
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
	
	public Attribute getNext() {
		return atlist.get(index++);
	}

	public boolean hasNext() {
		return index < atlist.size();
	}

	public String toString() {
		StringBuffer strbuf = new StringBuffer();
		for (int i = 0; i < getSize(); i++) {
			strbuf.append(atlist.get(i));
			if (i+1 < getSize())
				strbuf.append(",");
		}
		return strbuf.toString();
	}
	
	public boolean equals(Object obj2) {
		if (obj2 == null)
			return false;
		if (!(obj2 instanceof AttrListSet)) 
			return false;
		AttrListSet cjtoatobj = (AttrListSet)obj2;
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
