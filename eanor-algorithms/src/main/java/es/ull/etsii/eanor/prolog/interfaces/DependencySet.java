package es.ull.etsii.eanor.prolog.interfaces;

import es.ull.etsii.eanor.prolog.Dependency;

public interface DependencySet {
	
	public boolean add(Dependency dep);
	public boolean contains(Dependency dep);
	public Dependency get(Dependency dep);
	public boolean remove(Dependency dep);
	public AttributeSet getAttributeSet();
	public void clear();
	public int getSize();
	public boolean isEmpty();
	public void reset();
	public boolean hasNext();
	public Dependency getNext();
}
