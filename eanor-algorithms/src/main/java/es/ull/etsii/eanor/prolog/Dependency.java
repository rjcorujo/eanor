package es.ull.etsii.eanor.prolog;

import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;


public class Dependency {
	private AttributeSet antec_set;
	private AttributeSet consec_set;
	
	public Dependency() {
		antec_set = consec_set = null;
	}
	
	public Dependency(AttributeSet antec, AttributeSet cons) {
		antec_set = antec;
		consec_set = cons;
	}
	
	public void setAnts(AttributeSet ants) {
		antec_set = ants;
	}
	
	public void setCons(AttributeSet cons) {
		consec_set = cons;
	}
	
	public AttributeSet getAnts() {
		return antec_set;
	}
	
	public AttributeSet getCons() {
		return consec_set;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Dependency))
			return false;
		Dependency dep = (Dependency)(obj);
		return dep.getAnts().equals(getAnts()) && dep.getCons().equals(getCons());
	}
	
	public String toString () {
		StringBuffer strbuf = new StringBuffer("");
		antec_set.reset();
		while (antec_set.hasNext()) {
			strbuf.append(antec_set.getNext());
			if (antec_set.hasNext())
				strbuf.append(",");
		}
		strbuf.append(" -> ");
		consec_set.reset();
		while (consec_set.hasNext()) {
			strbuf.append(consec_set.getNext());
			if (consec_set.hasNext())
				strbuf.append(",");
		}
		return strbuf.toString();
	}
	
}
