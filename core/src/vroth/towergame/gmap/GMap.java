package vroth.towergame.gmap;

import java.util.ArrayList;

/**
 * This class will implement the map grid, with an updatable height
 * @author vroth
 *
 */
public class GMap {
	private ArrayList<GMapLine> mapLines = null;
	
	public GMap() {
		mapLines = new ArrayList<GMapLine>();
	}

	public void insertGMapLine(GMapLine mapLine) {
		mapLines.add(mapLine);
	}
	
	public GMapLine getMapLine(int y) {
		if(mapLines.size() > y)
			return mapLines.get(y);
		return null;
	}
}
