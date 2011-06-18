package msn.client.gui;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.TextField;

import msn.client.MSNAgent;
import msn.client.Profile;
import msn.client.managers.ProfileMng;

public class FormProfile extends Form{
	
	private CommandListener commandListener;
	private Profile profile;
	
    private ImageItem img;
    private TextField name;
    private TextField surname;
    private Command updateProfileCmd;

	public FormProfile(CommandListener cl, MSNAgent a, boolean isMyProfile, Command backCmd, Command fileCmd) {
		super("" + a.getLocalName() + ": MSN. Profile");

		commandListener=cl;
		profile=a.getProfileMng().getProfile();
		
		this.addCommand(backCmd);
		this.addCommand(fileCmd);
		this.addCommand(getUpdateProfileCmd());
		this.setCommandListener(commandListener);
		
		ProfileMng profileMng=new ProfileMng();
		profileMng.setProfile(a.getProfileMng().getProfile());

		if(isMyProfile){
			if (profileMng.loadProfile() == false) {
				img = new ImageItem("Profile Image", null, ImageItem.LAYOUT_CENTER, null);
				name = new TextField("Name", profile.getName(), 100, TextField.ANY);
				surname = new TextField("Surname", profile.getSurname(), 100, TextField.ANY);
			} else {
				img = new ImageItem("Profile Image", profile.getImg(), ImageItem.LAYOUT_CENTER, null);
				name = new TextField("Name", profile.getName(), 100, TextField.ANY);
				surname = new TextField("Surname", profile.getSurname(), 100, TextField.ANY);
			}
		}
		else{
			img = new ImageItem("Profile Image", profile.getImg(), ImageItem.LAYOUT_CENTER, null);
			name = new TextField("Name", profile.getName(), 100, TextField.ANY);
			surname = new TextField("Surname", profile.getSurname(), 100, TextField.ANY);
		}
		
        this.append(img);
        this.append(name);
        this.append(surname);
        
        name.setString(profile.getName());
        surname.setString(profile.getSurname());
        img.setImage(profile.getImg());
	}
	
	public FormProfile(CommandListener cl, String user, Profile p, Command backCmd) {
		super(user + ": MSN. Profile");

		commandListener=cl;
		profile=p;
		
		this.addCommand(backCmd);
		this.setCommandListener(commandListener);

		img = new ImageItem("Profile Image", profile.getImg(), ImageItem.LAYOUT_CENTER, null);
		name = new TextField("Name", profile.getName(), 100, TextField.ANY);
		surname = new TextField("Surname", profile.getSurname(), 100, TextField.ANY);
		
        this.append(img);
        this.append(name);
        this.append(surname);
        
        name.setString(profile.getName());
        surname.setString(profile.getSurname());
        img.setImage(profile.getImg());
	}

    public Command getUpdateProfileCmd(){
        if (updateProfileCmd == null ){
            updateProfileCmd = new Command ("Update Profile", Command.OK, 1);
        }
        return updateProfileCmd;
    }
    
    public String getName(){
    	return name.getString();
    }
    
    public String getSurname(){
    	return surname.getString();
    }
    
    public Image getImage(){
    	return img.getImage();
    }

	public void setImage(Image im) {
		img.setImage(im);	
	}
    
}
