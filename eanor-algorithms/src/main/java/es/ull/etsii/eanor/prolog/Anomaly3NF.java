package es.ull.etsii.eanor.prolog;


public class Anomaly3NF {
	private Dependency impliedDependency;
	
	public Anomaly3NF(Dependency impliedDependency) {
		super();
		this.impliedDependency = impliedDependency;
	}

	public Dependency getImpliedDependency() {
		return impliedDependency;
	}

	public void setImpliedDependency(Dependency impliedDependency) {
		this.impliedDependency = impliedDependency;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || !(obj instanceof Anomaly3NF))
			return false;
		Anomaly3NF other = (Anomaly3NF) obj;
		return other.impliedDependency.equals(impliedDependency);
	}
	
	public String toString() {
		return "La dependencia '"+impliedDependency+"' incumple 3FN, ya que ni el antecedente es superclave ni el consecuente un atributo primo";
	}
	
}
