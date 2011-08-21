package msn.client.behaviours;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsPredicate;
import jade.content.lang.Codec;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import jade.util.leap.Iterator;
import msn.client.MSNAgent;
import msn.ontology.MSNOntology;

public class AccessBehaviour extends CyclicBehaviour {
	
	private MessageTemplate template;
	private Codec codec;
	private Ontology onto;
	private MSNAgent myAgent;
	
	public AccessBehaviour(Agent a,Codec codec,Ontology onto) {
		super(a);
		myAgent=(MSNAgent)a;
		this.codec = codec;
		this.onto = onto;
	}
	
	public void onStart() {
		// Subscribe as a participant to the Manager agent
		ACLMessage subscription = new ACLMessage(ACLMessage.SUBSCRIBE);
		subscription.setLanguage(codec.getName());
		subscription.setOntology(onto.getName());
		String convId = "C-"+myAgent.getLocalName();
		subscription.setConversationId(convId);
		subscription.addReceiver(new AID(myAgent.MANAGER_NAME, AID.ISLOCALNAME));
		myAgent.send(subscription);
		template = MessageTemplate.MatchConversationId(convId);
	}
	
	public void action() {
		// Receives information about people joining and leaving 
		// the chat from the ChatManager agent
		ACLMessage msg = myAgent.receive(template);
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.INFORM) {
				try {
					AbsPredicate p = (AbsPredicate) myAgent.getContentManager().extractAbsContent(msg);
					if (p.getTypeName().equals(MSNOntology.JOINED)) {
						// Get new participants, add them to the list of participants
						// and notify the gui
						AbsAggregate agg = (AbsAggregate) p.getAbsTerm(MSNOntology.JOINED_WHO);
						if (agg != null) {
							Iterator it = agg.iterator();
							while (it.hasNext()) {
								AbsConcept c = (AbsConcept) it.next();
								myAgent.addPartecipant(BasicOntology.getInstance().toObject(c));
							}
						}
						myAgent.setArrayPartecipants(myAgent.getParticipantNames());
						
						Logger logger = Logger.getMyLogger(this.getClass().getName());
						logger.log(Logger.INFO, "-----In the network at: "+System.currentTimeMillis()+" -Connected users: "+myAgent.getDiscoveryMng().getPartecipants().length);
						
					}
					if (p.getTypeName().equals(MSNOntology.LEFT)) {
						// Get old participants, remove them from the list of participants
						// and notify the gui
						AbsAggregate agg = (AbsAggregate) p.getAbsTerm(MSNOntology.JOINED_WHO);
						if (agg != null) {
							Iterator it = agg.iterator();
							while (it.hasNext()) {
								AbsConcept c = (AbsConcept) it.next();
								myAgent.removePartecipant(BasicOntology.getInstance().toObject(c));
							}
						}
						myAgent.setArrayPartecipants(myAgent.getParticipantNames());
					}
				}
				catch (Exception e) {
					Logger.println(e.toString());
					e.printStackTrace();
				}
			}
			else {
				myAgent.handleUnexpected(msg);
			}
		}
		else {
			block();
		}
	}
} 
