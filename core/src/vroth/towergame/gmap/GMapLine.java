package vroth.towergame.gmap;

import vroth.towergame.GConfig;
import vroth.towergame.gobject.GObject;

public class GMapLine {
	private GObject[] lineBackground = null;
	private GObject[] lineForeground = null;
	
	public GMapLine() {
		this.lineBackground = new GObject[GConfig.MAP_WIDTH];
		this.lineForeground = new GObject[GConfig.MAP_WIDTH];
	}
	
	public GObject getObjectBackground(int x) {
		return lineBackground[x];
	}
	
	public void setObjectBackground(int x, GObject object) {
		this.lineBackground[x] = object;
	}
	
	public GObject getObjectForeground(int x) {
		return lineForeground[x];
	}
	
	public void setObjectForeground(int x, GObject object) {
		this.lineForeground[x] = object;
	}
}
