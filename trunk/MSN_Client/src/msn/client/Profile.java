package msn.client;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import javax.microedition.lcdui.Image;


public class Profile {

    private String nickname;
    private String name=null;
    private String surname=null;
    private Image img=null;
    
    public Profile (){
    	
    }
    
    public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public void setImg(Image img) {
		this.img = img;
	}
	public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Image getImg() {
        return img;
    }
    
    public String getNickname(){
    	return nickname;
    }

	public String toString() {
		return "Profile [img=" + img + ", name=" + name + ", nickname="
				+ nickname + ", surname=" + surname + "]";
	}





}
