package msn.client;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE

import msn.client.behaviours.AccessBehaviour;
import msn.client.behaviours.ReceiveBehaviour;
import msn.client.behaviours.SendBehaviour;
import msn.client.gui.GuiManager;
import msn.client.managers.DiscoveryMNG;
import msn.client.managers.FriendsMNG;
import msn.client.managers.ProfileMng;
import msn.client.managers.WallMng;
import msn.ontology.MSNOntology;
import msn.ontology.MessageTicket;
import jade.core.Agent;
import jade.core.AID;

import jade.lang.acl.ACLMessage;

import jade.util.leap.*;
import jade.util.Logger;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;


public class MSNAgent extends Agent {

	public static final String PROTOCOL_FRIEND="__PROTOCOL_FRIEND__";
	public static final String PROTOCOL_WALL="__PROTOCOL_WALL__";
	public static final String PROTOCOL_PROFILE="__PROTOCOL_PROFILE__";
	public static final String PROTOCOL_WALLFILE="__PROTOCOL_WALLFILE__";
	public static final String PROTOCOL_SEARCH = "__PROTOCOL_SEARCH__";
	public static final String PROTOCOL_ERROR = "__PROTOCOL_ERROR__";
	
	public static final String FRIEND_REQUEST="__FRIEND_REQUEST__";
	public static final String FRIEND_ACCEPTATION="__FRIEND_ACCEPTATION__";
	public static final String FRIEND_DELETE="__FRIEND_DELETE__";
	public static final String PROFILE_REQUEST="__PROFILE_REQUEST__";
	public static final String PROFILE_RESPONSE="__PROFILE_RESPONSE__";
	public static final String WALL_REQUEST="__WALL_REQUEST__";
	public static final String WALL_RESPONSE="__WALL_RESPONSE__";
	public static final String WALLFILE_REQUEST="__WALLFILE_REQUEST__";
	public static final String WALLFILE_RESPONSE="__WALLFILE_RESPONSE__";
	public static final String SEARCH_REQUEST="__SEARCH_REQUEST__";
	public static final String SEARCH_RESPONSE="__SEARCH_RESPONSE__";
	public static final String ERROR_FILE="__ERROR_FILE__";
	
	public static final String MANAGER_NAME = "manager";
	
	
	private GuiManager myGui;
	private Set participants = new SortedSetImpl();
	private Codec codec = new SLCodec();
	private Ontology msgonto = MSNOntology.getInstance();
	private Logger logger = Logger.getMyLogger(this.getClass().getName());
    private String[] arrayPartecipants;
    
    //Managers
    private FriendsMNG friendsMng;
    private DiscoveryMNG discoveryMng;
    private ProfileMng profileMng;
    private WallMng wallMng;
	

	protected void setup() {
		// Register language and ontology
		arrayPartecipants=new String[0];
		ContentManager cm = getContentManager();
		cm.registerLanguage(codec);
		cm.registerOntology(msgonto);
		cm.setValidationMode(false);
		
		
        discoveryMng = new DiscoveryMNG(this);
        friendsMng = new FriendsMNG(this, discoveryMng);
        profileMng = new ProfileMng();
        profileMng.setProfile(ProfileMng.generateProfile(this.getLocalName(),true));
        wallMng = new WallMng();
        wallMng.setWall(WallMng.wallFromDb(this.getLocalName(), true));
        myGui = new GuiManager(this);
		
		// Add initial behaviours
		addBehaviour(new AccessBehaviour(this,codec,msgonto));
        addBehaviour(new ReceiveBehaviour(this));	
	}	
	
	protected void takeDown() {
	}
	
	public void handleUnexpected(ACLMessage msg) {
		if(logger.isLoggable(Logger.WARNING)){
			logger.log(Logger.WARNING,"Unexpected message received from "+msg.getSender().getName());
			logger.log(Logger.WARNING,"Content is: "+msg.getContent());
		}
	}

	//GET SET METHODS
    public String[] getArrayPartecipants(){
        return arrayPartecipants;
    }
    
    public void setArrayPartecipants(String[] array){
        arrayPartecipants=array;
    }
    
    public GuiManager getGui(){
    	return myGui;
    }
    
    public void removePartecipant(Object o){
    	participants.remove(o);
    }
    
    public void addPartecipant(Object o){
    	participants.add(o);
    }
    

    
    public String[] getParticipantNames() {
		String[] pp = new String[participants.size()];
		Iterator it = participants.iterator();
		int i = 0;
		while (it.hasNext()) {
			AID id = (AID) it.next();
			pp[i++] = id.getLocalName();
		}
		return pp;
	}	
    
    public void sendMessageToAll(String conversation, String protocol,  MessageTicket notification){
    	String []friends=friendsMng.getFriends();
    	DiscoveryMNG dm=new DiscoveryMNG(this);
    	for (int i=0;i<friends.length;i++){
    		if (dm.getMap().containsKey(friends[i]))
    			sendMessage(friends[i], conversation,  protocol,  notification,true);
    	}
    }
    
    public void sendMessage(String dest, String conversation, String protocol,  MessageTicket notification, boolean isRequest){
    	DiscoveryMNG dm=new DiscoveryMNG(this);
    	if (dm.getMap().containsKey(dest)){
	    	ArrayList messages=MessageTicket.splitMessage(notification, 100000);
	    	Iterator i=messages.iterator();
	    	while (i.hasNext()){
	    		addBehaviour(new SendBehaviour(dest, this,this.codec,this.msgonto, conversation, protocol,(MessageTicket)i.next(),isRequest));
	    	}
    	}else{
    		myGui.showAlarm("The user "+dest+" is not connected");
    	}
	}
    
	public FriendsMNG getFriendsMng() {
		return friendsMng;
	}

	public void setFriendsMng(FriendsMNG friendsMng) {
		this.friendsMng = friendsMng;
	}

	public DiscoveryMNG getDiscoveryMng() {
		return discoveryMng;
	}

	public void setDiscoveryMng(DiscoveryMNG discoveryMng) {
		this.discoveryMng = discoveryMng;
	}

	public ProfileMng getProfileMng() {
		return profileMng;
	}

	public void setProfileMng(ProfileMng profileMng) {
		this.profileMng = profileMng;
	}

	public WallMng getWallMng() {
		return wallMng;
	}

	public void setWallMng(WallMng wallMng) {
		this.wallMng = wallMng;
	}

	public Logger getLogger() {
		return logger;
	}

	
}
