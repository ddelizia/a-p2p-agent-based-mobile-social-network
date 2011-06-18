package msn.client;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import jade.util.leap.ArrayList;

public class Wall {

    private String nickname;
    private ArrayList wallMessages;
    
    public ArrayList getWallMessages (){
    	return wallMessages;
    }
    
    public String getNickname(){
    	return nickname;
    }
    
	public void setWallMessages(ArrayList loadAllWall) {
		wallMessages=loadAllWall;	
	}

    public void setNickname(String nickname) {
		this.nickname = nickname;
	}




}
