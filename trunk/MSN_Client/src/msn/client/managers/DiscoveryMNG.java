package msn.client.managers;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE

import java.util.Hashtable;

import msn.client.MSNAgent;

public class DiscoveryMNG {

    MSNAgent myAgent;

    public DiscoveryMNG(MSNAgent myAgent) {
        this.myAgent=myAgent;
    }

    public String[] getPartecipants(){
        return myAgent.getArrayPartecipants();
    }

    public Hashtable getMap(){
        Hashtable ht=new Hashtable();
        String l[]=myAgent.getArrayPartecipants();
        for (int i=0; i<l.length; i++){
            ht.put(l[i], l[i]);
        }
        return ht;
    }

}
