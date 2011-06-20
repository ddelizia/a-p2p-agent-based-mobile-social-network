package msn.client;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import msn.client.utility.UtilityData;
/**
 *
 * @author danilo
 */
public class WallMessage {
	
	private final String NULL_MESSAGE="------------";

    private String nickname=null;
    private String link=null;
    private String msg=null;
    private Calendar date=null;
    private Hashtable tags=new Hashtable();

    public WallMessage (String nickname, String msg, String link, Calendar pubDate){
        this.nickname=nickname;
        this.msg=msg;
        this.link=link;
        this.date=pubDate;
        messageRender();
    }

    public WallMessage (String nickname,String msg, String link){
        this.nickname=nickname;
        this.msg=msg;
        this.link=link;
        date = Calendar.getInstance();
        Date d=new Date();
        date.setTime(d);
        messageRender();
    }

    public WallMessage (String nickname,String msg){
        this.nickname=nickname;
        this.msg=msg;
        this.tags=new Hashtable();
        date = Calendar.getInstance();
        Date d=new Date();
        date.setTime(d);
        messageRender();
    }

    public WallMessage (String nickname){
        this.nickname=nickname;
        date = Calendar.getInstance();
        Date d=new Date();
        date.setTime(d);
        messageRender();
    }
    
    public WallMessage (){ //To create from byte
    }
    
    private void messageRender(){
    	if (nickname!=null && nickname.equals(""))
    		nickname=NULL_MESSAGE;
    	if (link!=null && link.equals(""))
    		link=NULL_MESSAGE;
    	if (msg!=null && msg.equals(""))
    		msg=NULL_MESSAGE;  	
    }
    
    
    //METODI ACCESSORI +EQUALS
    public String getLink(){
        return link;
    }

    public String getNickname(){
        return nickname;
    }
    
    public String getMsg(){
        return msg;
    }
    
    public Calendar getDate(){
        return date;
    }
    
    public long getDateLong(){
        return date.getTime().getTime();
    }

    public Hashtable getTags(){
        return tags;
    }
    
    public String getTagsString(){
    	Enumeration e=tags.elements();
    	String s;
    	if (tags.size()==0)
    		s="";
    	else
    		s=(String)e.nextElement();
    	while (e.hasMoreElements()){
    		s+=","+(String)e.nextElement();
    	}
    	return s;
    }

    public boolean containsTag (String s){
        return tags.contains(s);
    }

    public void addTag(String s){
        tags.put(UtilityData.replaceAll(s," ", "").toUpperCase(),s);
    }

    public void setLink(String s){
        link=s;
    }

    public boolean equals (Object o){
        if (! (o instanceof WallMessage) )
            return false;
        WallMessage wm=(WallMessage)o;
        if (wm.getDate().equals(this.getDate()) && wm.getLink().equals(this.getLink()) && wm.getMsg().equals(this.getMsg()))
            return true;
        return false;
    }
    
    public String toString(){
        String taglist="";
        Enumeration en=tags.elements();
        Date d=new Date(getDateLong());
        while (en.hasMoreElements())
            taglist=taglist+","+(String)en.nextElement();
        return "Nickname: "+getNickname()+"\nMessage: "+getMsg()+"\nLink: "+getLink()+"\nTags: "+getTagsString()+"\nData: "+d.toString();
    }
    
    public String getEntireWallMessage (){
    	messageRender();
    	return getNickname()+"|"+getMsg()+"|"+getLink()+"|"+getDateLong()+"|"+getTagsString()+"|";
    }
    
    public void createWallMessageFromString(String s){
    	
    	StringTokenizer st=new StringTokenizer(s, "|");
    	
    	nickname=st.nextToken();
    	msg=st.nextToken();
    	link=st.nextToken();
    	Date d=new Date(Long.parseLong(st.nextToken()));
    	date=Calendar.getInstance();
    	date.setTime(d);
    	
    	String tagsString=null;
    	try {
    		tagsString=st.nextToken(); 	
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (tagsString!=null){
			StringTokenizer st1=new StringTokenizer(tagsString, ",");  	
			while (st1.hasMoreTokens())
				addTag(st1.nextToken());
		}
		
    }

}
