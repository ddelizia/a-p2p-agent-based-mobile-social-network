package msn.client.behaviours;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import jade.content.frame.FrameException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.HashMap;
import jade.util.leap.Map;

import java.io.IOException;
import msn.client.MSNAgent;
import msn.client.Navigator;
import msn.client.Profile;
import msn.client.Wall;
import msn.client.managers.ProfileMng;
import msn.client.managers.WallMng;
import msn.client.utility.UtilityData;
import msn.ontology.MessageTicket;

public class ReceiveBehaviour extends TickerBehaviour{

		private MSNAgent agent;
		private MessageTemplate template;
		private Map buffers;
	
		public ReceiveBehaviour(Agent a) {
			super(a,1000);
			agent = (MSNAgent)a;
			template=MessageTemplate.MatchReceiver(new AID []{myAgent.getAID()});
			buffers=new HashMap();
		}
	
	    protected void onTick() {
	
	        ACLMessage msg = myAgent.receive(template);
	        
	        
			if (msg != null) {
				
				String conv=msg.getConversationId();
		    	MessageTicket n=new MessageTicket();
		    	try {
					n.decodeFrames(msg.getContent());
				} catch (FrameException e) {
					e.printStackTrace();
				}
				String sender=msg.getSender().getLocalName();
				
	            if( msg.getPerformative() == ACLMessage.REQUEST){
	            	
	            	String prot=msg.getProtocol();

	            	if (prot.equals(agent.PROTOCOL_FRIEND ))
	            		agent.addBehaviour(new FriendBehaviour(n,conv));
	            	else if (prot.equals(agent.PROTOCOL_WALL))
	            		agent.addBehaviour(new WallBehaviour(n,conv,sender));
	            	else if (prot.equals(agent.PROTOCOL_PROFILE))
	            		agent.addBehaviour(new ProfileBehaviour(n,conv,sender));
	            	else if (prot.equals(agent.PROTOCOL_WALLFILE))
	            		agent.addBehaviour(new FileBehaviour(n,conv,sender));
	            	else if (prot.equals(agent.PROTOCOL_SEARCH))
	            		agent.addBehaviour(new SearchBehaviour(n,conv,sender));
	            	else if (prot.equals(agent.PROTOCOL_ERROR))
	            		agent.addBehaviour(new ErrorBehaviour(n,conv));
	                else 
	                	agent.handleUnexpected(msg);
	            }
	            else if( msg.getPerformative() == ACLMessage.INFORM){
	            	
	            	String prot=msg.getProtocol();

	            	if (prot.equals(agent.PROTOCOL_FRIEND ))
	            		agent.addBehaviour(new FriendBehaviour(n,conv));
	            	else if (prot.equals(agent.PROTOCOL_WALL))
	            		agent.addBehaviour(new WallBehaviour(n,conv,sender));
	            	else if (prot.equals(agent.PROTOCOL_PROFILE))
	            		agent.addBehaviour(new ProfileBehaviour(n,conv,sender));
	            	else if (prot.equals(agent.PROTOCOL_WALLFILE))
	            		agent.addBehaviour(new FileBehaviour(n,conv,sender));
	            	else if (prot.equals(agent.PROTOCOL_SEARCH))
	            		agent.addBehaviour(new SearchBehaviour(n,conv,sender));
	            	else if (prot.equals(agent.PROTOCOL_ERROR))
	            		agent.addBehaviour(new ErrorBehaviour(n,conv));
	                else 
	                	agent.handleUnexpected(msg);
	            }
	
			}
			else {
				block();
			}
	    }
	    
	    class FriendBehaviour extends OneShotBehaviour{
	    	private String conv;
	    	private MessageTicket n;
	    	
	    	public FriendBehaviour(MessageTicket n,String conv){
	    		this.conv=conv;
	    		this.n=n;
	    	}
	    	
			public void action() {
		    	
	            
				if (conv.equals(MSNAgent.FRIEND_REQUEST)) {
	            	agent.getGui().addNotification(n, null);
	            } 
				
				else if(conv.equals(MSNAgent.FRIEND_ACCEPTATION)){
	            	try {
	            		agent.getGui().addNotification(n, n.getContent());
	            		agent.getFriendsMng().friendAccepted(n.getContent());
	            		agent.getFriendsMng().save();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } 
	            
	            else if(conv.equals(MSNAgent.FRIEND_DELETE)){
	            	try {
	            		agent.getGui().addNotification(n, n.getContent());
	            		agent.getFriendsMng().deleteFriend(n.getContent());
	            		agent.getFriendsMng().save();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
			}
	    	
	    }
	    
	    
	    class ProfileBehaviour extends OneShotBehaviour{
	    	private String conv;
	    	private MessageTicket n;
	    	private String sender;
	    	
	    	public ProfileBehaviour(MessageTicket n,String conv,String sender){
	    		this.conv=conv;
	    		this.n=n;
	    		this.sender=sender;
	    	}
	    	
			public void action() {
				
		    	if (conv.equals(MSNAgent.PROFILE_REQUEST)){
		    		agent.getProfileMng().sendProfileTo(sender, agent);
	            } 
		    	
		    	else if (conv.equals(MSNAgent.PROFILE_RESPONSE)){
	                Profile profile;
	                try {
						profile = UtilityData.fromByteArrayProfile(n.getData(),false);
						agent.getGui().addNotification(n, profile);
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
			}
	    	
	    }
	    
	    class FileBehaviour extends OneShotBehaviour{
	    	private String conv;
	    	private MessageTicket n;
	    	private String sender;
	    	
	    	public FileBehaviour(MessageTicket n,String conv,String sender){
	    		this.conv=conv;
	    		this.n=n;
	    		this.sender=sender;
	    	}
			public void action() {

		    	
				if (conv.equals(MSNAgent.WALLFILE_REQUEST)){
					try {
						agent.getWallMng().sendFileTo(sender, agent,n.getContent());
					} catch (IOException e) {
						System.out.println("file non trovato");
						agent.sendMessage(sender, MSNAgent.ERROR_FILE, MSNAgent.PROTOCOL_ERROR, new MessageTicket(agent.getLocalName(), MSNAgent.ERROR_FILE,n.getContent(),new byte[0]), false);
					}
	            } 
				
				else if (conv.equals(MSNAgent.WALLFILE_RESPONSE)){
	            	
	            	Map buffer=null;
	            	if (!buffers.containsKey(n.getNotificationKey())){
	            		buffer=new HashMap();
	            		putInBuffers(n.getNotificationKey(), buffer);
	            	}else{
	            		buffer=(Map)buffers.get(n.getNotificationKey());
	            	}
	            	
	            	putElementInBuffer(buffer,new Integer(n.getOrder()), n);
	            	
	            	if (buffer.size()==n.getTotal()){
	            		MessageTicket not=MessageTicket.mergeMessages(buffer,n);
	            		buffers.remove(n.getNotificationKey());
						try {
							agent.getGui().addNotification(not, null);
			            	int lastOcc=n.getContent().lastIndexOf('/');
			            	String cont=n.getContent();
			            	String filename=cont.substring(lastOcc+1);
			            	Navigator.createFileOnMem(not.getData(), filename);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
	            	}
	            }
			}
	    	
	    }
	    
	    public synchronized void putInBuffers(Object key, Object value) {
	    	buffers.put(key, value);
		}
	    public synchronized void putElementInBuffer(Map buffer, Object key, Object value) {
	    	buffer.put(key, value);
		}
	    
	    
	    class SearchBehaviour extends OneShotBehaviour{
	    	private String conv;
	    	private MessageTicket n;
	    	private String sender;
	    	
	    	public SearchBehaviour(MessageTicket n,String conv,String sender){
	    		this.conv=conv;
	    		this.n=n;
	    		this.sender=sender;
	    	}
	    	
			public void action() {

		    	
				if (conv.equals(MSNAgent.SEARCH_REQUEST)){
					agent.getWallMng().sendResearchTo(sender, agent,n.getContent());
	            } 
				
				else if (conv.equals(MSNAgent.SEARCH_RESPONSE)){
	            	Wall wall;
					try {
						wall = UtilityData.fromByteArrayWall(n.getData(),false);
						agent.getGui().addNotification(n, wall);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
			}
	    	
	    }
	    
	    class WallBehaviour extends OneShotBehaviour{
	    	private String conv;
	    	private MessageTicket n;
	    	private String sender;
	    	
	    	public WallBehaviour(MessageTicket n,String conv,String sender){
	    		this.conv=conv;
	    		this.n=n;
	    		this.sender=sender;
	    	}
	    	
			public void action() {

		    	if (conv.equals(MSNAgent.WALL_REQUEST)){
		    		agent.getWallMng().sendWallTo(sender, agent);
	            } else if (conv.equals(MSNAgent.WALL_RESPONSE)){
	            	Wall wall;
					try {
						wall = UtilityData.fromByteArrayWall(n.getData(),false);
						agent.getGui().addNotification(n, wall);
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
			}
	    	
	    }
	    
	    class ErrorBehaviour extends OneShotBehaviour{
	    	private String conv;
	    	private MessageTicket n;
	    	
	    	public ErrorBehaviour(MessageTicket n,String conv){
	    		this.conv=conv;
	    		this.n=n;
	    	}
	    	
			public void action() {

		    	if (conv.equals(MSNAgent.ERROR_FILE)){
		    		agent.getGui().addNotification(n, null);
	            }
			}
	    	
	    }
}
