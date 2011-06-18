package msn.client.gui;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import msn.client.MSNAgent;

public class FormMain extends Form{
	
    private ChoiceGroup selection = new ChoiceGroup("Menú", ChoiceGroup.EXCLUSIVE);
    private TextField resTextField;
    private Command exitCmd;
    private Command okCmd;
    
    public final String DISCOVERY = "Discovery";
    public final String FRIENDS = "Friends";
    public final String PROFILE = "Profile";
    public final String NOTIFICATION = "Notification";
    public final String WALL = "Wall";
    
    private GuiCommandListener commandListener;
    
    public FormMain(GuiCommandListener cl, MSNAgent a, Command search) {
		super("" + a.getLocalName() + ": MSN. Main");
		
		commandListener=cl;
		
		fillSelection();
		this.append(selection);
		this.append(getResTextField());
		this.addCommand(search);
        this.addCommand(getOkCmd());
        this.addCommand(getExitCmd());
        this.setCommandListener(commandListener);
	}
    
    public Command getExitCmd() {
        if (exitCmd == null) {
            exitCmd = new Command("Exit", Command.EXIT, 4);
        }
        return exitCmd;
    }

    public Command getOkCmd() {
        if (okCmd == null) {
            okCmd = new Command("OK", Command.OK, 1);
        }
        return okCmd;
    }
    
    public String getSelection(){
    	return selection.getString(selection.getSelectedIndex());
    }
    
    public TextField getResTextField(){
    	if (resTextField == null) {
    		resTextField = new TextField("Search", "", 200, TextField.ANY);
        }
        return resTextField;
    }
    
    //adding commands
    private void fillSelection() {
        selection.append(DISCOVERY, null);
        selection.append(FRIENDS, null);
        selection.append(NOTIFICATION, null);
        selection.append(PROFILE, null);
        selection.append(WALL,null);
    }
	
    
    
}
