package es.ull.etsii.eanor.prolog.interfaces;

public interface KeySet{
	
	public boolean add(AttributeSet attrs);
	public boolean contains(AttributeSet attrs);
	public int getSize();
	public boolean isEmpty();
	public boolean remove(AttributeSet attrs);
	public void clear();
	
	/* Return the next Key from the KeySet*/
	public AttributeSet getNext();
	/* Check if there are more Keys available*/
	public boolean hasNext();
	/* Reset the position, so the next time getNext is called 
	 * will return the first element
	 */
	public void reset();

}
