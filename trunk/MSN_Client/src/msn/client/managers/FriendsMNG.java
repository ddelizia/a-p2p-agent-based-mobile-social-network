package msn.client.managers;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

import msn.client.MSNAgent;
import msn.ontology.MessageTicket;

public class FriendsMNG {

    private String file;
    private Hashtable friendsList;
    private Hashtable requestList;
    private Hashtable answersList;
    private Hashtable deleteList;
    private MSNAgent myAgent;
    private DiscoveryMNG dm;
    private boolean threadStoped;

    public FriendsMNG(MSNAgent myAgent, DiscoveryMNG dm) {
        this.myAgent = myAgent;
        this.file = myAgent.getLocalName();
        this.dm = dm;
        this.friendsList = new Hashtable();
        this.requestList = new Hashtable();
        this.answersList = new Hashtable();
        this.deleteList = new Hashtable();
        this.threadStoped=false;

        getData();

        NotificationSender ns = new NotificationSender();
        Thread t1 = new Thread(ns);
        t1.start();

        AcceptationSender as = new AcceptationSender();
        Thread t2 = new Thread(as);
        t2.start();
        
        FriendDeletionSender fd = new FriendDeletionSender();
        Thread t3 = new Thread(fd);
        t3.start();
    }
    
    public DiscoveryMNG getDiscoveryMNG(){
    	return dm;
    }

    public Hashtable getFriendsMap() {
        return friendsList;
    }
    public Hashtable getAnswersMap() {
        return answersList;
    }
    public Hashtable getDeleteList() {
        return deleteList;
    }

    // GETTING DATA FROM FILES
    public final int DATA_FRIENDSLIST = 0;
    public final int DATA_REQUESTLIST = 1;
    public final int DATA_ANSWERSLIST = 2;
    public final int DATA_DELETELIST = 3;

    public void getData() {
        try {

            RecordStore dbf = RecordStore.openRecordStore(file+"friends", true);
            RecordStore dbr = RecordStore.openRecordStore(file+"request", true);
            RecordStore dba = RecordStore.openRecordStore(file+"answers", true);
            RecordStore dbd = RecordStore.openRecordStore(file+"delete", true);

            getDataFromDb(dbf, friendsList);
            getDataFromDb(dba, answersList);
            getDataFromDb(dbr, requestList);
            getDataFromDb(dbd, deleteList);

            dbf.closeRecordStore();
            dba.closeRecordStore();
            dbr.closeRecordStore();
            dbd.closeRecordStore();
            
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }

    private void getDataFromDb(RecordStore db, Hashtable list) throws Exception{
        
        RecordEnumeration enumDb=db.enumerateRecords(null, null, false);
        
        while (enumDb.hasNextElement()){
            byte byt[] = enumDb.nextRecord();
            String in = new String(byt);
            list.put(in, in);
        }
  
    }

    // SAVING ALL THE LISTS
    public void save() throws Exception {

        RecordStore dbf = RecordStore.openRecordStore(file+"friends", true);
        RecordStore dbr = RecordStore.openRecordStore(file+"request", true);
        RecordStore dba = RecordStore.openRecordStore(file+"answers", true);
        RecordStore dbd = RecordStore.openRecordStore(file+"delete", true);

        saveOnDb(dbf, friendsList);
        saveOnDb(dba, answersList);
        saveOnDb(dbr, requestList);
        saveOnDb(dbd, deleteList);

        dbf.closeRecordStore();
        dba.closeRecordStore();
        dbr.closeRecordStore();
        dbd.closeRecordStore();

    }

    private void saveOnDb(RecordStore db, Hashtable list) throws Exception{

        Enumeration enumTable;
        enumTable = list.elements();

        RecordEnumeration enumDb=db.enumerateRecords(null, null, false);
        while (enumDb.hasNextElement()){
            db.deleteRecord(enumDb.nextRecordId());
        }

        int numberOfSavedElements=0;

        while (enumTable.hasMoreElements()) {
            String s = (String) enumTable.nextElement();
            byte[] b = s.getBytes();
            db.addRecord(b, 0, b.length);
            numberOfSavedElements++;
        }


    }

    public void addFriend(String f) {
        requestList.put(f, f);
    }

    public void acceptFriends(String f) {
        answersList.put(f, f);
        friendsList.put(f, f);
    }

    public void friendAccepted(String f){
        friendsList.put(f, f);
    }

    public void deleteFromAnswersList(String s){
        answersList.remove(s);
    }
    
    public void startDeleteFriend(String s){
    	friendsList.remove(s);
    	deleteList.put(s,s);
    }
    
    public void deleteFriend (String s){
        friendsList.remove(s);
    }

    public String[] getFriends() {
        String list[] = new String[friendsList.size()];

        int i = 0;
        Enumeration enumTable = friendsList.elements();
        while (enumTable.hasMoreElements()) {
            list[i] = (String) enumTable.nextElement();
            i++;
        }

        return list;
    }

    
    // stoping threads at exting from the application
    public void stopThreads() {
        threadStoped=true;
    }
    

    // SENDING NOTIFICATION BEFORE ADDING AS FRIEND
    class NotificationSender implements Runnable {

        public NotificationSender() {
            super();
        }

        public void run() {
            while (!threadStoped) {
                try {
                    if (requestList.size() > 0) {

                        int i = 0;
                        Enumeration enumTable = requestList.elements();
                        while (enumTable.hasMoreElements()) {
                            String current = (String) enumTable.nextElement();
                            i++;

                            boolean connected = dm.getMap().contains(current);

                            if (connected) {
                            	myAgent.sendMessage(current,MSNAgent.FRIEND_REQUEST, MSNAgent.PROTOCOL_FRIEND,new MessageTicket(current, MSNAgent.FRIEND_REQUEST ,myAgent.getLocalName(), new byte[0]),true);
                                requestList.remove(current);
                            }
                        }
                        try {
    						FriendsMNG.this.save();
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
                    }
                    synchronized (this) {
                        wait(10000);
                    }

                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // SENDING ACCEPTATION BEFORE ADDING AS FRIEND
    class AcceptationSender implements Runnable {

        public AcceptationSender() {
            super();
        }

        public void run() {
            
            while (!threadStoped) {
                try {

                    if (answersList.size() > 0) {
                    	int i = 0;
                        Enumeration enumTable = answersList.elements();
                        while (enumTable.hasMoreElements()) {
                            String current = (String) enumTable.nextElement();
                            i++;
                            
                            boolean connected = dm.getMap().contains(current);
                            if (connected) {
                                myAgent.sendMessage(current,MSNAgent.FRIEND_ACCEPTATION, MSNAgent.PROTOCOL_FRIEND,new MessageTicket(myAgent.getLocalName(),MSNAgent.FRIEND_ACCEPTATION ,myAgent.getLocalName(), new byte[0]),false);
                                answersList.remove(current);
                            }
                        }
                        try {
    						FriendsMNG.this.save();
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
                    }

                    synchronized (this) {
                        wait(10000);
                    }

                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
 // DELETE FRIEND FRIEND
    class FriendDeletionSender implements Runnable {

        public FriendDeletionSender() {
            super();
        }

        public void run() {
            
            while (!threadStoped) {
                try {

                    if (deleteList.size() > 0) {
                        int i = 0;
                        Enumeration enumTable = deleteList.elements();
                        while (enumTable.hasMoreElements()) {
                            String current = (String) enumTable.nextElement();
                            i++;

                            boolean connected = dm.getMap().contains(current);
                            if (connected) {
                                myAgent.sendMessage(current,MSNAgent.FRIEND_DELETE, MSNAgent.PROTOCOL_FRIEND,new MessageTicket(myAgent.getLocalName(),MSNAgent.FRIEND_DELETE ,myAgent.getLocalName(), new byte[0]),false);
                                deleteList.remove(current);
                            }
                        }
                        try {
    						FriendsMNG.this.save();
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
                    }

                    synchronized (this) {
                        wait(10000);
                    }

                } catch (InterruptedException ex) {
                	ex.printStackTrace();
                }
            }
        }
    }
}
