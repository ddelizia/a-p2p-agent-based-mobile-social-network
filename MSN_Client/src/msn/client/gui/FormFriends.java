package msn.client.gui;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import java.io.IOException;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;

import msn.client.MSNAgent;
import msn.client.managers.DiscoveryMNG;
import msn.client.managers.FriendsMNG;

public class FormFriends extends Form{
	
    private ChoiceGroup listOfFriends;
    private Command watchWallCmd;
    private Command deleteFriendCmd;
	
	private FriendsMNG friendsMng;
	private DiscoveryMNG discoveryMng;
	private CommandListener commandListener;
	private Image online;
	private Image offline;
	

	public FormFriends(CommandListener cl, MSNAgent a, Command backCmd, Command wprof) {
		super("" + a.getLocalName() + ": MSN. Friend List");
		
		friendsMng=a.getFriendsMng();
		commandListener=cl;
		this.discoveryMng=friendsMng.getDiscoveryMNG();
		
		try {
			online=Image.createImage("/res/green-light-38.png");
			offline=Image.createImage("/res/red-light-38.png");
		} catch (IOException e) {
			System.out.println("Image not found");
			e.printStackTrace();
		}
		
		
		this.addCommand(backCmd);
		this.addCommand(wprof);
		this.addCommand(getWatchWallCmd());
		this.addCommand(getDeleteFriendCmd());
		this.setCommandListener(commandListener);
        listOfFriends = new ChoiceGroup("List of Friends", ChoiceGroup.EXCLUSIVE);

        String[] friendsList = friendsMng.getFriends();
        for (int i = 0; i < friendsList.length; i++) {
        	Image im =discoveryMng.getMap().contains(friendsList[i]) ? online : offline; 
            listOfFriends.append(friendsList[i], im);
        }

        this.append(listOfFriends);
	}
	
	public void refresh(){
		listOfFriends.deleteAll();

        String[] friendsList = friendsMng.getFriends();
        for (int i = 0; i < friendsList.length; i++) {
        	Image im =discoveryMng.getMap().contains(friendsList[i]) ? online : offline;
            listOfFriends.append(friendsList[i], im);
        }
	}
	
	public Command getWatchWallCmd(){
        if (watchWallCmd == null){
            watchWallCmd=new Command("Watch wall", Command.OK, 6);
        }
        return watchWallCmd;
    }
	
	public Command getDeleteFriendCmd(){
        if (deleteFriendCmd == null){
        	deleteFriendCmd=new Command("Delete Friend", Command.OK, 6);
        }
        return deleteFriendCmd;
    }
    
    public String getFriend(){
    	return listOfFriends.getString(listOfFriends.getSelectedIndex());
    }

}
