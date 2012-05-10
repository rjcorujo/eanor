package es.ull.etsii.eanor.prolog.impl;
import java.util.LinkedList;

import es.ull.etsii.eanor.prolog.Dependency;
import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;
import es.ull.etsii.eanor.prolog.interfaces.DependencySet;

public class DepListSet implements DependencySet {
	
	private LinkedList<Dependency> vec;
	private int index;
	
	public DepListSet() {
		vec = new LinkedList<Dependency>();
		index = 0;
	}
	
	public boolean add (Dependency dep) {
		return vec.add(dep);
	}
	
	public boolean contains(Dependency dep) {
		return vec.contains(dep);
	}
	
	public Dependency get (Dependency dep) {
		return vec.get(vec.indexOf(dep));
	}
	
	public AttributeSet getAttributeSet() {
		AttributeSet attrSet = new AttrListSet();
		for (int i = 0; i < vec.size(); i++) {
			attrSet.addAll(vec.get(i).getAnts());
			attrSet.addAll(vec.get(i).getCons());
		}
		return attrSet;
	}
	
	public boolean remove(Dependency dep) {
		return vec.remove(dep);
	}
	
	public void clear() {
		vec.clear();
	}
	
	public int getSize() {
		return vec.size();
	}
	
	public boolean isEmpty() {
		return vec.size() == 0;
	}
	
	public void reset() {
		index = 0;
	}
	
	public boolean hasNext() {
		return index < vec.size();
	}

	public Dependency getNext() {
		Dependency dep = vec.get(index);
		index++;
		return dep;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DependencySet))
			return false;
		DependencySet depSet = (DependencySet)obj;
		boolean equality = true;
		reset();
		while (this.hasNext()) {
			equality = depSet.contains(this.getNext()) && equality;
		}
		return equality;
	}
	
	public String toString() {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("{");
		for (int i = 0; i < getSize(); i++) {
			Dependency dep = vec.get(i);
			strbuf.append("("+dep.getAnts()+")->("+dep.getCons()+")");
			if (i+1 < getSize())
				strbuf.append(",");
		}
		strbuf.append("}");
		return strbuf.toString();
	}
}
