package msn.client.utility;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.lcdui.Image;

import msn.client.Profile;
import msn.client.Wall;
import msn.client.WallMessage;
import msn.client.managers.ProfileMng;
import msn.client.managers.WallMng;

public class UtilityData {
	public static final String NEXT_DATA_NO = "dataNO";
	public static final String NEXT_DATA_SI = "dataSI";

	// WALL MESSAGE
	public static byte[] toByteArray(WallMessage wm) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.writeUTF(wm.getEntireWallMessage());
			
		dos.flush();

		return baos.toByteArray();
	}

	public static WallMessage fromByteArrayWallMessage(byte[] data)
			throws IOException {

		ByteArrayInputStream baisStream = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(baisStream);
		
		String m = dis.readUTF();
		
		WallMessage wm=new WallMessage();
		
		wm.createWallMessageFromString(m);

		dis.close();
		baisStream.close();
		
		return wm;
	}

	// PROFILE
	public static byte[] toByteArray(Profile p) throws IOException {

		String nickname = p.getNickname();
		String name = p.getName();
		String surname = p.getSurname();
		Image img = p.getImg();
		int imgH = 0;
		int imgW = 0;
		if (img != null) {
			imgH = img.getHeight();
			imgW = img.getWidth();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeUTF(nickname);
		if (name == null)
			dos.writeUTF(NEXT_DATA_NO);
		else {
			dos.writeUTF(NEXT_DATA_SI);
			dos.writeUTF(name);
		}
		if (surname == null)
			dos.writeUTF(NEXT_DATA_NO);
		else {
			dos.writeUTF(NEXT_DATA_SI);
			dos.writeUTF(surname);
		}
		if (img != null) {
			dos.writeUTF(NEXT_DATA_SI);
			dos.writeInt(imgW);
			dos.writeInt(imgH);

			int[] imgRgbData = new int[imgW * imgH];
			img.getRGB(imgRgbData, 0, imgW, 0, 0, imgW, imgH);
			for (int i = 0; i < imgRgbData.length; i++) {
				dos.writeInt(imgRgbData[i]);
			}
		} else {
			dos.writeUTF(NEXT_DATA_NO);
		}
		dos.flush();
		byte [] byteToReturn=baos.toByteArray();
		dos.close();
		baos.close();
		return byteToReturn;
	}

	public static Profile fromByteArrayProfile(byte data[], boolean isMy) throws IOException {
		Image img = null;
		int imgW = 0;
		int imgH = 0;
		String name = null;
		String surname = null;

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);

		String nickname = dis.readUTF();
		if (dis.readUTF().equals(NEXT_DATA_SI))
			name = dis.readUTF();
		if (dis.readUTF().equals(NEXT_DATA_SI))
			surname = dis.readUTF();

		String haveImage= dis.readUTF();
		if (haveImage.equals(NEXT_DATA_NO)) {
			img = null;
		} else {
			imgW = dis.readInt();
			imgH = dis.readInt();

			int len = imgH * imgW;

			int[] rawdata = new int[len];
			for (int k = 0; k < rawdata.length; k++) {
				rawdata[k] = dis.readInt();
			}

			img = Image.createRGBImage(rawdata, imgW, imgH, false);
		}

		dis.close();
		bais.close();

		ProfileMng pm=new ProfileMng();
		pm.setProfile(ProfileMng.generateProfile(nickname,isMy));
		pm.updateProfile(name, surname, img);
		return pm.getProfile();

	}

	// WALL
	public static byte[] toByteArray(Wall w) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		String nick = w.getNickname();
		ArrayList al = w.getWallMessages();

		dos.writeUTF(nick);

		int storedMessages = al.size();
		dos.writeInt(storedMessages);

		Iterator iter = al.iterator();

		while (iter.hasNext()) {
			WallMessage wm = (WallMessage) iter.next();
			byte[] b = toByteArray(wm);
			dos.writeInt(b.length);
			for (int i=0;i<b.length;i++)
				dos.writeByte(b[i]);
		}

		dos.flush();
		byte []b=baos.toByteArray();
		dos.close();
		baos.close();
		return b;
	}

	public static Wall fromByteArrayWall(byte data[],boolean isMy) throws IOException {
		Wall w;

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		
		WallMng wallmng=new WallMng();
		w = WallMng.wallFromDb(dis.readUTF(), isMy);
		wallmng.setWall(w);
		
		int storedMessages = dis.readInt();

		for (int i = 0; i < storedMessages; i++) {
			int len = dis.readInt();
			byte[] b = new byte[len];
			for (int k = 0; k < len; k++)
				b[k] = dis.readByte();
			WallMessage wm = fromByteArrayWallMessage(b);
			wallmng.add(wm,isMy);
		}

		dis.close();
		bais.close();

		return w;
	}
	
	public static String replaceAll(String content, String this1,String withthis){
		
		int from = 0;
		StringBuffer sb = new StringBuffer();
		int index  = -1;
		while(true){
			index = content.indexOf(this1,from);
			if(index!=-1){
				sb = new StringBuffer();
				String upto = content.substring(0,index);	
				sb.append(upto+withthis);
				String lastbit = content.substring(index+this1.length(),content.length());
				sb.append(lastbit);
				content = sb.toString();
				from = index+this1.length();
			}
			else{
				break;	
			}
		}
		return content;
		
	}
}
