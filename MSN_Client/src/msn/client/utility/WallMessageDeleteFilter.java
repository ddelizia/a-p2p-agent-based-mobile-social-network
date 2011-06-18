package msn.client.utility;

import java.io.IOException;

import msn.client.WallMessage;

public class WallMessageDeleteFilter implements
		javax.microedition.rms.RecordFilter {
	String wallm;

	public WallMessageDeleteFilter(String wallm) {
		super();
		this.wallm = wallm;
	}

	public boolean matches(byte[] arg0) {

		WallMessage wm;
		try {
			wm = UtilityData.fromByteArrayWallMessage(arg0);
			if (wm.toString().equals(wallm))
				return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
}
