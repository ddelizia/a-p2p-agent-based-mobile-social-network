package msn.client.managers;

import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

import msn.client.MSNAgent;
import msn.client.Profile;
import msn.client.gui.GuiManager;
import msn.client.utility.UtilityData;
import msn.client.utility.UtilityDatastore;
import msn.ontology.MessageTicket;

public class ProfileMng {
	
	private Profile profile;
	
    public Profile getProfile() {
    	if (profile==null)
    		profile=new Profile();
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public static Profile generateProfile(String nickname, boolean isMy) {
    	Profile profile=new Profile();
    	profile.setNickname(nickname);   	
        if (isMy){
	        try {
	            RecordStore db = RecordStore.openRecordStore(nickname + "profile", true);
	            int numrec=db.getNumRecords();
	            db.closeRecordStore();
	            if (numrec==0){
	            	UtilityDatastore.initProfile(nickname);
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
        }
        return profile;
    }

    public void updateProfile(String name, String surname, Image img) {
    	getProfile().setName(name);
    	getProfile().setSurname(surname);
    	getProfile().setImg(img);
        saveProfile();
    }


    
//LOAD / SAVE
    public boolean loadProfile() {
        
        Profile prof=UtilityDatastore.loadProfile(profile.getNickname());
        if (prof==null){
        	return false;
        }
        getProfile().setName(prof.getName());
        getProfile().setSurname(prof.getSurname());
        getProfile().setImg(prof.getImg());  
        return true;
    }

    public boolean saveProfile() {
        return UtilityDatastore.saveProfile(getProfile());
    }

//REQUEST    
    public void obtaingProfile(String name,MSNAgent myAgent, GuiManager menuGui){
        try {
            Runnable r = new requestingProfile(name, myAgent, menuGui);
            Thread thread = new Thread(r);
            thread.start();
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    class requestingProfile implements Runnable {

        String name;
        MSNAgent myAgent;
        Profile p;
        GuiManager menuGui;

        public requestingProfile(String name, MSNAgent myAgent, GuiManager menuGui) {
            super();
            this.name=name;
            this.myAgent=myAgent;
            this.menuGui=menuGui;
        }

        public void run() {
        	MessageTicket notification=new MessageTicket(name, MSNAgent.PROFILE_REQUEST,MessageTicket.NULLCONT,new byte[0]);
            myAgent.sendMessage(notification.getDestinationRequest(),MSNAgent.PROFILE_REQUEST, MSNAgent.PROTOCOL_PROFILE,notification, true);
            
        }
    }

    
//RESPONSE    
    public void sendProfileTo(String dest, MSNAgent myAgent){
        try {
            Runnable r = new requestingResponder(dest, myAgent, UtilityData.toByteArray(getProfile()));
            Thread thread = new Thread(r);
            thread.start();
            thread.join();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    class requestingResponder implements Runnable {

        String name;
        MSNAgent myAgent;
        byte[] b;

        public requestingResponder(String name, MSNAgent myAgent, byte [] b) {
            super();
            this.myAgent=myAgent;
            this.name=name;
            this.b=b;
        }

        public void run() {
            myAgent.sendMessage(name,MSNAgent.PROFILE_RESPONSE, MSNAgent.PROTOCOL_PROFILE,new MessageTicket(myAgent.getLocalName(), MSNAgent.PROFILE_RESPONSE,null,b),false);
        }
    }
}
