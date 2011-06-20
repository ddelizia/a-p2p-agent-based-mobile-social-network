package msn.client.utility;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import jade.util.leap.ArrayList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import msn.client.Profile;
import msn.client.WallMessage;
import msn.client.managers.ProfileMng;

public class UtilityDatastore {
	
    public static final int NO_DATA = 10;
    public static final int SI_DATA = 20;
    
    private static int byteToInt (byte[] b){
    	return Integer.parseInt(new String(b));	
    }
    
    private static byte[] intToByte (int i){
    	String b=new String(""+i);
    	return b.getBytes();
    }

//PROFILE
	public static final int HAVE_RECORD_NAME = 1;
	public static final int RECORD_NAME = 2;
	public static final int HAVE_RECORD_SURNAME = 3;
    public static final int RECORD_SURNAME = 4;
    public static final int HAVE_RECORD_IMG = 5;
    public static final int RECORD_WIDTH = 6;
    public static final int RECORD_HEIGHT = 7;
    public static final int RECORD_IMAGE = 8;
    public static final int NUM_OF_RECORDS = 8;
   
//PROFILE
    public static Profile loadProfile(String dbname) {
	    Image img=null;
	    int imgW=0;
	    int imgH=0;
	    String name=null;
	    String surname=null;
	        
        try {
            RecordStore db = RecordStore.openRecordStore(dbname + "profile", true);

            // retriving name
            if(byteToInt(db.getRecord(HAVE_RECORD_NAME))==SI_DATA){
            	byte[] record = db.getRecord(RECORD_NAME);
            	name = new String(record);
            }

            // retriving  surname
            if(byteToInt(db.getRecord(HAVE_RECORD_SURNAME))==SI_DATA){
            	byte[] record = db.getRecord(RECORD_SURNAME);
            	surname = new String(record);
            }
            
            // retriving  image
            if(byteToInt(db.getRecord(HAVE_RECORD_IMG))==SI_DATA){
            	
	            // RECORD #3 -> width
            	byte[] record = db.getRecord(RECORD_WIDTH);
	            imgW = Integer.parseInt(new String(record));
	
	            // RECORD #4 -> height
	            record = db.getRecord(RECORD_HEIGHT);
	            imgH = Integer.parseInt(new String(record));
	
	            // RECORD #5 -> image
	            record = db.getRecord(RECORD_IMAGE);
	            ByteArrayInputStream bin = new ByteArrayInputStream(record);
	            DataInputStream din = new DataInputStream(bin);
	
	            int len=imgW*imgH;
	            int[] rawdata = new int[len];
	            for (int k = 0; k < rawdata.length; k++) 
	               	rawdata[k] = din.readInt();
	
	            img = Image.createRGBImage(rawdata, imgW, imgH, false);
	            bin.reset();
	            din.close();
	            
            }

            db.closeRecordStore();


        } catch (RecordStoreException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
        	ex.printStackTrace();
            return null;
		}
        ProfileMng profileMng=new ProfileMng();
        profileMng.setProfile(ProfileMng.generateProfile(dbname,false));
        profileMng.updateProfile(name, surname, img);
        return profileMng.getProfile();
    }

    public static boolean saveProfile(Profile profile) {
    	
    	String name=profile.getName();
        String surname=profile.getSurname();
        Image img=profile.getImg();
        int imgH=0;
        int imgW=0;
        if (img!=null){
        	imgH=img.getHeight();
            imgW=img.getWidth();	
        }

        try {

            
            RecordStore db = RecordStore.openRecordStore(profile.getNickname()+"profile", true);

            // name
            if (name==null || name.equals(""))
            	db.setRecord(HAVE_RECORD_NAME, intToByte(NO_DATA), 0, intToByte(NO_DATA).length);
            else{
            	db.setRecord(HAVE_RECORD_NAME, intToByte(SI_DATA), 0, intToByte(SI_DATA).length);
            	byte[] record = name.getBytes();
                db.setRecord(RECORD_NAME, record, 0, record.length);
            }
            
            // surname
            if (surname==null || surname.equals(""))
            	db.setRecord(HAVE_RECORD_SURNAME, intToByte(NO_DATA), 0, intToByte(NO_DATA).length);
            else{
            	db.setRecord(HAVE_RECORD_SURNAME, intToByte(SI_DATA), 0, intToByte(SI_DATA).length);
            	byte[] record = surname.getBytes();
                db.setRecord(RECORD_SURNAME, record, 0, record.length);
            }
            
            // image
            if (img==null)
            	db.setRecord(HAVE_RECORD_IMG, intToByte(NO_DATA), 0, intToByte(NO_DATA).length);
            else{
            	db.setRecord(HAVE_RECORD_IMG, intToByte(SI_DATA), 0, intToByte(SI_DATA).length);
            	
            	//WIDTH
            	String b=new String(""+imgW);
                byte[] record1 = b.getBytes();
                db.setRecord(RECORD_WIDTH, record1, 0, record1.length);

                // height
                b=new String(""+imgH);
                byte[] record2 = b.getBytes();
                db.setRecord(RECORD_HEIGHT, record2, 0, record2.length);

                // img
                int[] imgRgbData = new int[imgW * imgH];
                img.getRGB(imgRgbData, 0, imgW, 0, 0, imgW, imgH);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                for (int i = 0; i < imgRgbData.length; i++) {
                    dos.writeInt(imgRgbData[i]);
                }
                // Open record store, create if it doesn't exist
                db.setRecord(RECORD_IMAGE,baos.toByteArray(), 0, baos.toByteArray().length);
            }

            db.closeRecordStore();

        } catch(IOException ex){
            ex.printStackTrace();
            return false;
        }catch(RecordStoreException rse){
            rse.printStackTrace();
            return false;
        }
        return true;
    }

    public static void initProfile(String dbname) {
    	
    	
        try {
            
            RecordStore db = RecordStore.openRecordStore(dbname+"profile", true);
            
            for (int i=0; i<NUM_OF_RECORDS; i++){
            	db.addRecord(intToByte(NO_DATA), 0, intToByte(NO_DATA).length);
            }
            db.closeRecordStore();

        }catch(RecordStoreException rse){
            rse.printStackTrace();
        }
    }

    
//WALL
    public static final int RECORD_MESSAGE_NUMBER = 1;
    
    public static boolean saveWallMessage(WallMessage wm) {
    	//adding in last position
    	RecordStore db;
		try {
			db = RecordStore.openRecordStore(wm.getNickname() + "wall", true);
			byte[] b;
			b = UtilityData.toByteArray(wm);

	    	db.addRecord(b, 0, b.length);
	    	db.closeRecordStore();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
    	
    	return true;
        
    }

    public static ArrayList loadAllWall(String dbname) {
    	int dim=0;
    	try {
            RecordStore db = RecordStore.openRecordStore(dbname + "wall", true);
            dim=db.getNumRecords();
            db.closeRecordStore();     
    	} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList();
		}
    	
    	return loadLastWallMessages(dbname, dim);
    }
    
    public static ArrayList loadLastWallMessages(String dbname, int num) {
    	ArrayList wallMessages=new ArrayList();
    	int count=0;
    	try {
            RecordStore db = RecordStore.openRecordStore(dbname + "wall", true);
            RecordEnumeration re=db.enumerateRecords(null,null, false);
            while (re.hasNextElement() && count<num){
            	byte[] record = re.nextRecord();
            	WallMessage wm=UtilityData.fromByteArrayWallMessage(record);
            	wallMessages.add(wm);
            	count++;
            }
            db.closeRecordStore();     
    	} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	
    	return wallMessages;
    }
    
    public static ArrayList loadAllWallResearch(String dbname,String tags) {
    	int dim=0;
    	try {
            RecordStore db = RecordStore.openRecordStore(dbname + "wall", true);
            dim=db.getNumRecords();
            db.closeRecordStore();     
    	} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList();
		}
    	
    	return loadLastWallMessagesResearch(dbname, tags,dim);
    }
    
    public static ArrayList loadLastWallMessagesResearch(String dbname, String tags, int num) {
    	ArrayList wallMessages=new ArrayList();
    	int count=0;
    	try {
            RecordStore db = RecordStore.openRecordStore(dbname + "wall", true);
            RecordEnumeration re=db.enumerateRecords(new WallMessageFilter(tags),null, false);
            while (re.hasNextElement() && count<num){
            	byte[] record = re.nextRecord();
            	WallMessage wm=UtilityData.fromByteArrayWallMessage(record);
            	wallMessages.add(wm);
            	count++;
            }
            db.closeRecordStore();     
    	} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	
    	return wallMessages;
    }
    
    public static void removeMessage(String dbname, String wallm) {
    	try {
            RecordStore db = RecordStore.openRecordStore(dbname + "wall", true);
            RecordEnumeration re=db.enumerateRecords(new WallMessageDeleteFilter(wallm),null, false);
            while (re.hasNextElement()){
            	int index = re.nextRecordId();
            	db.deleteRecord(index);
            }
            db.closeRecordStore();     
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }


}
