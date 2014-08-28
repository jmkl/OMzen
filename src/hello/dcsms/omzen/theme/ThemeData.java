package hello.dcsms.omzen.theme;

import android.util.Log;

public class ThemeData {
	public String _NAMATEMA;
	public String _PENGRAJIN;
	public String _KETERANGAN;
	public String _VERSI;
	public String _KONTAK;

	public void set(String n, String p, String ket, String v, String kt) {
		_NAMATEMA = n;
		_PENGRAJIN = p;
		_KETERANGAN = ket;
		_VERSI = v;
		_KONTAK = kt;

	}

	public void LOGDATA() {
		Log.d(getClass().getSimpleName(), _NAMATEMA);
		Log.d(getClass().getSimpleName(), _PENGRAJIN);
		Log.d(getClass().getSimpleName(), _KETERANGAN);
		Log.d(getClass().getSimpleName(), _VERSI);
		Log.d(getClass().getSimpleName(), _KONTAK);

	}

	public String get_NAMATEMA() {
		return _NAMATEMA;
	}

	public void set_NAMATEMA(String _NAMATEMA) {
		this._NAMATEMA = _NAMATEMA;
	}

	public String get_PENGRAJIN() {
		return _PENGRAJIN;
	}

	public void set_PENGRAJIN(String _PENGRAJIN) {
		this._PENGRAJIN = _PENGRAJIN;
	}

	public String get_KETERANGAN() {
		return _KETERANGAN;
	}

	public void set_KETERANGAN(String _KETERANGAN) {
		this._KETERANGAN = _KETERANGAN;
	}

	public String get_VERSI() {
		return _VERSI;
	}

	public void set_VERSI(String _VERSI) {
		this._VERSI = _VERSI;
	}

	public String get_KONTAK() {
		return _KONTAK;
	}

	public void set_KONTAK(String _KONTAK) {
		this._KONTAK = _KONTAK;
	}
}