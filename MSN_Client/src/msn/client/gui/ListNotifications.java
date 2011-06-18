package msn.client.gui;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import java.io.IOException;

import jade.core.Agent;
import jade.util.leap.ArrayList;
import jade.util.leap.HashMap;
import jade.util.leap.Map;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import msn.client.MSNAgent;
import msn.client.Profile;
import msn.client.Wall;
import msn.ontology.MessageTicket;

public class ListNotifications extends List {
	
	Map elements=new HashMap();
	
	private CommandListener commandListener;
	
	//private Command fsBack;
	private Command view;
	private Command ignore;
	private Command back;

	public ListNotifications(CommandListener cl, MSNAgent a, Command back) {
		super("" + a.getLocalName() + ": MSN. Notifications", List.IMPLICIT);
		
		this.back=back;
		commandListener=cl;	
		this.addCommand(back);
		this.addCommand(getViewCmd());
		this.addCommand(getIgnoreCmd());
		this.setCommandListener(commandListener);
	}
	
	public void addElement(MessageTicket n,Object o){
		int pos=this.append(n.translate(), null);
		this.set(pos, ""+pos+"-"+n.translate(), null);
		ArrayList al=new ArrayList();
		al.add(0,n.getType());
		al.add(1,n.getContent());
		al.add(2,o);
		elements.put(""+pos+"-"+n.translate(), al);
		Alert alert;
		try {
			alert = new Alert("Notification","You got a new notification: "+n.translate(),Image.createImage("/res/notification_icon.png"),AlertType.CONFIRMATION);
		} catch (IOException e) {
			alert = new Alert("Notification","You got a new notification: "+n.translate(),null,AlertType.CONFIRMATION);
		}
		GuiCommandListener cl=(GuiCommandListener)commandListener;
		cl.showAlert(alert);
	}
	
	public ArrayList removeSelectedElement(){
		int index=getSelectedIndex();
		ArrayList al=(ArrayList) elements.remove(this.getString(index));
		this.delete(index);
		return al;
	}
	
	public ArrayList showSelectedNotification(){
		int index=getSelectedIndex();
		ArrayList al=(ArrayList) elements.get(this.getString(index));
		String type=(String)al.get(0);
		Object o=al.get(2);
		
		String cont=(String)al.get(1);

        System.out.println("ELEMENT TO SHOW "+type+" "+cont);
		
		if (type.equals(MSNAgent.WALL_RESPONSE)){
			Wall w=(Wall)o;
			FormWall fm=new FormWall(commandListener,w.getNickname(),w,back);
			Display.getDisplay(Agent.midlet).setCurrent(fm);
		}
		else if (type.equals(MSNAgent.SEARCH_RESPONSE)){
			Wall w=(Wall)o;
			FormWall fm=new FormWall(commandListener,w.getNickname(),w,back);
			Display.getDisplay(Agent.midlet).setCurrent(fm);
		}
		else if (type.equals(MSNAgent.PROFILE_RESPONSE)){
			Profile p=(Profile)o;
			FormProfile fp=new FormProfile(commandListener,p.getNickname(), p, back);
			Display.getDisplay(Agent.midlet).setCurrent(fp);
		}
		return removeSelectedElement();
	}
	
	public Command getViewCmd() {
        if (view == null) {
            view = new Command("View/Accept", Command.OK, 2);
        }
        return view;
    }
	
    public Command getIgnoreCmd() {
        if (ignore == null) {
            ignore = new Command("Ignore", Command.OK, 2);
        }
        return ignore;
    }
	
	

}
