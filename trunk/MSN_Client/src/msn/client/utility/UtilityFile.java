package msn.client.utility;

//#J2SE_EXCLUDE_FILE
//#PJAVA_EXCLUDE_FILE
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class UtilityFile {
	
	public final static int CHUNK_SIZE = 1024;
	
	public static byte[] fileRetriver(String link){

        byte[] filedata = new byte[0];
        try {
            FileConnection fc = (FileConnection) Connector.open("file:///" +link);
            if (!fc.exists()) {
                throw new IOException("File does not exists");
            }
            InputStream fis = fc.openInputStream();
            long overallSize = fc.fileSize();
            int length = 0;

            while (length < overallSize) {//converting the selected file to bytes
                byte[] data = new byte[CHUNK_SIZE];
                int readAmount = fis.read(data, 0, CHUNK_SIZE);
                byte[] newFileData = new byte[filedata.length + CHUNK_SIZE];
                System.arraycopy(filedata, 0, newFileData, 0, length);
                System.arraycopy(data, 0, newFileData, length, readAmount);
                filedata = newFileData;
                length += readAmount;
            }
            fis.close();
            fc.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return filedata;
    }
	
    public static Image createThumbnail(Image image) {
        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();

        int thumbWidth = 64;
        int thumbHeight = -1;

        if (thumbHeight == -1) {
            thumbHeight = thumbWidth * sourceHeight / sourceWidth;
        }

        Image thumb = Image.createImage(thumbWidth, thumbHeight);
        Graphics g = thumb.getGraphics();

        for (int y = 0; y < thumbHeight; y++) {
            for (int x = 0; x < thumbWidth; x++) {
                g.setClip(x, y, 1, 1);
                int dx = x * sourceWidth / thumbWidth;
                int dy = y * sourceHeight / thumbHeight;
                g.drawImage(image, x - dx, y - dy, Graphics.LEFT | Graphics.TOP);
            }
        }
        Image immutableThumb = Image.createImage(thumb);
        return immutableThumb;
    }

}
