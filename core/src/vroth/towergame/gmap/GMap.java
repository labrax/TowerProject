package vroth.towergame.gmap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.GConfig;
import vroth.towergame.gobject.GObject;
import vroth.towergame.gobject.GObjectFactory;
import vroth.towergame.gobject.GTile;

/**
 * This class will implement the map grid, with an updatable height
 * @author vroth
 *
 */
public class GMap {
	private ArrayList<GMapLine> mapLines = null;
	private Random r = new Random();
	private World world;
	
	public GMap(World world) {
		this.world = world;
		mapLines = new ArrayList<GMapLine>();
		generateMap();
	}
	
	public int size() {
		return mapLines.size();
	}
	
	public Sprite getElement(float stateTime, int x, int y, boolean foreground) {
		boolean top = false, down = false, left = false, right = false;
		if(mapLines.size() <= y)
			return null;

		GObject object;
		if(foreground)
			object = mapLines.get(y).getObjectForeground(x);
		else
			object = mapLines.get(y).getObjectBackground(x);
		
		if(object == null)
			return null;
		
		if(object instanceof GTile) {
			if(mapLines.size() > y) {
				if(foreground) {
					if(mapLines.get(y).getObjectForeground(x-1) != null)
						left = true;
					if(mapLines.get(y).getObjectForeground(x+1) != null)
						right = true;
					if(y-1 >= 0 && mapLines.get(y-1).getObjectForeground(x) != null)
						down = true;
				}
				else {
					if(mapLines.get(y).getObjectBackground(x-1) != null)
						left = true;
					if(mapLines.get(y).getObjectBackground(x+1) != null)
						right = true;
					if(y-1 >= 0 && mapLines.get(y-1).getObjectBackground(x) != null)
						down = true;
				}
			}
			if(mapLines.size() > y+1) {
				if(foreground) {
					if(mapLines.get(y+1).getObjectForeground(x) != null)
						top = true;
				}
				else {
					if(mapLines.get(y+1).getObjectBackground(x) != null)
						top = true;
				}
			}
		
			GTile tile = (GTile) object;
			return tile.getSprite(top, down, left, right);
		}
		return object.getSprite(stateTime);
	}
	
	public void insertElement(GObject object, int x, int y, boolean foreground) {
		while(y+1 > mapLines.size()) {
			mapLines.add(new GMapLine());
		}
		if(foreground)
			mapLines.get(y).setObjectForeground(x, object);
		else
			mapLines.get(y).setObjectBackground(x, object);
	}

	public Array<Vector2> getPath(Vector2 origin, Vector2 target) {
		return null;
	}
	
	public Array<Vector2> getPathGeneration(Vector2 origin, Vector2 target) {
		Array<Vector2> path = null;
		
		int[][] distance = new int[GConfig.INITIAL_HEIGHT][];
		Vector2[][] previous = new Vector2[GConfig.INITIAL_HEIGHT][];
		for(int i = 0; i < GConfig.INITIAL_HEIGHT; i++) {
			distance[i] = new int[GConfig.MAP_WIDTH];
			previous[i] = new Vector2[GConfig.MAP_WIDTH];
			
			for(int j = 0; j < GConfig.MAP_WIDTH; j++) {
				distance[i][j] = 99999999;
				previous[i][j] = new Vector2();
			}
		}
		MapComparator.refVector = target;
		Comparator<Vector2> comparator = new MapComparator();
		PriorityQueue<Vector2> pq = new PriorityQueue<Vector2>(comparator);
		distance[(int) origin.y][(int) origin.x] = 0;
		pq.add(origin);
		
		while(!pq.isEmpty()) {
			Vector2 curr = pq.remove();
			if(curr.x == target.x && curr.y == target.y) {
				path = new Array<Vector2>();
				while(curr.x != origin.x && curr.y != origin.y) {
					path.add(curr);
					curr = previous[(int) curr.y][(int) curr.x];
				}
				path.add(curr);
				//found path, exit loop
				return path;
			}
			
			for(int i = (int) curr.y-1; i <= (int) curr.y+1; i++) {
				for(int j = (int) curr.x-1; j <= (int) curr.x+1; j++) {
					//test if it is valid
					if(i >= 0 && j >= 0 && i < GConfig.INITIAL_HEIGHT && j < GConfig.MAP_WIDTH) {
						Vector2 test = new Vector2(j, i);
						
						//test if the distance is better
						if(distance[i][j] > distance[(int) curr.y][(int) curr.x] + 1) {
							previous[i][j].x = curr.x;
							previous[i][j].y = curr.y;
							distance[i][j] = distance[(int) curr.y][(int) curr.x] + 1;
							
							pq.add(test);
						}
					}
				}
			}
		}
		return path;
	}
	
	private Vector2 randomDirection() {
		Vector2 direction = new Vector2(r.nextInt(3)-1, r.nextInt(3)-1);
		return direction;
	}
	
	private Array<Vector2> randomPoints(Vector2 origin, int amount) {
		Array<Vector2> path = new Array<Vector2>();
		path.add(origin);
		for(int i = 0; i < amount; i++) {
			int e = r.nextInt(path.size);
			Vector2 newOne = new Vector2(path.get(e).x, path.get(e).y);
			Vector2 direction = randomDirection();
			newOne.x += direction.x;
			newOne.y += direction.y;
			if(newOne.x >= 0 && newOne.y >= 0 && newOne.x < GConfig.MAP_WIDTH && newOne.y < GConfig.GENERATION_HEIGHT) {
				if(!path.contains(newOne, false)) {
					path.add(newOne);
				}
			}
			else
				i--;
		}
		return path;
	}
	
	public void generateMap() {
		int iron = 0x10, gold = 0x11;
		int nothing = 0x0, dirt = 0x1;
		
		int amountPathIron = 0;
		
		int[][] initialMap = new int[GConfig.GENERATION_HEIGHT][];
		for(int i = 0; i < GConfig.GENERATION_HEIGHT; i++) {
			initialMap[i] = new int[GConfig.MAP_WIDTH];
			for(int j = 0; j < GConfig.MAP_WIDTH; j++) {
				initialMap[i][j] = dirt;
			}
		}
		
		//initialMap[1][1] = dirt;
		
		for(int lottery = 1; lottery <= GConfig.GENERATION_TRIES; lottery++) {
			int a = r.nextInt(100);
			if(a < 20) {
				if(amountPathIron > GConfig.MAX_AMOUNT_IRON)
					continue;
				//generate a path between 2 points with a lot of resources
				int x1 = r.nextInt(GConfig.MAP_WIDTH);
				int y1 = r.nextInt(GConfig.GENERATION_HEIGHT);
				int x2 = r.nextInt(GConfig.MAP_WIDTH);
				int y2 = r.nextInt(GConfig.GENERATION_HEIGHT);
				
				Array<Vector2> path = getPathGeneration(new Vector2(x1, y1), new Vector2(x2, y2));
				for(Vector2 v : path)
					initialMap[(int) v.y][(int) v.x] = iron;
				
				System.out.println("path of iron");
				amountPathIron++;
			}
			else if(a < 30) {
				System.out.println("empty spaces");
				int x1 = r.nextInt(GConfig.MAP_WIDTH);
				int y1 = r.nextInt(GConfig.GENERATION_HEIGHT);
				Array<Vector2> path = randomPoints(new Vector2(x1, y1), 5);
				for(Vector2 v : path)
					initialMap[(int) v.y][(int) v.x] = nothing;
			}
			else if(a < 70) {
				System.out.println("gold");
				int x1 = r.nextInt(GConfig.MAP_WIDTH);
				int y1 = r.nextInt(GConfig.GENERATION_HEIGHT);
				initialMap[y1][x1] = gold;
			}
			else {
				System.out.println("wasted luck");
			}
		}
			
		//passa a limpo
		for(int i = 0; i < GConfig.GENERATION_HEIGHT; i++) {
			for(int j = 0; j < GConfig.MAP_WIDTH; j++) {
				GObject object = null, object2 = null;
				if(initialMap[i][j] == dirt) {
					if(r.nextInt(100) < 80)
						object = GObjectFactory.getInstance().newTile(world, "tiles/grass", j*70, i*70);
					object2 = GObjectFactory.getInstance().newTile(world, "tiles/grass", j*70, i*70, false);
				}
				else if(initialMap[i][j] == gold) {
					if(r.nextInt(100) < 80)
						object = GObjectFactory.getInstance().newTile(world, "tiles/grass", j*70, i*70);
					object2 = GObjectFactory.getInstance().newStaticObject(world, "tiles/boxCoin.png", j*70, i*70, false);
				}
				else if(initialMap[i][j] == iron) {
					object = GObjectFactory.getInstance().newStaticObject(world, "tiles/castleCenter.png", j*70, i*70);
					object2 = GObjectFactory.getInstance().newStaticObject(world, "tiles/castleCenter.png", j*70, i*70, false);
				}
				else {
					object2 = GObjectFactory.getInstance().newTile(world, "tiles/grass", j*70, i*70, false);
				}
				
				if(object != null)
					insertElement(object, j, i, true);
				if(object2 != null)
					insertElement(object2, j, i, false);
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
