package msn.client.gui;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import java.io.IOException;

import jade.core.Agent;
import jade.util.leap.ArrayList;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;

import msn.client.MSNAgent;
import msn.ontology.MessageTicket;

public class GuiManager implements CommandListener {
	
    private java.util.Hashtable __previousDisplayables = new java.util.Hashtable();
    private Displayable displayableBeforFS=null;

//FORMS
    private FormMain formMain;
    private FormProfile formProfile;
    private FormWall formWall;
    private FormFriends formFriends;
    private FormOthers formOthers;
    private FormWallInsert formWallInsert;
    private ListNotifications listNotifications;
    
//BASIC COMMANDS
    private Command backCmd;
    private Command searchCmd;
    private Command mainCmd;
    private Command fileCmd;
    private Command watchProfileCmd;

//FILE SYTEM NAVIGATOR
    private ListFileSystem listFS;
    
//AGENT
    private MSNAgent myAgent; 
    
    
    private ListFileSystem getNewListFileSystem(){
    	listFS=new ListFileSystem(this,myAgent,formProfile,formWallInsert,getBackCmd());
    	return listFS;
    }
    
    public GuiManager(MSNAgent a){
    	System.out.println("APPLICATION GUI MANAGER STARTED");

        myAgent = a;
        
        //mngs init
        
        
        getNewListFileSystem();

        //form init
        formMain=new FormMain(this, a,getSearchCmd());
        formProfile=new FormProfile(this, a, true, getBackCmd(), getFileCmd());       
        formWall=new FormWall(this, a, true, getBackCmd(), getFileCmd());
        formFriends=new FormFriends(this, a, getBackCmd(),getWatchProfileCmd());       
        formOthers=new FormOthers(this, a, getBackCmd(),getWatchProfileCmd());       
        formWallInsert=new FormWallInsert(this, a, getBackCmd(), getFileCmd()); 
        listNotifications=new ListNotifications(this, a, getBackCmd());
        
        Display.getDisplay(Agent.midlet).setCurrent(formMain);
    }
    
	public void commandAction(Command c, Displayable d) {

		// GENERAL COMMANDS
		// -----------------------------------------------------------------
		if (c == getBackCmd()) {
			if (d instanceof ListFileSystem && displayableBeforFS == formProfile)
				Display.getDisplay(Agent.midlet).setCurrent(formProfile);
			else if (d instanceof ListFileSystem && displayableBeforFS == formWallInsert)
				Display.getDisplay(Agent.midlet).setCurrent(formWallInsert);
			else if ( (d instanceof FormWall || d instanceof FormProfile) && displayableBeforFS == listNotifications)
				Display.getDisplay(Agent.midlet).setCurrent(listNotifications);
			else if (d==formWallInsert)
				Display.getDisplay(Agent.midlet).setCurrent(formWall);
			else
				switchToPreviousDisplayable();
		}

		else if (c == getMainCmd()) {
			Display.getDisplay(Agent.midlet).setCurrent(formMain);
		}
				
		// MANAGING COMMANDS OF NAVIGATOR COMMAND
		// -----------------------------------------------------------------
		else if (c == listFS.getFsViewCmd()) {
			if (displayableBeforFS == formProfile) {
				listFS.runnerProfile(d);
			} else if (displayableBeforFS == formWallInsert) {
				listFS.runnerWallInsert(d);
			}
		} 

		// MANAGING COMMANDS OF FORM MAIN
		// -----------------------------------------------------------------
		else if (d == formMain) {
			// OK COMMAND
			// -----------------------------------------------------------------
			if (c == formMain.getOkCmd()) {
				String s = formMain.getSelection();
				if (s.equals(formMain.DISCOVERY)) {
					formOthers.refresh();
					switchDisplayable(null, formOthers);
				} else if (s.equals(formMain.FRIENDS)) {
					formFriends.refresh();
					switchDisplayable(null, formFriends);
				} else if (s.equals(formMain.PROFILE)) {
					switchDisplayable(null, formProfile);
				} else if (s.equals(formMain.WALL)) {
					switchDisplayable(null, formWall);
				} else if (s.equals(formMain.NOTIFICATION)) {
					displayableBeforFS = listNotifications;
					switchDisplayable(null, listNotifications);
				}
			}
			// EXIT COMMAND
			// -----------------------------------------------------------------
			else if (c == formMain.getExitCmd()) {
				try {
					myAgent.doDelete();
					myAgent.getFriendsMng().stopThreads();
					showExiting();
				} catch (Exception ex) {
				}
			}else if (c == getSearchCmd()) {
				myAgent.getWallMng().obtaingResearch(myAgent, this, formMain.getResTextField().getString());
			}
		}

		// MANAGING COMMANDS OF FORM OTHERS
		// -----------------------------------------------------------------
		else if (d == formOthers) {
			// REFRESH
			// -----------------------------------------------------------------
			if (c == formOthers.getRefreshCmd()) {
				formOthers.refresh();
			}
			// ADD FRIEND
			// -----------------------------------------------------------------
			else if (c == formOthers.getAddFriendCmd()) {
				Alert a = formOthers.addFriends();
				switchDisplayable(a, formMain);
			}
			// WHATCH PROFILE
			// -----------------------------------------------------------------
			if (c == getWatchProfileCmd()) {
				myAgent.getProfileMng().obtaingProfile(formOthers.getPartecipant(), myAgent, this);
			}
		}
		
		// MANAGING COMMANDS OF FORM FRIENDS
		// -----------------------------------------------------------------
		else if (d == formFriends) {
			// WHATCH PROFILE
			// -----------------------------------------------------------------
			if (c == getWatchProfileCmd()) {
				myAgent.getProfileMng().obtaingProfile(formFriends.getFriend(), myAgent, this);
			}
			// WHATCH WALL
			// -----------------------------------------------------------------
			else if (c == formFriends.getWatchWallCmd()) {
				myAgent.getWallMng().obtaingWall(formFriends.getFriend(), myAgent, this);
			}
			// DELETE FRIEND
			// -----------------------------------------------------------------
			else if (c == formFriends.getDeleteFriendCmd()) {
				myAgent.getFriendsMng().startDeleteFriend(formFriends.getFriend());
				formFriends.refresh();
			}
		}

		// MANAGING COMMANDS OF FORM PROFILE
		// -----------------------------------------------------------------
		else if (d == formProfile) {
			// UPDATE PROFILE
			// -----------------------------------------------------------------
			if (c == formProfile.getUpdateProfileCmd()) {
				myAgent.getProfileMng().updateProfile(formProfile.getName(), formProfile.getSurname(), formProfile.getImage());
				Display.getDisplay(Agent.midlet).setCurrent(formProfile);
			}
			// FILESYSTEM
			// -----------------------------------------------------------------
			else if (c == getFileCmd()) {
				displayableBeforFS = formProfile;
				Display.getDisplay(Agent.midlet).setCurrent(getNewListFileSystem());
				
			}
		}

		// MANAGING COMMANDS OF FORM WALL
		// -----------------------------------------------------------------
		else if (d == formWall) {
			// NEW WALL MESSAGE
			// -----------------------------------------------------------------
			if (c == formWall.getAddWallMessageCmd()) {
				Display.getDisplay(Agent.midlet).setCurrent(formWallInsert);
			}
			// DELETE WALL MESSAGE
			// -----------------------------------------------------------------
			else if (c == formWall.getDeleteWallMessageCmd()){
				formWall.deleteMessage();
			}
		}
		else if ( d instanceof FormWall) {
			FormWall fw=(FormWall) d;
			// DOWNLOAD FILE
			// -----------------------------------------------------------------
			if (c == fw.getFileCmd()) {
				myAgent.getWallMng().obtaingFile(fw.getWallOwner(), myAgent, this,fw.getSelectedFilePath());		
			}
		}

		// MANAGING COMMANDS OF FORM WALL INSERT
		// -----------------------------------------------------------------
		else if (d == formWallInsert) {
			// ADDFILE
			// -----------------------------------------------------------------
			if (c == getFileCmd()) {
				displayableBeforFS = formWallInsert;
				Display.getDisplay(Agent.midlet).setCurrent(getNewListFileSystem());
			}
			// ADD MESSAGE TO WALL
			// -----------------------------------------------------------------
			else if (c == formWallInsert.getAddMessageCmd()) {
				myAgent.getWallMng().add(formWallInsert.createWallMessage(),true);
				formWall.refresh();
				Display.getDisplay(Agent.midlet).setCurrent(formWall);
			}
		}

		// MANAGING COMMANDS OF FORM NOTIFICATION
		// -----------------------------------------------------------------
		else if (d == listNotifications) {
			// ACCEPT NOTIFICATION
			// -----------------------------------------------------------------
			if (c == listNotifications.getViewCmd()) {
				ArrayList al=listNotifications.showSelectedNotification();
				String type=(String)al.get(0);
				String content=(String)al.get(1);
				if (type.equals(MSNAgent.FRIEND_REQUEST)){
					try {
						myAgent.getFriendsMng().acceptFriends(content);
						myAgent.getFriendsMng().save();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// IGNORE NOTIFICATION
			// -----------------------------------------------------------------
			else if (c == listNotifications.getIgnoreCmd()) {
				ArrayList al=listNotifications.removeSelectedElement();
				String type=(String)al.get(0);
				String content=(String)al.get(1);
				if (type.equals(MSNAgent.FRIEND_REQUEST))
					myAgent.getFriendsMng().deleteFromAnswersList(content);
			}
		}
		
	}

    
    private void switchToPreviousDisplayable() {
        Displayable __currentDisplayable = Display.getDisplay(Agent.midlet).getCurrent();
        if (__currentDisplayable != null) {
            Displayable __nextDisplayable = (Displayable) __previousDisplayables.get(__currentDisplayable);
            if (__nextDisplayable != null) {
                switchDisplayable(null, __nextDisplayable);
            }
        }
    }
    
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {
        Display display = Display.getDisplay(Agent.midlet);
        Displayable __currentDisplayable = display.getCurrent();
        if (__currentDisplayable != null && nextDisplayable != null) {
            __previousDisplayables.put(nextDisplayable, __currentDisplayable);
        }
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }
    }
    
    private void showExiting() {
        clearMain();
        try {
        	myAgent.getFriendsMng().save();
            System.out.println("Friends saved");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Some problem on saving");
        }
        formMain.append(new StringItem(null, "Exiting. Please wait..."));
    }
    
    private synchronized void clearMain() {
        int size = formMain.size();
        for (int i = 0; i < size; ++i) {
        	formMain.delete(0);
        }
        Display.getDisplay(Agent.midlet).setCurrent(formMain);
    }
    
    public void showAlert(Alert alert){
    	Display display = Display.getDisplay(Agent.midlet);
    	display.setCurrent(alert, display.getCurrent());
    }
    public void showAlarm(String text){
    	Alert alert;
		try {
			alert = new Alert("Alarm", text, Image.createImage("/res/alarm.jpg"), AlertType.ALARM);
		} catch (IOException e) {
			alert = new Alert("Alarm", text, null, AlertType.ALARM);
		}
    	Display display = Display.getDisplay(Agent.midlet);
    	display.setCurrent(alert, display.getCurrent());
    }
    
//COMMON COMMANDS
	private Command getFileCmd() {
	    if (fileCmd == null) {
	        fileCmd = new Command("Get File", Command.OK, 2);
	    }
	    return fileCmd;
	}

	private Command getBackCmd() {
	    if (backCmd == null) {
	        backCmd = new Command("Back", Command.BACK, 4);
	    }
	    return backCmd;
	}
	
	private Command getSearchCmd() {
	    if (searchCmd == null) {
	    	searchCmd = new Command("Search", Command.OK, 4);
	    }
	    return searchCmd;
	}

	private Command getMainCmd() {
	    if (mainCmd == null) {
	        mainCmd = new Command("Main", Command.BACK, 4);
	    }
	    return mainCmd;
	}
	
	public Command getWatchProfileCmd(){
		if (watchProfileCmd == null){
            watchProfileCmd=new Command("Watch profile", Command.OK, 6);
        }
        return watchProfileCmd;
    }
	
	public void addNotification(MessageTicket n, Object o){
		listNotifications.addElement(n, o);
	}

//ELEMENTS GETTERS
	/*
	public Wall getMyWall(){
		return wall.getWall();
	}
	
	public Profile getMyProfile(){
		return profileMng.getProfile();
	}
	
	public FriendsMNG getMyFriendMNG(){
		return friendsMng;
	}


	
	public String[] getFriends(){
		return friendsMng.getFriends();
	}
	*/
	

}
