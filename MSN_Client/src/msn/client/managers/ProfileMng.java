package msn.client.managers;

import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

import msn.client.MSNAgent;
import msn.client.Profile;
import msn.client.gui.GuiCommandListener;
import msn.client.utility.UtilityData;
import msn.client.utility.UtilityDatastore;
import msn.ontology.MessageTicket;

public class ProfileMng {
	
	private Profile profile;
	
    public Profile getProfile() {
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
    	profile.setName(name);
    	profile.setSurname(surname);
    	profile.setImg(img);
        saveProfile();
    }


    
//LOAD / SAVE
    public boolean loadProfile() {
        
        Profile prof=UtilityDatastore.loadProfile(profile.getNickname());
        if (prof==null){
        	return false;
        }
    	profile.setNickname(prof.getName());
    	profile.setSurname(prof.getSurname());
    	profile.setImg(prof.getImg());  
        return true;
    }

    public boolean saveProfile() {
        return UtilityDatastore.saveProfile(profile);
    }

//REQUEST    
    public void obtaingProfile(String name,MSNAgent myAgent, GuiCommandListener menuGui){
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
        GuiCommandListener menuGui;

        public requestingProfile(String name, MSNAgent myAgent, GuiCommandListener menuGui) {
            super();
            this.name=name;
            this.myAgent=myAgent;
            this.menuGui=menuGui;
        }

        public void run() {
        	MessageTicket notification=new MessageTicket(name, MSNAgent.PROFILE_REQUEST,MessageTicket.NULLCONT,new byte[0]);
        	System.out.println("Richiedo il profilo");
        	System.out.println(notification);
            myAgent.sendMessage(notification.getDestinationRequest(),MSNAgent.PROFILE_REQUEST, MSNAgent.PROTOCOL_PROFILE,notification, true);
            
        }
    }

    
//RESPONSE    
    public void sendProfileTo(String dest, MSNAgent myAgent){
        try {
            Runnable r = new requestingResponder(dest, myAgent, UtilityData.toByteArray(profile));
            Thread thread = new Thread(r);
            thread.start();
            thread.join();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("ERROR Profile UtilityData.toByteArray(Profile.this)");
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
            System.out.println("Sending file to "+name);
            myAgent.sendMessage(name,MSNAgent.PROFILE_RESPONSE, MSNAgent.PROTOCOL_PROFILE,new MessageTicket(myAgent.getLocalName(), MSNAgent.PROFILE_RESPONSE,null,b),false);
        }
    }
}
