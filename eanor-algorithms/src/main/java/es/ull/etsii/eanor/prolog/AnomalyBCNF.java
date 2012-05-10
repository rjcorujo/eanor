package es.ull.etsii.eanor.prolog;


public class AnomalyBCNF {
	private Dependency impliedDependency;
	
	public AnomalyBCNF(Dependency impliedDependency) {
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
		if (obj == null || !(obj instanceof AnomalyBCNF))
			return false;
		AnomalyBCNF other = (AnomalyBCNF) obj;
		return other.impliedDependency.equals(impliedDependency);
	}
	
	public String toString() {
		return "La dependencia '"+impliedDependency+"' incumple la FNBC, ya que el antecedente no es superclave";
	}
	
}
