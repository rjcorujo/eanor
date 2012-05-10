package es.ull.etsii.eanor.prolog;

import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;

public class Anomaly2NF {
	private Attribute dependentAttribute;
	private Dependency impliedDependency;
	private AttributeSet impliedKey;
	
	public Anomaly2NF(Attribute dependentAttribute,	Dependency impliedDependency, AttributeSet impliedKey) {
		this.dependentAttribute = dependentAttribute;
		this.impliedDependency = impliedDependency;
		this.impliedKey = impliedKey;
	}

	public Attribute getDependentAttribute() {
		return dependentAttribute;
	}

	public void setDependentAttribute(Attribute dependentAttribute) {
		this.dependentAttribute = dependentAttribute;
	}

	public Dependency getImpliedDependency() {
		return impliedDependency;
	}

	public void setImpliedDependency(Dependency impliedDependency) {
		this.impliedDependency = impliedDependency;
	}

	public AttributeSet getImpliedKey() {
		return impliedKey;
	}

	public void setImpliedKey(AttributeSet impliedKey) {
		this.impliedKey = impliedKey;
	}
	
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || !(obj instanceof Anomaly2NF))
			return false;
		Anomaly2NF other = (Anomaly2NF) obj;
		return (other.dependentAttribute.equals(dependentAttribute) &&
				other.impliedDependency.equals(impliedDependency) &&
				other.impliedKey.equals(impliedKey));
	}
	
	public String toString() {
		return "Atributo '"+dependentAttribute+"' depende parcialmente de la clave '"+impliedKey+"' debido a la dependencia '"+impliedDependency+"'";
	}
	
}
