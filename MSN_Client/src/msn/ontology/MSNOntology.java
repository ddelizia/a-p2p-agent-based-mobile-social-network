package msn.ontology;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;

public class MSNOntology extends Ontology implements MSNVocabulary {
  
  // The singleton instance of this ontology
	private static Ontology theInstance = new MSNOntology();
	
	public static Ontology getInstance() {
		return theInstance;
	}
	
  /**
   * Constructor
   */
  private MSNOntology() {
  	super(ONTOLOGY_NAME, BasicOntology.getInstance(), null);

    try {
    	add(new PredicateSchema(JOINED));
    	add(new PredicateSchema(LEFT));
    	
    	PredicateSchema ps = (PredicateSchema) getSchema(JOINED);
    	ps.add(JOINED_WHO, (ConceptSchema) getSchema(BasicOntology.AID), 0, ObjectSchema.UNLIMITED);

    	ps = (PredicateSchema) getSchema(LEFT);
    	ps.add(LEFT_WHO, (ConceptSchema) getSchema(BasicOntology.AID), 0, ObjectSchema.UNLIMITED);

    	


    } 
    catch (OntologyException oe) {
    	oe.printStackTrace();
    } 
	}

}
