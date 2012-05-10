
package es.ull.etsii.eanor.webservice.rest;

import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import es.ull.etsii.eanor.prolog.Relation;
import es.ull.etsii.eanor.prolog.RelationSet;
import es.ull.etsii.eanor.prolog.impl.PrologRelNormalizer;
import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;
import es.ull.etsii.eanor.prolog.interfaces.DependencySet;
import es.ull.etsii.eanor.prolog.interfaces.KeySet;
import es.ull.etsii.eanor.prolog.interfaces.RelationNormalizer;
import es.ull.etsii.eanor.prolog.utils.AttributeSetParserSimple;
import es.ull.etsii.eanor.prolog.utils.DependencySetParserSimple;


/** Example resource class hosted at the URI path "/myresource"
 */

@Path("/deps/{deps}/attrs/{attrs}")
public class NormalizationResource {
    /** Method processing HTTP GET requests, producing "text/plain" MIME media
     * type.
     * Example: /claves/A,B-C%20B-A%20C-D/A,B,C,D
     * @return String that will be send back as a response of type "text/plain".
     */
	private DependencySet dependencySet;
	private AttributeSet attributeSet;
	
    private void processParameters(String deps, String attrs) {
    	if (!PrologRelNormalizer.isLoaded()) {
    		URL url = getClass().getResource("/eanor.pl");
    		PrologRelNormalizer.loadPLFile(url.getFile());
    	}
    	dependencySet = DependencySetParserSimple.parseStr("","",",","",","," ","",deps);
    	attributeSet = AttributeSetParserSimple.parseStr("",",","",attrs);
    }
    
    @Path("/mincover")
    @GET 
    @Produces("text/plain")
    public String getMinimalCover(@PathParam("deps") String deps, @PathParam("attrs") String attrs) {
		processParameters(deps, attrs);
    	Relation relation = new Relation(attributeSet,dependencySet);
		RelationNormalizer relNorm = new PrologRelNormalizer(relation);
		DependencySet depSet = relNorm.getMinimalCover();
        return depSet.toString();
    }
    
    @Path("/closure")
    @GET 
    @Produces("text/plain")
    public String getClosure(@PathParam("deps") String deps, @PathParam("attrs") String attrs) {
		processParameters(deps, attrs);
    	Relation relation = new Relation(dependencySet.getAttributeSet(),dependencySet);
		RelationNormalizer relNorm = new PrologRelNormalizer(relation);
		AttributeSet attrResSet = relNorm.getClosure(attributeSet);
        return attrResSet.toString();
    }
    
    @Path("/keys")
    @GET 
    @Produces("text/plain")
    public String getKeys(@PathParam("deps") String deps, @PathParam("attrs") String attrs) {
		processParameters(deps, attrs);
    	Relation relation = new Relation(attributeSet,dependencySet);
		RelationNormalizer relNorm = new PrologRelNormalizer(relation);
		KeySet keySet = relNorm.getKeys();
        return keySet.toString();
    }
    
    @Path("/synthesis")
    @GET 
    @Produces("text/plain")
    public String getSynthesis(@PathParam("deps") String deps, @PathParam("attrs") String attrs) {
    	processParameters(deps, attrs);
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attributeSet, dependencySet));
		RelationSet relSet = relNorm.applySynthesis();
        return relSet.toString();
    }
    
    @Path("/check2NF")
    @GET 
    @Produces("text/plain")
    public String check2NF(@PathParam("deps") String deps, @PathParam("attrs") String attrs) {
    	processParameters(deps, attrs);
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attributeSet, dependencySet));
		return ""+relNorm.is2NF();
    }
    
    @Path("/check3NF")
    @GET 
    @Produces("text/plain")
    public String check3NF(@PathParam("deps") String deps, @PathParam("attrs") String attrs) {
    	processParameters(deps, attrs);
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attributeSet, dependencySet));
		return ""+relNorm.is3NF();
    }
    
    @Path("/checkBCNF")
    @GET 
    @Produces("text/plain")
    public String checkBCNF(@PathParam("deps") String deps, @PathParam("attrs") String attrs) {
    	processParameters(deps, attrs);
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attributeSet, dependencySet));
		return ""+relNorm.isBCNF();
    }
   
}
