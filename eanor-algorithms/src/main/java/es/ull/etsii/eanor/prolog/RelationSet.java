package es.ull.etsii.eanor.prolog;

import java.util.Vector;

public class RelationSet {
private Vector<Relation> rellist;
	
	public RelationSet() {
		rellist = new Vector<Relation>();
	}
	
	public boolean add(Relation rel) {
		return isContained(rel) ? false : rellist.add(rel);
	}

	public Relation get(int index) {
		
		return index < rellist.size() ? (Relation)rellist.get(index) : null; 
	}

	public int getSize() {
		return rellist.size();
	}

	public Relation removeRelation(int index) {
		return index < rellist.size()? (Relation)rellist.remove(index) : null;
	}
	
	public void removeAllRels() {
		rellist.removeAllElements();
	}
	
	public boolean isContained (Relation rel) {
		return rellist.indexOf(rel) >= 0;
	}

	public String toString() {
		return rellist.toString();
	}
	
	public boolean equals(Object obj2) {
		if (obj2 == null)
			return false;
		if (!(obj2 instanceof RelationSet)) 
			return false;
		RelationSet cjtoatobj = (RelationSet)obj2;
		for (int i = 0; i < rellist.size(); i++) {
			if (!cjtoatobj.rellist.contains(rellist.get(i)))
				return false;
		}
		for (int i = 0; i < cjtoatobj.rellist.size(); i++) {
			if (!rellist.contains(cjtoatobj.rellist.get(i)))
				return false;
		}
		return true;
	}
}
