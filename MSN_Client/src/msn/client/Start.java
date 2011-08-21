package msn.client;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDletStateChangeException;

import jade.MicroBoot;
import jade.core.MicroRuntime;
import jade.core.Agent;
import jade.util.Logger;
import jade.util.leap.Properties;

public class Start extends MicroBoot implements CommandListener {
	private final Command okCommand = new Command("OK", Command.OK, 1);
	private final Command cancelCommand = new Command("Cancel", Command.CANCEL,1);
	private Form form;
	private TextField tf;
	private StringItem si;

	public void startApp() throws MIDletStateChangeException {
		super.startApp();

		form = new Form("Enter nickname:");
		Display.getDisplay(Agent.midlet).setCurrent(form);
		tf = new TextField(null, null, 32, TextField.ANY);
		form.append(tf);
		si = new StringItem(null, null);
		form.append(si);

		form.addCommand(okCommand);
		form.addCommand(cancelCommand);
		form.setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == okCommand) {
			Logger logger = Logger.getMyLogger(this.getClass().getName());
			logger.log(Logger.INFO, "-----Access Request TIME: "+System.currentTimeMillis());
			String name = tf.getString();
			if (!checkName(name)) {
				si.setText("The nickname must be composed of letters and digits only");
			} else {
				try {
					si.setText("Joining msn. Please wait...");
					
					MicroRuntime.startAgent(name,"msn.client.MSNAgent", null);

				} catch (Exception e) {
					si.setText("Nickname already in use");
				}
			}
		} else if (c == cancelCommand) {
			MicroRuntime.stopJADE();
		}
	}

	protected void customize(Properties p) {
		p.setProperty("exitwhenempty", "true");
	}

	private static boolean checkName(String name) {
		if (name == null || name.trim().equals("")) {
			return false;
		}
		return true;
	}

}

