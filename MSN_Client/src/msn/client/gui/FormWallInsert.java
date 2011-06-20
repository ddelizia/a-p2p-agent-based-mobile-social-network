package msn.client.gui;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import msn.client.MSNAgent;
import msn.client.StringTokenizer;
import msn.client.WallMessage;

public class FormWallInsert extends Form {
	
	private CommandListener commandListener;
	
    private TextField txtMessage;
    private TextField txtTags;
    private TextField txtPath;
    private Command addMessageCmd;
    private MSNAgent agent;

	public FormWallInsert(CommandListener cl, MSNAgent a, Command backCmd, Command fileCmd) {
		super("" + a.getLocalName() + ": MSN. Add Message Wall");
		
		commandListener=cl;
		agent=a;
		
		this.addCommand(backCmd);
		this.addCommand(fileCmd);
		this.addCommand(getAddMessageCmd());
		
        txtMessage=new TextField("Message", "", 200, TextField.ANY);
        txtTags=new TextField("Tags", "", 200, TextField.ANY);
        txtPath=new TextField("Link", "", 200, TextField.UNEDITABLE);
        this.append(txtMessage);
        this.append(txtTags);
        this.append(txtPath);
		
		this.setCommandListener(commandListener);
	}
	
	public Command getAddMessageCmd() {
        if (addMessageCmd== null){
            addMessageCmd = new Command("Public on wall", Command.OK, 2);
        }
        return addMessageCmd;
    }
	
	public WallMessage createWallMessage(){
		WallMessage wm=null;
		String message=txtMessage.getString();
        String tags=txtTags.getString().trim();
        String path=txtPath.getString();
        wm=new WallMessage(agent.getLocalName(),message);
        wm.setLink(path);
        StringTokenizer st=new StringTokenizer(tags,",");
        while (st.hasMoreTokens()){
        	String tok=st.nextToken();
            wm.addTag(tok);
        }
        return wm;
	}

	public void setPath(String path) {
		txtPath.setString(path);		
	}

}
