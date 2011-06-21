package msn.ontology;

/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */


import jade.content.onto.*;
import jade.content.schema.*;

/**
   Ontology containing concepts, predicates and actions used 
   within the chat application.
   @author Giovanni Caire - TILAB
 */
public class AccessOntology extends Ontology implements AccessVocabulary {
  
  // The singleton instance of this ontology
	private static Ontology theInstance = new AccessOntology();
	
	public static Ontology getInstance() {
		return theInstance;
	}
	
  /**
   * Constructor
   */
  private AccessOntology() {
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
