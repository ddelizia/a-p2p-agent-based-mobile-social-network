package msn.client.behaviours;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import java.util.Calendar;
import java.util.Date;

import jade.content.frame.FrameException;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import msn.client.MSNAgent;
import msn.ontology.MessageTicket;

public class SendBehaviour extends OneShotBehaviour {
	
    private String conversation;
    private MessageTicket o;
    private MSNAgent myAgent;
    private String protocol;
    private String destin;
	private Codec codec;
	private Ontology onto;
	private boolean isRequest;

    public SendBehaviour(String dest,Agent a,Codec codec,Ontology onto , String conversation,String protocol,  MessageTicket notification, boolean isRequest) {
        super(a);
        this.destin=dest;
        this.conversation=conversation;
        this.o=notification;
        this.protocol=protocol;
        this.onto=onto;
        this.codec=codec;
        myAgent=(MSNAgent)a;
        this.isRequest=isRequest;
    }

    public void action() {
    	ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setOntology(onto.getName());
		msg.setLanguage(codec.getName());
		msg.setPerformative(ACLMessage.INFORM);
		if (isRequest)
			msg.setPerformative(ACLMessage.REQUEST);
		msg.setConversationId(conversation);
		msg.setProtocol(protocol);
		
		Logger logger = Logger.getMyLogger(this.getClass().getName());
		logger.log(Logger.INFO, "-----Send "+protocol+" Data at: "+System.currentTimeMillis()+" -Connected users: "+myAgent.getDiscoveryMng().getPartecipants().length);
    	
		AID dest = new AID(destin, AID.ISLOCALNAME);

		String encoded=null;
		try {
			encoded = o.encodeFrames();
		} catch (FrameException e) {
			e.printStackTrace();
		}
		msg.setContent(encoded);

		msg.addReceiver(dest);
		myAgent.send(msg);
		
    }
} 
