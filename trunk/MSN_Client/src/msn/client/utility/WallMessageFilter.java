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
		System.out.println("Wall message filter started");
		String s=UtilityData.replaceAll(tags," ", "").toUpperCase();
		System.out.println("String: "+s);
		StringTokenizer st=new StringTokenizer(s,",");
		while (st.hasMoreTokens()){
			String token=st.nextToken();
			System.out.println("token: "+token);
			this.tags.add(token);
		}
		System.out.println("Ended");
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
