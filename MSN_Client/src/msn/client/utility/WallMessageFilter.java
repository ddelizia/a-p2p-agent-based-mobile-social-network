package msn.client.utility;

import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;

import java.io.IOException;
import java.util.Hashtable;

import msn.client.StringTokenizer;
import msn.client.WallMessage;

public class WallMessageFilter implements javax.microedition.rms.RecordFilter {
	ArrayList tags;
	
	public WallMessageFilter(String tags){
		super();
		this.tags=new ArrayList();
		String s=UtilityData.replaceAll(tags," ", "").toUpperCase();
		StringTokenizer st=new StringTokenizer(s,",");
		while (st.hasMoreTokens()){
			String token=st.nextToken();
			this.tags.add(token);
		}
	}

	public boolean matches(byte[] arg0) {
		boolean contains=false;
		try {
			WallMessage wm=UtilityData.fromByteArrayWallMessage(arg0);
			Hashtable hm=wm.getTags();
			Iterator it=tags.iterator();
			while (it.hasNext()){
				String s=(String)it.next();
				if(hm.containsKey(s))
					contains=true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contains;
	}
}
