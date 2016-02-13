package vroth.towergame.gmap;

import vroth.towergame.GConfig;
import vroth.towergame.gobject.GObject;
import vroth.towergame.gobject.GTile;

public class GMapLine {
	private GTile[] lineBackground = null;
	private GTile[] lineForeground = null;
	private GObject[] lineStaticObjects = null;
	
	public GMapLine() {
		this.lineBackground = new GTile[GConfig.MAP_WIDTH];
		this.lineForeground = new GTile[GConfig.MAP_WIDTH];
		this.lineStaticObjects = new GObject[GConfig.MAP_WIDTH];
	}
	
	public GTile getTileBackground(int x) {
		if(x >= 0 && x < GConfig.MAP_WIDTH)
			return lineBackground[x];
		else
			return null;
	}
	
	public void setTileBackground(int x, GTile tile) {
		//if(x > GConfig.MAP_WIDTH/2-GConfig.GENERATION_WIDTH/2 || x < GConfig.MAP_WIDTH/2 + GConfig.GENERATION_WIDTH/2)
		if(x >= 0 && x < GConfig.MAP_WIDTH)
			this.lineBackground[x] = tile;
	}
	
	public GTile getTileForeground(int x) {
		if(x >= 0 && x < GConfig.MAP_WIDTH)
			return lineForeground[x];
		else
			return null;
	}
	
	public void setTileForeground(int x, GTile tile) {
		//if(x > GConfig.MAP_WIDTH/2-GConfig.GENERATION_WIDTH/2 || x < GConfig.MAP_WIDTH/2 + GConfig.GENERATION_WIDTH/2)
		if(x >= 0 && x < GConfig.MAP_WIDTH)
			this.lineForeground[x] = tile;
	}
	
	public GObject getStaticObject(int x) {
		if(x >= 0 && x < GConfig.MAP_WIDTH)
			return lineStaticObjects[x];
		else
			return null;
	}
	
	public void setStaticObject(int x, GObject object) {
		//if(x > GConfig.MAP_WIDTH/2-GConfig.GENERATION_WIDTH/2 || x < GConfig.MAP_WIDTH/2 + GConfig.GENERATION_WIDTH/2)
		if(x >= 0 && x < GConfig.MAP_WIDTH)
			this.lineStaticObjects[x] = object;
	}
}
