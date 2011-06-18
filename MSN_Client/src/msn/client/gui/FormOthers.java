package msn.client.gui;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;

import msn.client.MSNAgent;
import msn.client.managers.FriendsMNG;

public class FormOthers extends Form {
	
	private MSNAgent myAgent;
	private FriendsMNG friendsMng;
	private CommandListener commandListener;
	
    private Command refreshCmd;
    private Command addFriendCmd;
    private ChoiceGroup partecipants;
    
	
	public FormOthers(CommandListener cl, MSNAgent a, Command backCmd, Command wPCmd) {
		
		super("" + a.getLocalName() + ": MSN. Participants");
		
		myAgent=a;
		friendsMng=a.getFriendsMng();
		commandListener=cl;
		
        this.addCommand(getRefreshCmd());
        this.addCommand(backCmd);
        this.addCommand(wPCmd);
        this.addCommand(getAddFriendCmd());

        this.setCommandListener(commandListener);
        
        partecipants = new ChoiceGroup("List of Partecipants", ChoiceGroup.EXCLUSIVE);
        System.out.println("Start discovery");
        String l[] = myAgent.getArrayPartecipants();
        
        System.out.println("# partecipanti: "+ l.length);
        
        for (int i = 0; i < l.length; i++) {
            if (!friendsMng.getFriendsMap().contains(l[i])) {
                partecipants.append(l[i], null);
            }
        }
        
        this.append(partecipants);
	}
	
    public Command getRefreshCmd() {
        if (refreshCmd == null) {
            refreshCmd = new Command("Refresh", Command.OK, 1);
        }
        return refreshCmd;
    }

    public Command getAddFriendCmd() {
        if (addFriendCmd == null) {
            addFriendCmd = new Command("Add to friends", Command.OK, 2);
        }
        return addFriendCmd;
    }
    
    public void refresh() {
    	this.deleteAll();
        partecipants = new ChoiceGroup("List of Partecipants", ChoiceGroup.EXCLUSIVE);
        String l[] = myAgent.getArrayPartecipants();
        for (int i = 0; i < l.length; i++) {
            if (!friendsMng.getFriendsMap().contains(l[i])) {
                partecipants.append(l[i], null);
            }
        }
        this.append(partecipants);
    }
    
    public Alert addFriends() {
        for (int i = 0; i < partecipants.size(); i++) {
            if (partecipants.isSelected(i)) {
                friendsMng.addFriend(partecipants.getString(i));
            }
        }
       return new Alert("Alert", "Your request has been sent", null, AlertType.CONFIRMATION);
    }
    
    public String getPartecipant(){
    	return partecipants.getString(partecipants.getSelectedIndex());
    }
	
}
