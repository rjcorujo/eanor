package es.ull.etsii.eanor.prolog;

import java.util.HashSet;
import java.util.Set;

public class AnomaliesWrapper {
	private Set<Anomaly2NF> anomalies2NF;
	private Set<Anomaly3NF> anomalies3NF;
	private Set<AnomalyBCNF> anomaliesBCNF;
	
	public AnomaliesWrapper(Set<Anomaly2NF> anoms2NF) {
		anomalies2NF = anoms2NF;
		anomalies3NF = new HashSet<Anomaly3NF>();
		anomaliesBCNF = new HashSet<AnomalyBCNF>();
	}
	
	public AnomaliesWrapper(Set<Anomaly2NF> anoms2NF, Set<Anomaly3NF> anoms3NF) {
		anomalies2NF = anoms2NF;
		anomalies3NF = anoms3NF;
		anomaliesBCNF = new HashSet<AnomalyBCNF>();
	}
	
	public AnomaliesWrapper(Set<Anomaly2NF> anoms2NF, Set<Anomaly3NF> anoms3NF, Set<AnomalyBCNF> anomsBCNF) {
		anomalies2NF = anoms2NF;
		anomalies3NF = anoms3NF;
		anomaliesBCNF = anomsBCNF;
	}
	
	public Set<Anomaly2NF> getAnomalies2NF() {
		return anomalies2NF;
	}
	public void setAnomalies2NF(Set<Anomaly2NF> anomalies2nf) {
		anomalies2NF = anomalies2nf;
	}
	public Set<Anomaly3NF> getAnomalies3NF() {
		return anomalies3NF;
	}
	public void setAnomalies3NF(Set<Anomaly3NF> anomalies3nf) {
		anomalies3NF = anomalies3nf;
	}
	public Set<AnomalyBCNF> getAnomaliesBCNF() {
		return anomaliesBCNF;
	}
	public void setAnomaliesBCNF(Set<AnomalyBCNF> anomaliesBCNF) {
		this.anomaliesBCNF = anomaliesBCNF;
	}
	
}
