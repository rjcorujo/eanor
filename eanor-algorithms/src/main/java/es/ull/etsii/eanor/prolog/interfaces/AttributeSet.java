package es.ull.etsii.eanor.prolog.interfaces;

import es.ull.etsii.eanor.prolog.Attribute;

public interface AttributeSet {
	
	public boolean add(Attribute attr);
	public void addAll(AttributeSet attr);
	public boolean contains(Attribute attr);
	public Attribute get(String attrnam);
	public boolean remove(Attribute attr);
	public void clear();
	public int getSize();
	public boolean isEmpty();
	public void reset();
	public boolean hasNext();
	public Attribute getNext();
}
