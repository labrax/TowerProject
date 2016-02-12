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
		if(x >= 0 && x < GConfig.MAP_WIDTH)
			return lineBackground[x];
		else
			return null;
	}
	
	public void setObjectBackground(int x, GObject object) {
		if(x > GConfig.MAP_WIDTH/2-GConfig.GENERATION_WIDTH/2 || x < GConfig.MAP_WIDTH/2 + GConfig.GENERATION_WIDTH/2)
			this.lineBackground[x] = object;
	}
	
	public GObject getObjectForeground(int x) {
		if(x >= 0 && x < GConfig.MAP_WIDTH)
			return lineForeground[x];
		else
			return null;
	}
	
	public void setObjectForeground(int x, GObject object) {
		if(x > GConfig.MAP_WIDTH/2-GConfig.GENERATION_WIDTH/2 || x < GConfig.MAP_WIDTH/2 + GConfig.GENERATION_WIDTH/2)
			this.lineForeground[x] = object;
	}
}
