package es.ull.etsii.eanor.prolog.interfaces;

import java.util.List;
import java.util.Set;

import es.ull.etsii.eanor.prolog.AnomaliesWrapper;
import es.ull.etsii.eanor.prolog.Anomaly2NF;
import es.ull.etsii.eanor.prolog.RelationSet;

public interface RelationNormalizer {
	
	/*Calculates the set of Keys*/
	public KeySet getKeys();
	
	/*Calculates closure of a Set of attributes*/
	public AttributeSet getClosure(AttributeSet attrs);
	
	/*Calculates the subset of relation's dependencies that is
	 * the minimal cover for that relation
	 */
	public DependencySet getMinimalCover();
	
	/* Normal Forms Testers*/
	public AnomaliesWrapper test2NF();
	public AnomaliesWrapper test3NF();
	public AnomaliesWrapper testBCNF();
	public boolean is2NF();
	public boolean is3NF();
	public boolean isBCNF();
	
	/* Normalization Algorithms*/
	public RelationSet applySynthesis();
	
}
