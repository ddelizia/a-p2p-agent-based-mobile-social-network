package msn.client.gui;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import jade.core.Agent;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import msn.client.MSNAgent;
import msn.client.Navigator;
import msn.client.utility.UtilityFile;

public class ListFileSystem extends List {
	
	
	private Navigator nav;
	private CommandListener commandListener;
	
	private FormProfile fp;
	private FormWallInsert fwi;
	
	//private Command fsBack;
	private Command fsView;

	public ListFileSystem(CommandListener cl, MSNAgent a, FormProfile fp, FormWallInsert fwi, Command back) {
		super("" + a.getLocalName() + ": MSN. Navigator", List.IMPLICIT);
		
		commandListener=cl;
		nav=new Navigator();
		
		this.fp=fp;
		this.fwi=fwi;
		
		new Thread(new Runnable() {
            public void run() {
                nav.reset();
                nav.showCurrDir(ListFileSystem.this);
            }
        }).start();
		
		this.addCommand(back);
		this.addCommand(getFsViewCmd());
		this.setCommandListener(commandListener);     
	}

	public Command getFsViewCmd() {
        if (fsView == null) {
            fsView = new Command("Go", Command.SCREEN, 2);
        }
        return fsView;
    }
	
	public void runnerProfile(Displayable d){
		List curr = (List) d;
        final String currFile = curr.getString(curr.getSelectedIndex());
		new Thread(new Runnable() {
            public void run() {
                if (currFile.endsWith(Navigator.SEP_STR) || currFile.equals(Navigator.UP_DIRECTORY)) {
                    nav.traverseDirectory(currFile);
                    nav.showCurrDir(ListFileSystem.this);
                    Display.getDisplay(Agent.midlet).setCurrent(ListFileSystem.this);
                } else {
                	Image image=null;
                    if (currFile.endsWith(".jpg") || currFile.endsWith(".jpeg") || currFile.endsWith(".png")){
                        try {
                            image = UtilityFile.createThumbnail(Image.createImage(nav.getInputStream(currFile)));
                            fp.setImage(image);
                            Display.getDisplay(Agent.midlet).setCurrent(fp);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            System.out.print("Errore recuperando immagine");
                        }
                    }
                    
                }
            }
        }).start();
	}
	
	public void runnerWallInsert(Displayable d){
		List curr = (List) d;
        final String currFile = curr.getString(curr.getSelectedIndex());
		new Thread(new Runnable() {
            public void run() {
                if (currFile.endsWith(Navigator.SEP_STR) || currFile.equals(Navigator.UP_DIRECTORY)) {
                    nav.traverseDirectory(currFile);
                    nav.showCurrDir(ListFileSystem.this);
                    Display.getDisplay(Agent.midlet).setCurrent(ListFileSystem.this);
                } else {
                	fwi.setPath("" + nav.getCurrentDirectory() + ""+ currFile);
                	Display.getDisplay(Agent.midlet).setCurrent(fwi);
                }
            }
        }).start();
	}
	
	
	

}
