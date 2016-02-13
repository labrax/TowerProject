package vroth.towergame.gmap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.GConfig;
import vroth.towergame.gobject.GObject;
import vroth.towergame.gobject.GObjectFactory;
import vroth.towergame.gobject.GTile;
import vroth.towergame.gutil.GResourcesLoader;

/**
 * This class will implement the map grid, with an updatable height
 * @author vroth
 *
 */
public class GMap {
	public static final int iron = GConfig.iron, gold = GConfig.gold, nothing = GConfig.nothing, dirt = GConfig.dirt;
	private ArrayList<GMapLine> mapLines = null;
	private Random r = new Random();
	private GObjectFactory factory;
	
	private GTile water;
	private Sprite ironSprite;
	private Sprite coinSprite;
	
	/**
	 * Create the game map handler in a physical world
	 * @param world is the physics world
	 */
	public GMap(World world) {
		factory = GObjectFactory.getInstance(world);
		water = factory.newWater();
		ironSprite = GResourcesLoader.getInstance().loadSprite("resource/stoneCoal.png");
		coinSprite = GResourcesLoader.getInstance().loadSprite("resource/coins.png");
		mapLines = new ArrayList<GMapLine>();
	}
	
	/**
	 * 
	 * @return the height of the map
	 */
	public int getHeight() {
		return mapLines.size();
	}
	
	/**
	 * Insert a line on the top of the current map information
	 * @param mapLine
	 */
	public void insertGMapLine(GMapLine mapLine) {
		mapLines.add(mapLine);
	}
	
	/**
	 * Return a line on the y coordinate
	 * @param y is the height
	 * @return the line if it there is information, null otherwise
	 */
	public GMapLine getMapLine(int y) {
		if(y < mapLines.size())
			return mapLines.get(y);
		return null;
	}
	
	/**
	 * Checks if an object is visible on screen
	 * @param reference is the player reference position
	 * @param position is the object position
	 * @param size is the size of the object
	 * @return true or false
	 */
	public boolean isVisible(Vector2 reference, Vector2 position, Vector2 size) {
		boolean x = false, y = false;
		if(position.y - reference.y < GConfig.SCREEN_HEIGHT/2)
			y = true;
		if(position.x - reference.x < GConfig.SCREEN_WIDTH/2)
			x = true;
		if(reference.y - position.y+size.y > GConfig.SCREEN_HEIGHT/2)
			y = true;
		if(reference.x - position.x+size.x > GConfig.SCREEN_WIDTH/2)
			x = true;
		
		if(x == true && y == true)
			return true;
		else
			return false;
	}
	
	/**
	 * Return the range of visible points
	 * @param reference
	 * @return
	 */
	private Vector2[] rangeVisible(Vector2 refPosition) {
		Vector2[] visible = new Vector2[2];
		visible[0] = new Vector2((int) Math.floor(refPosition.x/GConfig.TILE_SPACING - GConfig.SCREEN_HEIGHT/GConfig.TILE_SPACING - 1), (int) Math.ceil(refPosition.y/GConfig.TILE_SPACING - GConfig.SCREEN_HEIGHT/GConfig.TILE_SPACING - 1));
		visible[1] = new Vector2((int) Math.floor(refPosition.x/GConfig.TILE_SPACING + GConfig.SCREEN_HEIGHT/GConfig.TILE_SPACING + 1), (int) Math.ceil(refPosition.y/GConfig.TILE_SPACING + GConfig.SCREEN_HEIGHT/GConfig.TILE_SPACING + 1)); 
		//System.out.println(visible[0] + " " + visible[1]);
		return visible;
	}
	
	private boolean[] getObjectMask(int x, int y, int type) {
		boolean top = false, down = false, left = false, right = false;
		boolean[] willReturn = new boolean[4];
		for(int i = 0; i < 4; i++)
			willReturn[i] = false;
			
		if(y >= mapLines.size())
			return null;

		GObject object;
		object = mapLines.get(y).getStaticObject(x);
		
		if(object == null)
			return null;
		
		if(object instanceof GTile) {
			if(mapLines.size() > y) {
				if(mapLines.get(y).getStaticObject(x-1) != null && (mapLines.get(y).getStaticObject(x-1).getType() == type))
					left = true;
				if(mapLines.get(y).getStaticObject(x+1) != null && (mapLines.get(y).getStaticObject(x+1).getType() == type))
					right = true;
				if(y-1 >= 0 && mapLines.get(y-1).getStaticObject(x) != null && (mapLines.get(y-1).getStaticObject(x).getType() == type))
					down = true;
			}
			if(mapLines.size() > y+1) {
				if(mapLines.get(y+1).getStaticObject(x) != null && (mapLines.get(y+1).getStaticObject(x).getType() == type))
					top = true;
			}
		
			willReturn[0] = top;
			willReturn[1] = down;
			willReturn[2] = left;
			willReturn[3] = right;
			return willReturn;
		}
		return willReturn;
	}
	
	/**
	 * Get a sprite from the map elements, checking the neighbours for the correct sprite
	 * @param stateTime is the time since the game beginning
	 * @param x is the x coordinate
	 * @param y is the y coordinate
	 * @param foreground if the element is on the foreground or background.
	 * @return the sprite
	 */
	private boolean[] getTileMask(int x, int y, boolean foreground, int type) {
		boolean top = false, down = false, left = false, right = false;
		boolean[] willReturn = new boolean[4];
		for(int i = 0; i < 4; i++)
			willReturn[i] = false;
			
		if(y >= mapLines.size())
			return null;

		GObject object;
		if(foreground)
			object = mapLines.get(y).getTileForeground(x);
		else
			object = mapLines.get(y).getTileBackground(x);
		
		if(object == null)
			return null;
		
		if(object instanceof GTile) {
			if(mapLines.size() > y) {
				if(foreground) {
					if(mapLines.get(y).getTileForeground(x-1) != null && (mapLines.get(y).getTileForeground(x-1).getType()&type) > 0)
						left = true;
					if(mapLines.get(y).getTileForeground(x+1) != null && (mapLines.get(y).getTileForeground(x+1).getType()&type) > 0)
						right = true;
					if(y-1 >= 0 && mapLines.get(y-1).getTileForeground(x) != null && (mapLines.get(y-1).getTileForeground(x).getType()&type) > 0)
						down = true;
				}
				else {
					if(mapLines.get(y).getTileBackground(x-1) != null && (mapLines.get(y).getTileBackground(x-1).getType()&type) > 0)
						left = true;
					if(mapLines.get(y).getTileBackground(x+1) != null && (mapLines.get(y).getTileBackground(x+1).getType()&type) > 0)
						right = true;
					if(y-1 >= 0 && mapLines.get(y-1).getTileBackground(x) != null && (mapLines.get(y-1).getTileBackground(x).getType()&type) > 0)
						down = true;
				}
			}
			if(mapLines.size() > y+1) {
				if(foreground) {
					if(mapLines.get(y+1).getTileForeground(x) != null && (mapLines.get(y+1).getTileForeground(x).getType()&type) > 0)
						top = true;
				}
				else {
					if(mapLines.get(y+1).getTileBackground(x) != null && (mapLines.get(y+1).getTileBackground(x).getType()&type) > 0)
						top = true;
				}
			}
		
			willReturn[0] = top;
			willReturn[1] = down;
			willReturn[2] = left;
			willReturn[3] = right;
			return willReturn;
		}
		return willReturn;
	}
	
	/**
	 * Render tiles at a position
	 * @param batch
	 * @param stateTime is the stateTime since the beginning of the game
	 * @param drawReference is the drawing reference
	 * @param x is the coordinate
	 * @param y is the coordinate
	 */
	private void renderPosition(SpriteBatch batch, float stateTime, Vector2 drawReference, int x, int y) {
		GTile b = getMapLine(y).getTileBackground(x);
		GTile f = getMapLine(y).getTileForeground(x);
		GObject o = getMapLine(y).getStaticObject(x);
		
		boolean[] mask = getTileMask(x, y, false, ~GConfig.nothing);
		if(b != null) {
			Sprite toDraw = b.getSprite(mask[0], mask[1], mask[2], mask[3]);
			batch.draw(toDraw, b.getBody().getPosition().x + drawReference.x, b.getBody().getPosition().y + drawReference.y);
			switch(b.getType()) {
				case GConfig.gold:
					batch.draw(coinSprite, b.getBody().getPosition().x + drawReference.x, b.getBody().getPosition().y + drawReference.y);
					break;
				case GConfig.iron:
					batch.draw(ironSprite, b.getBody().getPosition().x + drawReference.x, b.getBody().getPosition().y + drawReference.y);
					break;
				default:
					break;
			}
			Color c = batch.getColor();
			batch.setColor(new Color(0.6f, 0.2f, 0f, 0.7f));
			batch.draw(toDraw, b.getBody().getPosition().x + drawReference.x, b.getBody().getPosition().y + drawReference.y);
			batch.setColor(c);
		}

		mask = getTileMask(x, y, true, ~GConfig.nothing);
		if(f != null) {
			Sprite toDraw = f.getSprite(mask[0], mask[1], mask[2], mask[3]);
			batch.draw(toDraw, f.getBody().getPosition().x + drawReference.x, f.getBody().getPosition().y + drawReference.y);
			switch(f.getType()) {
				case GConfig.gold:
					batch.draw(coinSprite, f.getBody().getPosition().x + drawReference.x, f.getBody().getPosition().y + drawReference.y);
					break;
				case GConfig.iron:
					batch.draw(ironSprite, f.getBody().getPosition().x + drawReference.x, f.getBody().getPosition().y + drawReference.y);
					break;
				default:
					break;
			}
		}
		
		if(o != null) {
			Sprite toDraw;
			if(o instanceof GTile) {
				GTile t = (GTile) o;
				mask = getObjectMask(x, y, o.getType());
				toDraw = t.getSprite(mask[0], mask[1], mask[2], mask[3]);
			}
			else {
				toDraw = o.getSprite(stateTime);
			}
			batch.draw(toDraw, o.getBody().getPosition().x + drawReference.x, o.getBody().getPosition().y + drawReference.y);
		}
	}
	
	private void renderTiles(SpriteBatch batch, float stateTime, Vector2 drawReference, Vector2 range[]) {
		for(int i = (int) range[0].y; i < (int) range[1].y; i++) {
			for(int j = (int) range[0].x; j < (int) range[1].x; j++) {
				if(i >= 0 && i < getHeight()) {
					if(j < 0 || j >= GConfig.MAP_WIDTH)
						continue;
					renderPosition(batch, stateTime, drawReference, j, i);
				}
			}
		}
	}
	
	private void renderWater(SpriteBatch batch, float stateTime, Vector2 drawReference, Vector2 range[]) {
		for(int i = (int) range[0].y; i < (int) range[1].y; i++) {
			for(int j = (int) range[0].x; j < (int) range[1].x; j++) {
				if(i <= 0) {
					Vector2 bodyPosition = new Vector2(j*GConfig.TILE_SPACING, i*GConfig.TILE_SPACING);
					if(i == 0)
						batch.draw(water.getSprite(false, true, true, true), bodyPosition.x + drawReference.x + (float) (15*Math.sin(stateTime/0.2)), bodyPosition.y + drawReference.y);
					else
						batch.draw(water.getSprite(true, true, true, true), bodyPosition.x + drawReference.x + (float) (15*Math.sin(stateTime/0.2)), bodyPosition.y + drawReference.y);
				}
			}
		}
	}
	
	/**
	 * Render the map on the screen
	 * @param batch
	 * @param stateTime is the stateTime since the beginning of the game
	 * @param refPosition is the player reference position
	 * @param drawReference is the drawing reference for the screen size
	 */
	public void render(SpriteBatch batch, float stateTime, Vector2 refPosition, Vector2 drawReference) {
		Vector2[] range = rangeVisible(refPosition);
		renderTiles(batch, stateTime, drawReference, range);
		renderWater(batch, stateTime, drawReference, range);
	}
	
	/**
	 * Insert a tile on the map foreground
	 * @param tile is the tile
	 * @param position is the coordinate
	 * @return true if successful, false if there is something on the position
	 */
	public boolean insertTile(GTile tile, Vector2 position) {
		while((int) position.y >= mapLines.size()) {
			mapLines.add(new GMapLine());
		}
		if(mapLines.get((int) position.y).getTileForeground((int) position.x) == null) {
			mapLines.get((int) position.y).setTileForeground((int) position.x, tile);
			return true;
		}
		return false;
	}
	
	/**
	 * Insert a tile on the map
	 * @param tile is the tile
	 * @param position is the coordinate
	 * @param foreground if the tile is on the front or back
	 * @return true if successful, false if there is something on the position
	 */
	public boolean insertTile(GTile tile, Vector2 position, boolean foreground) {
		while((int) position.y >= mapLines.size()) {
			mapLines.add(new GMapLine());
		}
		if(foreground) {
			if(mapLines.get((int) position.y).getTileForeground((int) position.x) == null) {
				mapLines.get((int) position.y).setTileForeground((int) position.x, tile);
				return true;
			}
		}
		else {
			if(mapLines.get((int) position.y).getTileBackground((int) position.x) == null) {
				mapLines.get((int) position.y).setTileBackground((int) position.x, tile);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Insert an GObject on the map
	 * @param object is the object
	 * @param position is the coordinate
	 * @return true if successful, false if there is something on the position
	 */
	public boolean insertObject(GObject object, Vector2 position) {
		while((int) position.y >= mapLines.size()) {
			mapLines.add(new GMapLine());
		}
		if(mapLines.get((int) position.y).getStaticObject((int) position.x) == null) {
			mapLines.get((int) position.y).setStaticObject((int) position.x, object);
			return true;
		}
		return false;
	}
	
	/**
	 * Return an object on a position
	 * @param position is the coordinate
	 * @return the object if exits, null otherwise
	 */
	public GObject getObject(Vector2 position) {
		if((int) position.y < 0 || (int) position.y >= mapLines.size())
			return null;
		
		GObject object = mapLines.get((int) position.y).getStaticObject((int) position.x);
		if(object == null)
			object = mapLines.get((int) position.y).getTileForeground((int) position.x);
		if(object == null)
			object = mapLines.get((int) position.y).getTileBackground((int) position.x);
		return object;
	}
	
	/**
	 * Return the foreground object
	 * @param position is the coordinate
	 * @return the object if exists, null otherwise
	 */
	public GObject getForegroundObject(Vector2 position) {
		if((int) position.y < 0 || (int) position.y >= mapLines.size())
			return null;
		
		GObject object = mapLines.get((int) position.y).getTileForeground((int) position.x);
		return object;
	}
	
	/**
	 * Destroy an object on the position.
	 * If there is no object on the foreground destroy the one on the back.
	 * @param position is the coordinate
	 */
	public void destroyObject(Vector2 position) {
		if(mapLines.size() <= (int) position.y)
			return;
		
		GObject object = mapLines.get((int) position.y).getStaticObject((int) position.x);
		if(object != null) {
			mapLines.get((int) position.y).setStaticObject((int) position.x, null);
			return;
		}
		
		object = mapLines.get((int) position.y).getTileForeground((int) position.x);
		if(object != null) {
			mapLines.get((int) position.y).setTileForeground((int) position.x, null);
			return;
		}
		mapLines.get((int) position.y).setTileBackground((int) position.x, null);
	}

	public Array<Vector2> getPath(Vector2 origin, Vector2 target) {
		return null;
	}
	
	/**
	 * Return a djikstra path between two points (ignore any barrier)
	 * @param origin is the source
	 * @param target is the target
	 * @return an array of Vector2 coordinates
	 */
	public Array<Vector2> getPathGeneration(Vector2 origin, Vector2 target) {
		Array<Vector2> path = null;
		
		int[][] distance = new int[GConfig.GENERATION_HEIGHT][];
		Vector2[][] previous = new Vector2[GConfig.GENERATION_HEIGHT][];
		for(int i = 0; i < GConfig.GENERATION_HEIGHT; i++) {
			distance[i] = new int[GConfig.GENERATION_WIDTH];
			previous[i] = new Vector2[GConfig.GENERATION_WIDTH];
			
			for(int j = 0; j < GConfig.GENERATION_WIDTH; j++) {
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
					if(i >= 0 && j >= 0 && i < GConfig.GENERATION_HEIGHT && j < GConfig.GENERATION_WIDTH) {
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
	
	/**
	 * Return a random direction
	 * @return a direction
	 */
	private Vector2 randomDirection() {
		Vector2 direction = new Vector2(r.nextInt(3)-1, r.nextInt(3)-1);
		return direction;
	}
	
	/**
	 * From a source point return amount tries of random points connected to the origin
	 * @param origin is the source
	 * @param amount is the amount of random tries
	 * @return an array of coordinates
	 */
	private Array<Vector2> randomPoints(Vector2 origin, int amount) {
		Array<Vector2> path = new Array<Vector2>();
		path.add(origin);
		for(int i = 0; i < amount; i++) {
			int e = r.nextInt(path.size);
			Vector2 newOne = new Vector2(path.get(e).x, path.get(e).y);
			Vector2 direction = randomDirection();
			newOne.x += direction.x;
			newOne.y += direction.y;
			if(newOne.x >= 0 && newOne.y >= 0 && newOne.x < GConfig.GENERATION_WIDTH && newOne.y < GConfig.GENERATION_HEIGHT) {
				if(!path.contains(newOne, false)) {
					path.add(newOne);
				}
			}
			else
				i--;
		}
		return path;
	}
	
	/**
	 * Version 2 of the map generator.
	 * This version creates an equation y = ax²+c in order to create a mountain for the game.
	 * - It generates paths of iron
	 * - It gets some random points and remove
	 */
	public void generateMapV2() {
		int amountPathIron = 0;
		
		//minimum A for a proper parabol
		double eqA = ((double) GConfig.GENERATION_HEIGHT)/((GConfig.GENERATION_WIDTH/2)*(GConfig.GENERATION_WIDTH/2));
		
		//lets add some error or maximum 10% of A
		double eqAerr = r.nextBoolean() ? r.nextFloat()/10*eqA : r.nextFloat()/-10*eqA;
		eqA += eqAerr;
		
		//get mininum C for an equation with 2 on the minimum height
		double eqC = 1 - eqA*((GConfig.GENERATION_WIDTH/2)*(GConfig.GENERATION_WIDTH/2));
		
		GConfig.GENERATION_HEIGHT = (int) Math.ceil(-eqC) + 1;
		
		//the best option in a concave map, otherwise there will be a 1 line bugged line
		eqA = -eqA;
		
		System.out.println("Using equation " + eqA + "x^2 + " + (eqA > 0 ? 0 : -eqC) + ". GENERATION_HEIGHT is " + GConfig.GENERATION_HEIGHT);
		
		int[][] initialMap = new int[GConfig.GENERATION_HEIGHT][];
		for(int i = 0; i < GConfig.GENERATION_HEIGHT; i++) {
			initialMap[i] = new int[GConfig.GENERATION_WIDTH];
			for(int j = 0; j < GConfig.GENERATION_WIDTH; j++) {
				initialMap[i][j] = nothing;
			}
		}
		
		for(int i = 0; i < GConfig.GENERATION_WIDTH/2; i++) {
			int height = (int) Math.ceil(eqA*i*i + (eqA > 0 ? 0 : -eqC));
			//System.out.println(height);
			for(int j = 0; j <= height; j++) {
				initialMap[j][GConfig.GENERATION_WIDTH/2+i] = dirt;
				initialMap[j][GConfig.GENERATION_WIDTH/2-i] = dirt;
			}
		}
		
		//System.err.println("Generation dimensions are: " + GConfig.MAP_WIDTH + ", " + GConfig.GENERATION_HEIGHT);
		for(int lottery = 1; lottery <= GConfig.GENERATION_TRIES; lottery++) {
			int a = r.nextInt(100);
			if(a < 30) {
				if(amountPathIron > GConfig.MAX_AMOUNT_IRON)
					continue;
				//generate a path between 2 points with a lot of resources
				int x1 = r.nextInt(GConfig.GENERATION_WIDTH);
				int y1 = r.nextInt(GConfig.GENERATION_HEIGHT);
				int x2 = r.nextInt(GConfig.GENERATION_WIDTH);
				int y2 = r.nextInt(GConfig.GENERATION_HEIGHT);
				
				if(initialMap[y1][x1] == nothing || initialMap[y2][x2] == nothing) {
					lottery--;
					continue;
				}
					
				Array<Vector2> path = null;
				try {
					path = getPathGeneration(new Vector2(x1, y1), new Vector2(x2, y2));
					
					for(int i = 0; i < (path.size > 5 ? 5 : path.size); i++) {
						Vector2 v = path.get(i);
						Array<Vector2> path2 = randomPoints(new Vector2(v.x, v.y), 3);
						for(Vector2 v2 : path2)
							initialMap[(int) v2.y][(int) v2.x] = iron;
					}
				}
				catch(Exception e) {
					System.err.println("Path went wrong with: " + x1 + ", " + y1 + " -> " + x2 + ", " + y2);
					e.printStackTrace();
					lottery--;
					continue;
				}
				
				//System.out.println("path of iron");
				amountPathIron++;
			}
			else if(a < 40) {
				//System.out.println("empty spaces");
				int x1 = r.nextInt(GConfig.GENERATION_WIDTH);
				int y1 = r.nextInt(GConfig.GENERATION_HEIGHT);
				Array<Vector2> path = randomPoints(new Vector2(x1, y1), 5);
				for(Vector2 v : path)
					initialMap[(int) v.y][(int) v.x] = nothing;
			}
			else if(a < 70) {
				//System.out.println("gold");
				int x1 = r.nextInt(GConfig.GENERATION_WIDTH);
				int y1 = r.nextInt(GConfig.GENERATION_HEIGHT);
				if(initialMap[y1][x1] == nothing) {
					lottery--;
					continue;
				}
				initialMap[y1][x1] = gold;
			}
			else {
				//System.out.println("wasted luck");
			}
		}
		
		int amountIron = 0, amountDirt = 0, amountGold = 0;
		//passa a limpo
		for(int i = 0; i < GConfig.GENERATION_HEIGHT; i++) {
			for(int j = 0; j < GConfig.GENERATION_WIDTH; j++) {
				Vector2 position = new Vector2((GConfig.MAP_WIDTH/2 - GConfig.GENERATION_WIDTH/2)*GConfig.TILE_SPACING + j*GConfig.TILE_SPACING, i*GConfig.TILE_SPACING);
				GTile object = null, object2 = null;
				if(initialMap[i][j] == dirt) {
					if(r.nextInt(100) < 80) {
						amountDirt++;
						object = factory.newDirt(position, false);
					}
					if(r.nextInt(100) < 5) {
						amountGold++;
						object2 = factory.newCoinTile(position, true);
					}
					else {
						amountDirt++;
						object2 = factory.newDirt(position, true);
					}
				}
				else if(initialMap[i][j] == gold) {
					if(r.nextInt(100) < 80) {
						amountDirt++;
						object = factory.newDirt(position, false);
					}
					amountGold++;
					object2 = factory.newCoinTile(position, true);
				}
				else if(initialMap[i][j] == iron) {
					if(r.nextInt(100) < 80) {
						amountIron++;
						object = factory.newIron(position, false);
					}
					else if(r.nextInt(100) < 90) {
						amountDirt++;
						object = factory.newDirt(position, false);
					}
					amountIron++;
					object2 = factory.newIron(position, true);
				}
				
				if(object != null)
					insertTile(object, new Vector2((GConfig.MAP_WIDTH/2 - GConfig.GENERATION_WIDTH/2) + j, i), true);
				if(object2 != null)
					insertTile(object2, new Vector2((GConfig.MAP_WIDTH/2 - GConfig.GENERATION_WIDTH/2) + j, i), false);
			}
		}
		System.out.println("Iron (" + amountIron + "), Gold (" + amountGold + "), Dirt (" + amountDirt + ")");
	}
	
	/**
	 * Version 1 of the map generator
	 * This version creates a base map that is a rectangle
	 * - It also generates big regular paths of iron
	 * - It also creates some random points holes
	 */
	public void generateMapV1() {
		int amountPathIron = 0;
		
		int[][] initialMap = new int[GConfig.GENERATION_HEIGHT][];
		for(int i = 0; i < GConfig.GENERATION_HEIGHT; i++) {
			initialMap[i] = new int[GConfig.GENERATION_WIDTH];
			for(int j = 0; j < GConfig.GENERATION_WIDTH; j++) {
				initialMap[i][j] = dirt;
			}
		}
		
		for(int lottery = 1; lottery <= GConfig.GENERATION_TRIES; lottery++) {
			int a = r.nextInt(100);
			if(a < 20) {
				if(amountPathIron > GConfig.MAX_AMOUNT_IRON)
					continue;
				//generate a path between 2 points with a lot of resources
				int x1 = r.nextInt(GConfig.GENERATION_WIDTH);
				int y1 = r.nextInt(GConfig.GENERATION_HEIGHT);
				int x2 = r.nextInt(GConfig.GENERATION_WIDTH);
				int y2 = r.nextInt(GConfig.GENERATION_HEIGHT);
				
				Array<Vector2> path = getPathGeneration(new Vector2(x1, y1), new Vector2(x2, y2));
				for(Vector2 v : path)
					initialMap[(int) v.y][(int) v.x] = iron;
				
				System.out.println("path of iron");
				amountPathIron++;
			}
			else if(a < 30) {
				System.out.println("empty spaces");
				int x1 = r.nextInt(GConfig.GENERATION_WIDTH);
				int y1 = r.nextInt(GConfig.GENERATION_HEIGHT);
				Array<Vector2> path = randomPoints(new Vector2(x1, y1), 5);
				for(Vector2 v : path)
					initialMap[(int) v.y][(int) v.x] = nothing;
			}
			else if(a < 70) {
				System.out.println("gold");
				int x1 = r.nextInt(GConfig.GENERATION_WIDTH);
				int y1 = r.nextInt(GConfig.GENERATION_HEIGHT);
				initialMap[y1][x1] = gold;
			}
			else {
				System.out.println("wasted luck");
			}
		}
			
		//clean up
		for(int i = 0; i < GConfig.GENERATION_HEIGHT; i++) {
			for(int j = 0; j < GConfig.GENERATION_WIDTH; j++) {
				Vector2 position = new Vector2(j*GConfig.TILE_SPACING, i*GConfig.TILE_SPACING);
				GTile object = null, object2 = null;
				if(initialMap[i][j] == dirt) {
					if(r.nextInt(100) < 80)
						object = factory.newDirt(position, false);
					object2 = factory.newDirt(position, true);
				}
				else if(initialMap[i][j] == gold) {
					if(r.nextInt(100) < 80)
						object = factory.newDirt(position, false);
					object2 = factory.newCoinTile(position, true);
				}
				else if(initialMap[i][j] == iron) {
					object = factory.newIron(position, false);
					object2 = factory.newIron(position, true);
				}
				else {
					object2 = factory.newDirt(position, true);
				}
				
				if(object != null)
					insertTile(object, new Vector2(j, i), true);
				if(object2 != null)
					insertTile(object2, new Vector2(j, i), false);
			}
		}
	}
}
