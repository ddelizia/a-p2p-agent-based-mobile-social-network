package msn.client.managers;

import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;

import java.io.IOException;

import msn.client.MSNAgent;
import msn.client.Navigator;
import msn.client.Wall;
import msn.client.WallMessage;
import msn.client.gui.GuiCommandListener;
import msn.client.utility.UtilityData;
import msn.client.utility.UtilityDatastore;
import msn.ontology.MessageTicket;

public class WallMng {
	private Wall wall;
	
	public Wall getWall() {
		return wall;
	}

	public void setWall(Wall wall) {
		this.wall = wall;
	}

	public static Wall wallFromDb (String nickname, boolean isMy) {
		Wall wall=new Wall();
		wall.setNickname(nickname);
		wall.setWallMessages(new ArrayList());
        if (isMy){
        	wall.setWallMessages(UtilityDatastore.loadAllWall(nickname));
        }
        return wall;
    }
    
    public static  Wall wallFromSearch (String nickname, String tags) {
    	Wall wall=new Wall();
    	wall.setNickname(nickname);
    	wall.setWallMessages(new ArrayList());
    	wall.setWallMessages(UtilityDatastore.loadAllWallResearch(nickname, tags));
    	return wall;
    }
    
    public void add (WallMessage wm,boolean isMy){
    	wall.getWallMessages().add(wm);
    	if (isMy)
    		UtilityDatastore.saveWallMessage(wm);
    }
    
    public void delete (String element){
    	Iterator it=wall.getWallMessages().iterator();
    	while (it.hasNext()){
    		WallMessage wm=(WallMessage)it.next();
    		if (wm.toString().equals(element));
    			it.remove();
    	}
    	UtilityDatastore.removeMessage(wall.getNickname(), element);
    }
    
    public ArrayList getWallMessages (){
    	return wall.getWallMessages();
    }
    
    public String getNickname(){
    	return wall.getNickname();
    }
    
	public void setWallMessages(ArrayList loadAllWall) {
		wall.setWallMessages(loadAllWall);	
	}

	
	  //REQUEST    
    public void obtaingWall(String destination,MSNAgent myAgent, GuiCommandListener menuGui){
        //Profile p = null;
        try {
            Runnable r = new RequestingWall(destination, myAgent, menuGui);
            Thread thread = new Thread(r);
            thread.start();
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //return p;
    }

    class RequestingWall implements Runnable {

        String destination;
        MSNAgent myAgent;
        GuiCommandListener menuGui;

        public RequestingWall(String destination, MSNAgent myAgent, GuiCommandListener menuGui) {
            super();
            this.destination=destination;
            this.myAgent=myAgent;
            this.menuGui=menuGui;
        }

        public void run() {
            MessageTicket notification=new MessageTicket(destination, MSNAgent.WALL_REQUEST ,MessageTicket.NULLCONT, new byte[0]);
        	System.out.println("Richiedo il muro");
        	System.out.println(notification);
            myAgent.sendMessage(notification.getDestinationRequest(),MSNAgent.WALL_REQUEST, MSNAgent.PROTOCOL_WALL, notification,true);
            
        }
    }
    
    //REQUEST    
    public void obtaingFile(String destinationRequest,MSNAgent myAgent, GuiCommandListener menuGui,String path){
        //Profile p = null;
        try {
            Runnable r = new RequestingFile(destinationRequest, myAgent, menuGui, path);
            Thread thread = new Thread(r);
            thread.start();
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //return p;
    }

    class RequestingFile implements Runnable {

        String destinationRequest;
        MSNAgent myAgent;
        GuiCommandListener menuGui;
        String path;

        public RequestingFile(String destinationRequest, MSNAgent myAgent, GuiCommandListener menuGui, String path) {
            super();
            this.destinationRequest=destinationRequest;
            this.myAgent=myAgent;
            this.menuGui=menuGui;
            this.path=path;
        }

        public void run() {
            MessageTicket notification=new MessageTicket(destinationRequest, MSNAgent.WALLFILE_REQUEST ,path, new byte[0]);
        	System.out.println("Richiedo il file");
        	System.out.println(notification);
            myAgent.sendMessage(notification.getDestinationRequest(),MSNAgent.WALLFILE_REQUEST, MSNAgent.PROTOCOL_WALLFILE, notification,true);
            
        }
    }
    
    //REQUEST    
    public void obtaingResearch(MSNAgent myAgent, GuiCommandListener menuGui,String research){
        //Profile p = null;
        try {
            Runnable r = new RequestingResearch(myAgent, menuGui, research);
            Thread thread = new Thread(r);
            thread.start();
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //return p;
    }

    class RequestingResearch implements Runnable {

        MSNAgent myAgent;
        GuiCommandListener menuGui;
        String research;

        public RequestingResearch( MSNAgent myAgent, GuiCommandListener menuGui, String research) {
            super();
            this.myAgent=myAgent;
            this.menuGui=menuGui;
            this.research=research;
        }

        public void run() {
            MessageTicket notification=new MessageTicket(MSNAgent.SEARCH_REQUEST, MSNAgent.SEARCH_REQUEST ,research, new byte[0]);
        	System.out.println("Search");
        	System.out.println(notification);
            myAgent.sendMessageToAll(MSNAgent.SEARCH_REQUEST, MSNAgent.PROTOCOL_SEARCH, notification);
            
        }
    }

    
//RESPONSE    
    public void sendWallTo(String destination, MSNAgent myAgent){
        try {
            Runnable r = new RequestingResponder(destination, myAgent, UtilityData.toByteArray(wall));
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

    class RequestingResponder implements Runnable {

        String destination;
        MSNAgent myAgent;
        byte[] b;

        public RequestingResponder(String destination, MSNAgent myAgent, byte [] b) {
            super();
            this.myAgent=myAgent;
            this.destination=destination;
            this.b=b;
        }

        public void run() {
            myAgent.sendMessage(destination,MSNAgent.WALL_RESPONSE, MSNAgent.PROTOCOL_WALL, new MessageTicket(myAgent.getLocalName(), MSNAgent.WALL_RESPONSE,null, b),false);
        }
    }
    
  //RESPONSE    
    public void sendFileTo(String destination, MSNAgent myAgent,String path){
        try {
        	System.out.println("path: "+path);
            Runnable r = new RequestingFileResponder(destination, myAgent, Navigator.getFileInBytes(path), path);
            Thread thread = new Thread(r);
            thread.start();
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    class RequestingFileResponder implements Runnable {

        String destination;
        String path;
        MSNAgent myAgent;
        byte[] b;

        public RequestingFileResponder(String destination, MSNAgent myAgent, byte [] b, String path) {
            super();
            this.myAgent=myAgent;
            this.destination=destination;
            this.b=b;
            this.path=path;
        }

        public void run() {
            myAgent.sendMessage(destination,MSNAgent.WALLFILE_RESPONSE, MSNAgent.PROTOCOL_WALLFILE, new MessageTicket(myAgent.getLocalName(), MSNAgent.WALLFILE_RESPONSE,path,b),false);
        }
    }
    
    //RESPONSE    
    public void sendResearchTo(String destination, MSNAgent myAgent,String research){
        try {
            Runnable r = new RequestingSearchResponder(destination, myAgent, UtilityData.toByteArray(WallMng.wallFromSearch(destination, research)), research);
            Thread thread = new Thread(r);
            thread.start();
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}

    }

    class RequestingSearchResponder implements Runnable {

        String destination;
        MSNAgent myAgent;
        byte[] b;
        String research;

        public RequestingSearchResponder(String destination, MSNAgent myAgent, byte [] b, String research) {
            super();
            this.myAgent=myAgent;
            this.destination=destination;
            this.research=research;
            this.b=b;
        }

        public void run() {
            myAgent.sendMessage(destination,MSNAgent.SEARCH_RESPONSE, MSNAgent.PROTOCOL_SEARCH, new MessageTicket(myAgent.getLocalName(), MSNAgent.SEARCH_RESPONSE,research, b),false);
        }
    }
}
