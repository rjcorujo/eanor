package es.ull.etsii.eanor.prolog.tests;

import java.util.Iterator;
import java.util.Set;

import es.ull.etsii.eanor.prolog.Anomaly2NF;
import es.ull.etsii.eanor.prolog.Anomaly3NF;

public class EanorTestsUtils {

	/* This method checks if two 2NF Anomalies Sets are equals*/
	public static boolean equalsSetAnomalies2NF(Set<Anomaly2NF> col1, Set<Anomaly2NF> col2) {
		Iterator<Anomaly2NF> itc1 = col1.iterator();
		boolean equals = true;
		while(itc1.hasNext() && equals) {
			Iterator<Anomaly2NF> itc2 = col2.iterator();
			Anomaly2NF anomC1 = itc1.next();
			boolean eq = false;
			while(itc2.hasNext() && !eq) {
				eq = itc2.next().equals(anomC1);				
			}
			equals = eq;
		}
		
		Iterator<Anomaly2NF> itc2 = col2.iterator();
		while(itc2.hasNext() && equals) {
			Iterator<Anomaly2NF> it1 = col1.iterator();
			Anomaly2NF anomC2 = itc2.next();
			boolean eq = false;
			while(it1.hasNext() && !eq) {
				eq = it1.next().equals(anomC2);				
			}
			equals = eq;
		}
		return equals;
	}
	
	public static boolean equalsSetAnomalies3NF(Set<Anomaly3NF> col1, Set<Anomaly3NF> col2) {
		Iterator<Anomaly3NF> itc1 = col1.iterator();
		boolean equals = true;
		while(itc1.hasNext() && equals) {
			Iterator<Anomaly3NF> itc2 = col2.iterator();
			Anomaly3NF anomC1 = itc1.next();
			boolean eq = false;
			while(itc2.hasNext() && !eq) {
				eq = itc2.next().equals(anomC1);				
			}
			equals = eq;
		}
		
		Iterator<Anomaly3NF> itc2 = col2.iterator();
		while(itc2.hasNext() && equals) {
			Iterator<Anomaly3NF> it1 = col1.iterator();
			Anomaly3NF anomC2 = itc2.next();
			boolean eq = false;
			while(it1.hasNext() && !eq) {
				eq = it1.next().equals(anomC2);				
			}
			equals = eq;
		}
		return equals;
	}
}
