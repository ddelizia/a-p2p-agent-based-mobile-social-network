package msn.client.gui;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import jade.util.leap.Iterator;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;

import msn.client.MSNAgent;
import msn.client.Wall;
import msn.client.WallMessage;
import msn.client.managers.WallMng;
import msn.client.utility.UtilityDatastore;

public class FormWall extends Form{
	
	private CommandListener commandListener;
	private Wall wallMng;
	
	private ChoiceGroup wallList = new ChoiceGroup("Wall", ChoiceGroup.EXCLUSIVE);
	
	private Command addWallMessageCmd;
	private Command addFileCmd;
	private Command deleteWallMessageCmd;
	
	private MSNAgent msnagent;
	
	
	public FormWall(CommandListener cl, MSNAgent a, boolean isMy, Command backCmd, Command fileCmd) {
		super("" + a.getLocalName() + ": MSN. Wall");
		
		commandListener=cl;
		wallMng=a.getWallMng().getWall();
		msnagent=a;
		
		if (isMy){
			this.addCommand(getAddWallMessageCmd());
			this.addCommand(getDeleteWallMessageCmd());
		}
		
		this.addCommand(backCmd);
		
		wallList.deleteAll();
		fillWallMessages(wallList, wallMng);
		
		
		this.append(wallList);
		
		this.setCommandListener(commandListener);
	}
	
	public FormWall(CommandListener cl, String user, Wall w, Command backCmd) {
		super(user + ": MSN. Wall");
		
		commandListener=cl;
		wallMng=w;
		msnagent=null;
		
		this.addCommand(backCmd);
		this.addCommand(getFileCmd());
		
		wallList.deleteAll();
		fillWallMessages(wallList, wallMng);
		
		
		this.append(wallList);
		
		this.setCommandListener(commandListener);
	}
	
	public Command getAddWallMessageCmd(){
        if (addWallMessageCmd == null) {
            addWallMessageCmd = new Command("Add New Message", Command.OK, 2);
        }
        return addWallMessageCmd;
    }
	
	public Command getDeleteWallMessageCmd(){
        if (deleteWallMessageCmd == null) {
        	deleteWallMessageCmd = new Command("Delete Message", Command.OK, 2);
        }
        return deleteWallMessageCmd;
    }
	
	public Command getFileCmd(){
        if (addFileCmd == null) {
        	addFileCmd = new Command("Download file", Command.OK, 2);
        }
        return addFileCmd;
    }
	
	private void fillWallMessages(ChoiceGroup wl, Wall w){
		Iterator it=w.getWallMessages().iterator();
		while (it.hasNext()){
			WallMessage wm=(WallMessage) it.next();
			wl.append(wm.toString(),null);
		}
	}
	
	public void refresh(){
		this.deleteAll();
		wallList.deleteAll();
		fillWallMessages(wallList, wallMng);
		this.append(wallList);
	}
	
	public void deleteMessage(){
		int index=wallList.getSelectedIndex();
		WallMng w=new WallMng();
		w.setWall(wallMng);
		w.delete(wallList.getString(index));
		refresh();
		
	}
	
	public String getWallOwner(){
		return wallMng.getNickname();
	}
	
	public void loadMessages(){
		wallMng.setWallMessages(UtilityDatastore.loadAllWall(msnagent.getLocalName()));
		fillWallMessages(wallList, wallMng);
	}
	
	public String getSelectedFilePath(){
		String wallS=wallList.getString(wallList.getSelectedIndex());
		
		String response="";
		
		Iterator it=wallMng.getWallMessages().iterator();
		while (it.hasNext()){
			WallMessage wm=(WallMessage) it.next();
			if (wallS.equals(wm.toString())){
				response=wm.getLink();
			}
		}	
		return response;
	}

}
