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
			myAgent = (MSNAgent)a;
			template=MessageTemplate.MatchReceiver(new AID []{myAgent.getAID()});
			buffers=new HashMap();
		}
	
	    protected void onTick() {
	
	        ACLMessage msg = myAgent.receive(template);
	        
	        
			if (msg != null) {
	            if( msg.getPerformative() == ACLMessage.REQUEST){
	            	
	            	String prot=msg.getProtocol();

	            	if (prot.equals(agent.PROTOCOL_FRIEND ))
	            		agent.addBehaviour(new FriendBehaviour(msg));
	            	else if (prot.equals(agent.PROTOCOL_WALL))
	            		agent.addBehaviour(new WallBehaviour(msg));
	            	else if (prot.equals(agent.PROTOCOL_PROFILE))
	            		agent.addBehaviour(new ProfileBehaviour(msg));
	            	else if (prot.equals(agent.PROTOCOL_WALLFILE))
	            		agent.addBehaviour(new FileBehaviour(msg));
	            	else if (prot.equals(agent.PROTOCOL_SEARCH))
	            		agent.addBehaviour(new SearchBehaviour(msg));
	                else 
	                	agent.handleUnexpected(msg);
	            }
	            else if( msg.getPerformative() == ACLMessage.INFORM){
	            	
	            	String prot=msg.getProtocol();

	            	if (prot.equals(agent.PROTOCOL_FRIEND ))
	            		agent.addBehaviour(new FriendBehaviour(msg));
	            	else if (prot.equals(agent.PROTOCOL_WALL))
	            		agent.addBehaviour(new WallBehaviour(msg));
	            	else if (prot.equals(agent.PROTOCOL_PROFILE))
	            		agent.addBehaviour(new ProfileBehaviour(msg));
	            	else if (prot.equals(agent.PROTOCOL_WALLFILE))
	            		agent.addBehaviour(new FileBehaviour(msg));
	            	else if (prot.equals(agent.PROTOCOL_SEARCH))
	            		agent.addBehaviour(new SearchBehaviour(msg));
	                else 
	                	agent.handleUnexpected(msg);
	            }
	
			}
			else {
				block();
			}
	    }
	    
	    class FriendBehaviour extends OneShotBehaviour{
	    	private ACLMessage msg;
	    	public FriendBehaviour(ACLMessage msg){
	    		this.msg=msg;
	    	}
			public void action() {
		    	String conv=msg.getConversationId();
		    	MessageTicket n=new MessageTicket();
		    	try {
					n.decodeFrames(msg.getContent());
				} catch (FrameException e) {
				}
	            if (conv.equals(MSNAgent.FRIEND_REQUEST)) {
	            	agent.getGui().addNotification(n, null);
	            } else if(conv.equals(MSNAgent.FRIEND_ACCEPTATION)){
	            	try {
	            		agent.getGui().addNotification(n, n.getContent());
	            		agent.getFriendsMng().friendAccepted(n.getContent());
	            		agent.getFriendsMng().save();
					} catch (Exception e) {
					}
	            } else if(conv.equals(MSNAgent.FRIEND_DELETE)){
	            	try {
	            		agent.getGui().addNotification(n, n.getContent());
	            		agent.getFriendsMng().deleteFriend(n.getContent());
	            		agent.getFriendsMng().save();
					} catch (Exception e) {
					}
	            }
			}
	    	
	    }
	    
	    
	    class ProfileBehaviour extends OneShotBehaviour{
	    	private ACLMessage msg;
	    	public ProfileBehaviour(ACLMessage msg){
	    		this.msg=msg;
	    	}
			public void action() {
		    	String conv=msg.getConversationId();
		    	MessageTicket n=new MessageTicket();
		    	try {
					n.decodeFrames(msg.getContent());
				} catch (FrameException e) {
					e.printStackTrace();
				}
		    	if (conv.equals(MSNAgent.PROFILE_REQUEST)){
		    		ProfileMng profManager=new ProfileMng();
		    		profManager.setProfile(agent.getProfileMng().getProfile());
		    		profManager.sendProfileTo(msg.getSender().getLocalName(), agent);
	            } else if (conv.equals(MSNAgent.PROFILE_RESPONSE)){
	                Profile profile;
	                try {
						profile = UtilityData.fromByteArrayProfile(n.getData(),false);
						agent.getGui().addNotification(n, profile);
					} catch (IOException e) {
						System.out.println("Profile send/receive error");
						e.printStackTrace();
					}
	            }
			}
	    	
	    }
	    
	    class FileBehaviour extends OneShotBehaviour{
	    	private ACLMessage msg;
	    	public FileBehaviour(ACLMessage msg){
	    		this.msg=msg;
	    	}
			public void action() {
				String conv=msg.getConversationId();
		    	MessageTicket n=new MessageTicket();
		    	try {
					n.decodeFrames(msg.getContent());
				} catch (FrameException e) {
					e.printStackTrace();
				}
		    	if (conv.equals(MSNAgent.WALLFILE_REQUEST)){
		    		WallMng wallmng=new WallMng();
		    		wallmng.setWall(agent.getWallMng().getWall());
		    		wallmng.sendFileTo(msg.getSender().getLocalName(), agent,n.getContent());
	            } else if (conv.equals(MSNAgent.WALLFILE_RESPONSE)){
	            	
	            	System.out.println(n.getNotificationKey());
	            	
	            	Map buffer=null;
	            	if (!buffers.containsKey(n.getNotificationKey())){
	            		buffer=new HashMap();
	            		buffers.put(n.getNotificationKey(), buffer);
	            	}else{
	            		buffer=(Map)buffers.get(n.getNotificationKey());
	            	}
	            		
	            	buffer.put(new Integer(n.getOrder()), n);
	            	System.out.println("Adding: "+n.getOrder());
	            	
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
							System.out.println("WallFile send/receive error");
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
	            	}
	            }
			}
	    	
	    }
	    
	    class SearchBehaviour extends OneShotBehaviour{
	    	private ACLMessage msg;
	    	public SearchBehaviour(ACLMessage msg){
	    		this.msg=msg;
	    	}
			public void action() {
		    	String conv=msg.getConversationId();
		    	MessageTicket n=new MessageTicket();
		    	try {
					n.decodeFrames(msg.getContent());
				} catch (FrameException e) {
					e.printStackTrace();
				}
		    	if (conv.equals(MSNAgent.SEARCH_REQUEST)){
		    		WallMng wallmng=new WallMng();
		    		wallmng.setWall(agent.getWallMng().getWall());
		    		wallmng.sendResearchTo(msg.getSender().getLocalName(), agent,n.getContent());
	            } else if (conv.equals(MSNAgent.SEARCH_RESPONSE)){
	            	Wall wall;
					try {
						wall = UtilityData.fromByteArrayWall(n.getData(),false);
						agent.getGui().addNotification(n, wall);
					} catch (IOException e) {
						System.out.println("WallSearch send/receive error");
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
			}
	    	
	    }
	    
	    class WallBehaviour extends OneShotBehaviour{
	    	private ACLMessage msg;
	    	public WallBehaviour(ACLMessage msg){
	    		this.msg=msg;
	    	}
			public void action() {
		    	String conv=msg.getConversationId();
		    	MessageTicket n=new MessageTicket();
		    	try {
					n.decodeFrames(msg.getContent());
				} catch (FrameException e) {
					e.printStackTrace();
				}
		    	if (conv.equals(MSNAgent.WALL_REQUEST)){
		    		WallMng wallmng=new WallMng();
		    		wallmng.setWall(agent.getWallMng().getWall());
		    		wallmng.sendWallTo(msg.getSender().getLocalName(), agent);
	            } else if (conv.equals(MSNAgent.WALL_RESPONSE)){
	            	Wall wall;
					try {
						wall = UtilityData.fromByteArrayWall(n.getData(),false);
						agent.getGui().addNotification(n, wall);
					} catch (IOException e) {
						System.out.println("Wall send/receive error");
						e.printStackTrace();
					}
	            }
			}
	    	
	    }
}
