package msn.client.utility;

public class FriendFilter implements javax.microedition.rms.RecordFilter {
	String friend;
	
	public FriendFilter(String friend){
		this.friend=friend;	
	}

	public boolean matches(byte[] arg0) {
		return friend.equals(new String(arg0));
	}
	


}
