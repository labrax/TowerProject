package vroth.towergame.gmap;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.GConfig;

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

	public Array<Vector2> getPath(Vector2 origin, Vector2 target) {
		
		return null;
	}
	
	public int compare(Vector2 a, Vector2 b) {
		return 0;
	}
	
	public Array<Vector2> getPathGeneration(Vector2 origin, Vector2 target) {
		int[][] distance = new int[GConfig.INITIAL_HEIGHT][];
		Vector2[][] previous = new Vector2[GConfig.INITIAL_HEIGHT][];
		for(int i = 0; i < GConfig.INITIAL_HEIGHT; i++) {
			distance[i] = new int[GConfig.MAP_WIDTH];
			previous[i] = new Vector2[GConfig.MAP_WIDTH];
			for(int j = 0; j < GConfig.MAP_WIDTH; j++) {
				distance[i][j] = Integer.MAX_VALUE;
			}
		}
		PriorityQueue<Vector2> pq = new PriorityQueue<Vector2>();
		distance[(int) origin.y][(int) origin.x] = 0;
		pq.add(origin);
		
		while(!pq.isEmpty()) {
			
		}
		
	}
	
	public void generateMap() {
		int[][] initialMap = new int[GConfig.INITIAL_HEIGHT][];
		for(int i = 0; i < GConfig.INITIAL_HEIGHT; i++)
			initialMap[i] = new int[GConfig.MAP_WIDTH];
		
		Random r = new Random();
		
		boolean done = false;
		while(!done) {
			int a = r.nextInt(100);
			if(a == 0) {
				int x1 = r.nextInt(GConfig.MAP_WIDTH);
				int y1 = r.nextInt(GConfig.INITIAL_HEIGHT);
				int x2 = r.nextInt(GConfig.MAP_WIDTH);
				int y2 = r.nextInt(GConfig.INITIAL_HEIGHT);
				
				
			}
			else if(a < 10) {
				
			}
			else if(a >= 10 && a < 20) {
				
			}
			else if(a >= 50 && a < 60) {
				
			}
		}
			
		//passa a limpo
		for(int i = 0; i < GConfig.INITIAL_HEIGHT; i++) {
			for(int j = 0; j < GConfig.MAP_WIDTH; j++) {
				
			}
		}
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
