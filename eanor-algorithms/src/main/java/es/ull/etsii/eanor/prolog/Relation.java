package es.ull.etsii.eanor.prolog;

import es.ull.etsii.eanor.prolog.impl.AttrListSet;
import es.ull.etsii.eanor.prolog.impl.DepListSet;
import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;
import es.ull.etsii.eanor.prolog.interfaces.DependencySet;

public class Relation {
	private String name;
	private AttributeSet attrset;
	private DependencySet depset;
	
	public Relation() {
		attrset = null;
		depset = null;
		name = "Unknown";
	}
	
	public Relation(AttributeSet attrSet, DependencySet depSet) {
		depset = depSet;
		attrset = attrSet;
		name = "Unknown";
	}
	
	/*Getters*/
	public String getName() {
		return name;
	}
	
	public AttributeSet getAttrset() {
		return attrset;
	}

	public DependencySet getDepset() {
		return depset;
	}

	/*Setters*/
	public void setName(String pname) {
		name = pname;
	}
	
	public void setAttrset(AttributeSet attrset) {
		this.attrset = attrset;
	}

	public void setDepset(DependencySet depset) {
		this.depset = depset;
	}
	
	/*Metodos*/
	public boolean addAttribute (Attribute attr) {
		return attrset.add(attr);
	}
	
	public boolean addDependency(Dependency dep) {
		return depset.add(dep); 
	}

	/*Remove*/
	public void removeAtribSet() {
		attrset.clear();
	}
	
	public void removeDepSet() {
		depset.clear();
	}
	
	/*Typical methods*/
	
	public boolean isValid() {
		return (name != null) && (attrset.getSize()>0);
	}
	
	public String toString() {
		return name+"[ Attrs = {"+attrset+"} Deps = {"+depset+"} ]";
	}
	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Relation) {
			Relation rel2 = (Relation)obj;
			return rel2.attrset.equals(attrset) && rel2.depset.equals(depset);
		}
		return false;
	}
	

	
	/*Algoritmos*/
	

}
