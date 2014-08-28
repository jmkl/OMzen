package hello.dcsms.omzen.theme;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class ThemeInfoData {
	ThemeData tDATA;
	ArrayList<Bitmap> tdSS;

	public ThemeData gettDATA() {
		return tDATA;
	}

	public void settDATA(ThemeData tDATA) {
		this.tDATA = tDATA;
	}

	public ArrayList<Bitmap> getTdSS() {
		return tdSS;
	}

	public void setTdSS(ArrayList<Bitmap> tdSS) {
		this.tdSS = tdSS;
	}
}