package msn.client;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Random;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Image;

import msn.client.gui.ListFileSystem;


public class Navigator {

    private String currDirName;
    public final static String UP_DIRECTORY = "..";
    public final static String MEGA_ROOT = "/";
    public final static String SEP_STR = "/";
    public final static char SEP = '/';
    public final static int CHUNK_SIZE = 1024;

    public Navigator() {
        currDirName = MEGA_ROOT;
    }

    public void reset() {
        currDirName = MEGA_ROOT;
    }

    public void showCurrDir(ListFileSystem browser) {
        Enumeration e;
        FileConnection currDir = null;
        browser.deleteAll();
        try {
            if (MEGA_ROOT.equals(currDirName)) {
                e = FileSystemRegistry.listRoots();
                browser.setTitle(currDirName);
            } else {
                currDir = (FileConnection) Connector.open("file:///" + currDirName);
                e = currDir.list();
                browser.setTitle(currDirName);
                browser.append(UP_DIRECTORY, null);
            }
            while (e.hasMoreElements()) {
                String fileName = (String) e.nextElement();
                if (fileName.charAt(fileName.length() - 1) == SEP) {
                    browser.append(fileName, null);
                } else {
                    browser.append(fileName, null);
                }
            }
            if (currDir != null) {
                currDir.close();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void traverseDirectory(String fileName) {
        if (currDirName.equals(MEGA_ROOT)) {
            if (fileName.equals(UP_DIRECTORY)) {
                return;
            }
            currDirName =fileName;
        } else if (fileName.equals(UP_DIRECTORY)) {
            int i = currDirName.lastIndexOf(SEP, currDirName.length() - 2);
            if (i != -1) {
                currDirName = currDirName.substring(0, i + 1);
            } else {
                currDirName = MEGA_ROOT;
            }
        } else {
            currDirName = currDirName + fileName;
        }
    }
    
    public String getCurrentDirectory(){
    	return currDirName;
    }

    public Image showImageFile(String fileName) {
        Image img = null;
        try {
            FileConnection fc = (FileConnection) Connector.open("file:///" + currDirName + fileName);
            if (!fc.exists()) {
                throw new IOException("File does not exists");
            }
            InputStream fis = fc.openInputStream();
            img = Image.createImage(fis);
            fis.close();
            fc.close();


        } catch (Exception e) {
        }
        return img;
    }

    public Image showImageFileFromPath(String path) {
        Image img = null;
        try {
            FileConnection fc = (FileConnection) Connector.open("file:///" + path);
            if (!fc.exists()) {
                throw new IOException("File does not exists");
            }
            InputStream fis = fc.openInputStream();
            img = Image.createImage(fis);
            fis.close();
            fc.close();


        } catch (Exception e) {
        }
        return img;
    }


    public String getPath(String fileName){
        return currDirName + fileName;
    }

    public InputStream getInputStream(String fileName) {
        InputStream fis=null;
        try {
            FileConnection fc = (FileConnection) Connector.open("file:///" + currDirName + fileName);
            if (!fc.exists()) {
                throw new IOException("File does not exists");
            }
            fis = fc.openInputStream();
            fc.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fis;
    }
    
    public static byte[] getFileInBytes(String path) throws IOException {
    	byte[] filebBs=new byte[0];
    	FileConnection fc = (FileConnection) Connector.open("file:///"+path , Connector.READ_WRITE);
    	if(fc.exists()) 
        { 
            filebBs=new byte[(int)fc.fileSize()]; 
            InputStream is=fc.openInputStream(); 
            is.read(filebBs, 0, filebBs.length); 
            is.close();
            fc.close(); 
        }else{
        	throw new IOException("File not found");
        }
      
        return filebBs;
    }
    
    public static boolean createFileOnMem(byte[]  fileBs,String fileName) throws Exception 
    { 
        boolean isCreated=false; 
        try { 
        	FileConnection fileConnection;
        	Enumeration  e = FileSystemRegistry.listRoots();
        	String rootdir = (String) e.nextElement();
        	fileConnection = (FileConnection) Connector.open("file:///" + rootdir + "MSN/" ,Connector.READ_WRITE); 
            if(!fileConnection.exists()){
            	fileConnection.mkdir(); 
            }
            fileConnection.close();
            
            fileConnection = (FileConnection) Connector.open("file:///" + rootdir + "MSN/"+fileName , Connector.READ_WRITE); 
            Random generator = new Random();
            while(fileConnection.exists()){
            	int i=generator.nextInt();
                String fN=""+ i  +"_" + fileName;
                fileConnection = (FileConnection) Connector.open("file:///" + rootdir + "MSN/"+fN , Connector.READ_WRITE); 
            }
            fileConnection.create();

            OutputStream outputStream=fileConnection.openOutputStream(); 
            outputStream.write(fileBs); 
            outputStream.flush(); 
            outputStream.close(); 
            fileConnection.close(); 
            isCreated=true; 
        } catch (Exception e) { 
        } 
             

        return isCreated; 
             
    } 
}
