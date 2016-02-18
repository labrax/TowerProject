package vroth.towergame;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.GConfig.TYPES;
import vroth.towergame.gmap.GMap;
import vroth.towergame.gobject.GCreature;
import vroth.towergame.gobject.GCreature.STATE;
import vroth.towergame.gobject.GObject;
import vroth.towergame.gobject.GObjectFactory;
import vroth.towergame.gobject.GObjectResource;
import vroth.towergame.gobject.GPlayer;
import vroth.towergame.gobject.GTile;
import vroth.towergame.gutil.GDatabase;
import vroth.towergame.gutil.GResourcesLoader;

public class GPlayScreen implements IScreen {
	private World world;
	private GPlayer player;

	float refX, refY;
	
	private Vector2 mousePosition, pointerPosition;
	private int clickButton;
	private int cursorSelection = 0;
	private boolean isClicking = false;
	
	private Vector2 refPosition;
	
	private float stateTime;

	private Array<GObject> gameObjects = null;
	private Array<GCreature> gameCreatures = null;
	private Array<GCreature> creaturesToRemove = null;
	private GMap gameMap = null;
	
	private Random random;
	
	private boolean insideRange = false;
	
	private boolean isControlLeft = false;
	
	private TowerGame caller;
	
	float lastRespawn = 5;
	
	GRespawn respawner;
	
	private BitmapFont highScoreFont;
	private GlyphLayout highScoreLayout;
	
	private boolean playerRespawn = false;
	
	private Sprite background;
	
	protected GPlayScreen(TowerGame caller){
		this.caller = caller;
		
		background = GResourcesLoader.getInstance().loadSprite("background.png");
		
		highScoreFont = GResourcesLoader.getInstance().getFont("kenpixel_blocks.fnt");
		highScoreFont.getData().setScale(GConfig.PLAYER_ITEM_FONT_SIZE);
		highScoreFont.setColor(new Color(1, 0, 0, 1));
	}
	
	public void create(World world) {
		pointerPosition = new Vector2(0, 0);
		mousePosition = new Vector2(0, 0);
		
		this.world = world;
		this.random = new Random();
		
		gameObjects = new Array<GObject>();
		gameCreatures = new Array<GCreature>();
		creaturesToRemove = new Array<GCreature>();
		gameMap = new GMap(world);
		respawner = new GRespawn(gameCreatures);
		//gameMap.generateMapV2();
		
		GObjectFactory.getInstance(world);
		player = gameMap.loadFile(GConfig.MAP_FILE);
		
		loadCursor();
	}
	
	private boolean checkRange(Vector2 mousePosition, Vector2 screenCenter) {
		if(mousePosition.dst(screenCenter) < GConfig.PLAYER_RANGE)
			return true;
		return false;
	}
	
	private boolean isInside(Vector2 mouseFixedPosition, Vector2 creatureBase, Vector2 creatureDimension) {
		//System.out.println(mouseFixedPosition + " " + creatureBase + " " + creatureDimension);
		if(mouseFixedPosition.x >= creatureBase.x && mouseFixedPosition.x < creatureBase.x + creatureDimension.x)
			if(mouseFixedPosition.y >= creatureBase.y && mouseFixedPosition.y < creatureBase.y + creatureDimension.y)
				return true;
		return false;
	}
	
	private void leftClick(float stateTime, float deltaTime) {
		GObject object = gameMap.getObject(pointerPosition);
		if(insideRange && object != null && object.isIndestructible() == false) {
			GConfig.TYPES objectType = object.getType();
			float hp = object.hit(player.getDamage() * deltaTime);
			if(hp < 0) {
				Array<GObject> newObjects = GObjectFactory.getInstance(world).newResource(objectType, new Vector2(object.getBody().getPosition().x + object.getDimension().x/2, object.getBody().getPosition().y + object.getDimension().y/2));
				for(GObject o : newObjects) {
					if(gameObjects.size < GConfig.MAX_RESOURCES_ON_GAME)
						gameObjects.add(o);
					else {
						world.destroyBody(o.getBody());
					}
				}
				
				//TODO: fix bug
				/*Filter filter = object.fixture.getFilterData(); 
				filter.categoryBits = GConfig.CATEGORY_BTILE;
				filter.maskBits = GConfig.MASK_NO_TOUCH;
				object.fixture.setFilterData(filter);
				world.step(0, 0, 0);*/
				
				//player.getBody().applyForceToCenter(new Vector2(GConfig.SPEED_WALK*(object.getCenter().x-player.getCenter().x), GConfig.FORCE_UP*(object.getCenter().y-player.getCenter().y)), true);
				world.destroyBody(object.getBody());
				gameMap.destroyObject(pointerPosition);
			}
		}
		else if(insideRange) {
			for(GCreature c : gameCreatures) {
				if(isInside(new Vector2(pointerPosition.x*70, pointerPosition.y*70), c.getBody().getPosition(), c.getDimension()))
					c.hit(player.getDamage()*deltaTime);
			}
		}
	}
	
	private boolean validBuild(Vector2 position, TYPES type) {
		if(type != TYPES.LADDER && type != TYPES.CASTLE && type != TYPES.HOUSE) {
			if(isInside(new Vector2((int) position.x*70, (int) position.y*70), player.getBody().getPosition(), player.getDimension()) 
					|| isInside(new Vector2((int) position.x*70+70, (int) position.y*70), player.getBody().getPosition(), player.getDimension())
					|| isInside(new Vector2((int) position.x*70, (int) position.y*70+70), player.getBody().getPosition(), player.getDimension())
					|| isInside(new Vector2((int) position.x*70+70, (int) position.y*70+70), player.getBody().getPosition(), player.getDimension())) {
				return false;
			}
		}
		else if(type == TYPES.LADDER) {
			boolean[] mask = gameMap.getObjectMask((int) position.x, (int) position.y, type);
			if(mask != null && (mask[0] == true || mask[1] == true || mask[2] == true || mask[3] == true))
				return true;
		}
		if(position.x < 0 || position.x >= GConfig.MAP_WIDTH)
			return false;
		
		if(gameMap.getHeight() < position.y) {
			gameMap.insertTile(null, position);
		}
		
		GTile b = gameMap.getMapLine((int) position.y).getTileBackground((int) position.x);
		if(b != null && b.getType() != TYPES.NOTHING)
			return true;
		boolean[] mask = gameMap.getTileMask((int) position.x, (int) position.y, true);
		//System.out.println("" + mask[0] + " " + mask[1] + " " + mask[2] + " " + mask[3]);
		if(mask != null && (mask[0] == true || mask[1] == true || mask[2] == true || mask[3] == true))
			return true;
		return false;
	}
	
	private void rightClick(float stateTime) {
		GConfig.TYPES type = GDatabase.getInstance().getItemsFromCursorIndex(cursorSelection);
		if(player.getItems().containsKey(type) && player.getItems().get(type) > 0)
			if(insideRange && validBuild(pointerPosition, type)) {
				boolean built = false;
				GObject object = null;
				switch(type) {
					case LADDER:
						object = GObjectFactory.getInstance(world).newTile(type, new Vector2(((int) pointerPosition.x)*70, ((int) pointerPosition.y)*70), false);
						if(gameMap.insertTile(null, pointerPosition) && gameMap.insertObject(null, pointerPosition))
							built = gameMap.insertObject(object, pointerPosition);
						else if(gameMap.getForegroundObject(pointerPosition) != null && (gameMap.getForegroundObject(pointerPosition).getType() == GConfig.TYPES.CASTLE || gameMap.getForegroundObject(pointerPosition).getType() == GConfig.TYPES.HOUSE))
							built = gameMap.insertObject(object, pointerPosition);
						break;
					case GRASS:
						object = GObjectFactory.getInstance(world).newTile(type, new Vector2(((int) pointerPosition.x)*70, ((int) pointerPosition.y)*70), false);
						if(gameMap.insertTile(null, pointerPosition) && gameMap.insertObject(null, pointerPosition))
							built = gameMap.insertTile((GTile) object, pointerPosition);
						break;
					case CASTLE:
					case HOUSE:
						object = GObjectFactory.getInstance(world).newTile(type, new Vector2(((int) pointerPosition.x)*70, ((int) pointerPosition.y)*70), false);
						if(gameMap.insertTile(null, pointerPosition))
							built = gameMap.insertTile((GTile) object, pointerPosition);
						break;
					default:
						break;
				}
				if(object != null && built == false)
					world.destroyBody(object.getBody());
				if(object != null && object.getType() != GConfig.TYPES.GRASS)
					GObjectFactory.getInstance(world).setBuilding(object);
				if(built) {
					player.getItems().put(type, player.getItems().get(type)-1);
				}
				//System.out.println(built);
			}
	}
	
	private void iterateObjects(float stateTime, float deltaTime) {
		for(GObject o : gameObjects) {
			/*if(o.getCreationTime() + GConfig.ON_GAME_RESOURCE_TIME < stateTime) {
				world.destroyBody(o.getBody());
				gameObjects.removeValue(o, true);
			}
			//object bellow water line
			else */if(o.getBody().getPosition().y < 0) {
				float hp = o.hit(deltaTime*random.nextInt(GConfig.MAX_WATER_DAMAGE-GConfig.MIN_WATER_DAMAGE) + GConfig.MIN_WATER_DAMAGE);
				if(hp < 0) {
					world.destroyBody(o.getBody());
				}
				gameObjects.removeValue(o, true);
			}
			//resource close to player
			else if(o instanceof GObjectResource) {
				float distance = o.getCenter().dst2(player.getCenter());
				//System.out.println(distance);
				if(distance < GConfig.DISTANCE_RESOURCE_COLLECT) {
					player.addItem(o.getType());
					world.destroyBody(o.getBody());
					gameObjects.removeValue(o, true);
				}
				else if(distance < GConfig.DISTANCE_RESOURCE_APPROACH) {
					Vector2 currVelocity = o.getBody().getLinearVelocity();
					Vector2 direction = new Vector2(player.getCenter().x - o.getCenter().x, player.getCenter().y - o.getCenter().y);
					direction.x *= 2;
					direction.y *= 2;
					Vector2 directionRandomized = new Vector2(direction.x + (random.nextInt(30)-15)/100*direction.x, direction.y + (random.nextInt(30)-15)/100*direction.y);
					Vector2 approachDistanceFix = new Vector2((GConfig.DISTANCE_RESOURCE_APPROACH - distance)/GConfig.DISTANCE_RESOURCE_APPROACH*directionRandomized.x, (GConfig.DISTANCE_RESOURCE_APPROACH - distance)/GConfig.DISTANCE_RESOURCE_APPROACH*directionRandomized.y);
					o.getBody().setLinearVelocity(new Vector2(Math.abs(currVelocity.x) > Math.abs(approachDistanceFix.x) ? currVelocity.x : approachDistanceFix.x, Math.abs(currVelocity.y) > Math.abs(approachDistanceFix.y) ? currVelocity.y : approachDistanceFix.y));
				}
			}
		}
	}
	
	private void creatureAttack(Vector2 attackPosition, float damage, float stateTime, float deltaTime) {
		//System.out.println(attackPosition);
		GObject object = gameMap.getObject(attackPosition);
		if(object != null && object.isIndestructible() == false) {
			GConfig.TYPES objectType = object.getType();
			float hp = object.hit(damage * deltaTime);
			
			if(hp < 0) {
				Array<GObject> newObjects = GObjectFactory.getInstance(world).newResource(objectType, new Vector2(object.getBody().getPosition().x + object.getDimension().x/2, object.getBody().getPosition().y + object.getDimension().y/2));
				for(GObject o : newObjects) {
					if(gameObjects.size < GConfig.MAX_RESOURCES_ON_GAME) {
						gameObjects.add(o);
					}
					else {
						world.destroyBody(o.getBody());
					}
				}
				world.destroyBody(object.getBody());
				gameMap.destroyObject(attackPosition);
			}
		}
	}
	
	private Vector2 positionToCoordinate(float x, float y) {
		return new Vector2(x/70, y/70);
	}
	
	public void iterateCreatures(float deltaTime) {
		for(GCreature c : gameCreatures) {
			if(c.getBody().getPosition().y < 0)
				c.hit(deltaTime*random.nextInt(GConfig.MAX_WATER_DAMAGE-GConfig.MIN_WATER_DAMAGE) + GConfig.MIN_WATER_DAMAGE);
			if(c.getState() != STATE.DEAD && isInside(player.getCenter(), c.getBody().getPosition(), c.getDimension()))
				player.hit(c.getDamage()*deltaTime/10);
			
			if(c.isGoingDown()) {
				Vector2 attackPosition = positionToCoordinate(c.getBody().getPosition().x, c.getBody().getPosition().y - 5);
				creatureAttack(attackPosition, c.getDamage(), deltaTime, deltaTime);
				
				attackPosition = positionToCoordinate(c.getBody().getPosition().x + c.getDimension().x, c.getBody().getPosition().y - 5);
				creatureAttack(attackPosition, c.getDamage(), deltaTime, deltaTime);
			}
			if(c.isGoingUp()) {
				Vector2 attackPosition = positionToCoordinate(c.getBody().getPosition().x, c.getBody().getPosition().y + c.getDimension().y + 5);
				creatureAttack(attackPosition, c.getDamage(), deltaTime, deltaTime);
				
				attackPosition = positionToCoordinate(c.getBody().getPosition().x + c.getDimension().x, c.getBody().getPosition().y + c.getDimension().y + 5);
				creatureAttack(attackPosition, c.getDamage(), deltaTime, deltaTime);
			}
			if(c.isGoingLeft()) {
				Vector2 attackPosition = positionToCoordinate(c.getBody().getPosition().x - 5, c.getBody().getPosition().y);
				creatureAttack(attackPosition, c.getDamage(), deltaTime, deltaTime);
				
				attackPosition = positionToCoordinate(c.getBody().getPosition().x - 5, c.getBody().getPosition().y + c.getDimension().y);
				creatureAttack(attackPosition, c.getDamage(), deltaTime, deltaTime);
			}
			if(c.isGoingRight()) {
				Vector2 attackPosition = positionToCoordinate(c.getBody().getPosition().x + c.getDimension().x + 5, c.getBody().getPosition().y);
				creatureAttack(attackPosition, c.getDamage(), deltaTime, deltaTime);
				
				attackPosition = positionToCoordinate(c.getBody().getPosition().x + c.getDimension().x + 5, c.getBody().getPosition().y + c.getDimension().y);
				creatureAttack(attackPosition, c.getDamage(), deltaTime, deltaTime);
			}
			c.setGoal(player.getCenter());
			c.update(stateTime, deltaTime);
		}
	}
	
	public void update(float deltaTime) {
		world.step(deltaTime, 6, 2);
		stateTime += deltaTime;
		
		if(player.getBody().getLinearVelocity().x != 0 || player.getBody().getLinearVelocity().y != 0)
			updateMouse();
		if(isClicking && clickButton == 0) {
			leftClick(stateTime, deltaTime);
		}
		else if(isClicking && clickButton == 1) {
			rightClick(stateTime);
		}
		
		GObject atPlayerPos = gameMap.getObject(new Vector2(player.getCenter().x/70, player.getCenter().y/70));
		if(atPlayerPos != null && atPlayerPos.getType() == GConfig.TYPES.LADDER)
			player.setClimb(stateTime, true);
		else
			player.setClimb(stateTime, false);
		player.update(stateTime, deltaTime);
		
		iterateObjects(stateTime, deltaTime);
		
		//check if there is any creature to remove
		for(GCreature c : creaturesToRemove) {
			if(c != player) {
				world.destroyBody(c.getBody());
				gameCreatures.removeValue(c, true);
			}
			else {
				HashMap<GConfig.TYPES, Integer> itemsHash = player.getItems();
				world.destroyBody(player.getBody());
				player = GObjectFactory.getInstance(world).newPlayer(GConfig.PLAYER_FOLDER, gameMap.getRespawn());
				if(GConfig.KEEP_ITEMS)
					for(GConfig.TYPES type : itemsHash.keySet()) {
						player.addItem(type, itemsHash.get(type));
					}
				playerRespawn = true;
			}
		}
		creaturesToRemove.clear();
		
		if(playerRespawn) {
			gameCreatures.clear();
			playerRespawn = false;
		}
		
		iterateCreatures(deltaTime);
		
		if(gameCreatures.size < GConfig.MAX_CREATURES_ON_GAME && lastRespawn + GConfig.CURR_RESPAWN_TIME < stateTime) {
			respawner.respawnSomething(player.getCenter(), gameMap);
			lastRespawn = stateTime;
		}
		GConfig.CURR_RESPAWN_TIME = GConfig.MAX_RESPAWN_TIME - GConfig.FACTOR_RESPAWN*gameMap.getHighScore();
		if(GConfig.CURR_RESPAWN_TIME < GConfig.MIN_RESPAWN_TIME)
			GConfig.CURR_RESPAWN_TIME = GConfig.MIN_RESPAWN_TIME;
		
		if(GConfig.DEBUG_CONTROLS)
			refPosition = new Vector2(refX, refY);
		else {
			refPosition = player.getBody().getPosition();
			refPosition.x += player.getDimension().x/2;
			refPosition.y += player.getDimension().y/2;
		}
	}
	
	public void render(OrthographicCamera camera, Box2DDebugRenderer debugRenderer, SpriteBatch batch) {
		camera.update();
		Gdx.gl.glClearColor(GConfig.BACKGROUND_COLOR.r, GConfig.BACKGROUND_COLOR.g, GConfig.BACKGROUND_COLOR.b, GConfig.BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Vector2 drawReference = new Vector2(GConfig.SCREEN_WIDTH/2-refPosition.x, GConfig.SCREEN_HEIGHT/2-refPosition.y);
		
		camera.position.set(refPosition, 0);
		
		batch.begin();
		//render the background
		batch.draw(background, GConfig.SCREEN_WIDTH/2 - 960, 0 + drawReference.y);
		
		//render the map
		gameMap.render(batch, stateTime, refPosition, drawReference);
		
		//render the objects
		for(GObject o: gameObjects) {
			if(o instanceof GObjectResource)
				continue;
			if(gameMap.isVisible(refPosition, o.getBody().getPosition(), o.getDimension()))
				batch.draw(o.getSprite(stateTime), o.getBody().getPosition().x + drawReference.x, o.getBody().getPosition().y + drawReference.y);
		}

		//render the creatures
		for(GCreature c : gameCreatures) {
			if(gameMap.isVisible(refPosition, c.getBody().getPosition(), c.getDimension()))
				c.render(batch, stateTime, drawReference);
		}
		
		//render the player
		player.render(batch, stateTime, drawReference);
		
		for(GObject o : gameObjects) {
			if(o instanceof GObjectResource) {
				if(gameMap.isVisible(refPosition, o.getBody().getPosition(), o.getDimension()))
					batch.draw(o.getSprite(stateTime), o.getBody().getPosition().x + drawReference.x, o.getBody().getPosition().y + drawReference.y);				
			}
		}

		batch.end();
		
		if(GConfig.DRAW_HIGHSCORE) {
			if(gameMap.isVisible(refPosition, new Vector2(player.getBody().getPosition().x, gameMap.getHighScore()*70-player.getDimension().y/2), new Vector2(1, 1))) {
				ShapeRenderer sr = new ShapeRenderer();
				sr.setProjectionMatrix(camera.combined);
				sr.setColor(Color.RED);
				sr.begin(ShapeType.Filled);
				sr.rect(player.getCenter().x - GConfig.SCREEN_WIDTH/2, gameMap.getHighScore()*70 - 1, player.getCenter().x + GConfig.SCREEN_WIDTH/2, 3f);
				sr.end();

				highScoreLayout = new GlyphLayout(highScoreFont, "Highscore: " + gameMap.getHighScore());
				batch.begin();
				highScoreFont.draw(batch, highScoreLayout, 0, gameMap.getHighScore()*70 + drawReference.y + 15);
				batch.end();
				//System.out.println("draw");
			}
		}

		if(GConfig.DEBUG_PHYSICS)
			debugRenderer.render(world, camera.combined);
		
		if(GConfig.DRAW_RANGE) {
			ShapeRenderer sr = new ShapeRenderer();
			sr.setProjectionMatrix(camera.combined);
			sr.setColor(Color.BLACK);
			sr.begin(ShapeType.Line);
			sr.circle(player.getCenter().x, player.getCenter().y, GConfig.PLAYER_RANGE);
			sr.end();
		}
	}
	
	public void resize(int width, int height) {
		updateMouse();
		loadCursor();
	}
	
	public void pause() {
		
	}
	
	public void resume() {
		
	}
	
	public void dispose() {
		gameMap.saveToFile(GConfig.MAP_FILE, player);
		gameObjects.clear();
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		for(Body b : bodies)
			world.destroyBody(b);
		Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
	}

	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.F12)
			GConfig.DEBUG_PHYSICS = !GConfig.DEBUG_PHYSICS;
		if(keycode == Input.Keys.F11) {
			if(!GConfig.DEBUG_CONTROLS) {
				refX = player.getBody().getPosition().x + player.getDimension().x/2;
				refY = player.getBody().getPosition().y + player.getDimension().y/2;
			}
			GConfig.DEBUG_CONTROLS = !GConfig.DEBUG_CONTROLS;
		}
		if(keycode == Input.Keys.F10) {
			GConfig.DRAW_RANGE = !GConfig.DRAW_RANGE;
		}
		if(keycode == Input.Keys.F9) {
			respawner.respawnSomething(player.getCenter(), gameMap);
		}
		if(keycode == Input.Keys.F8) {
			respawner.respawnNearby(player.getCenter(), gameMap);
		}
		
		if(keycode == Input.Keys.CONTROL_LEFT)
			isControlLeft = true;
		
		if(keycode == Input.Keys.S && isControlLeft) {
			gameMap.saveToFile(GConfig.MAP_FILE, player);
		}
		else if(keycode == Input.Keys.L && isControlLeft) {
			Array<Body> bodies = new Array<Body>();
			world.getBodies(bodies);
			world.clearForces();
			for(Body b : bodies) {
				world.destroyBody(b);
			}
			gameObjects.clear();
			gameCreatures.clear();
			player = gameMap.loadFile(GConfig.MAP_FILE);
		}
		else if(keycode == Input.Keys.N && isControlLeft) {
			Array<Body> bodies = new Array<Body>();
			world.getBodies(bodies);
			world.clearForces();
			for(Body b : bodies) {
				world.destroyBody(b);
			}
			gameObjects.clear();
			gameCreatures.clear();
			player = gameMap.loadFile("map.dat2");
		}
		
		if(keycode == Input.Keys.ESCAPE) {
			caller.endState();
		}
		
		if(GConfig.DEBUG_CONTROLS) {
			if(keycode == Input.Keys.DOWN)
				refY -= 100;
			if(keycode == Input.Keys.UP)
				refY += 100;
			if(keycode == Input.Keys.LEFT)
				refX -= 100;
			if(keycode == Input.Keys.RIGHT)
				refX += 100;
			return true;
		}
		return player.keyDown(keycode);
	}

	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.CONTROL_LEFT)
			isControlLeft = false;
		
		return player.keyUp(keycode);
	}

	public boolean keyTyped(char character) {
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		isClicking = true;
		clickButton = button;
		return true;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		isClicking = false;
		return true;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		isClicking = true;
		mouseMoved(screenX, screenY);
		return true;
	}

	public void updateMouse() {
		Vector2 refPosition = new Vector2(refX, refY);
		if(!GConfig.DEBUG_CONTROLS) {
			refPosition = player.getBody().getPosition();
			refPosition.x += player.getDimension().x/2;
			refPosition.y += player.getDimension().y/2;
		}
		if(mousePosition.x < GConfig.SCREEN_WIDTH/2)
			refPosition.x -= (GConfig.SCREEN_WIDTH/2 - mousePosition.x);
		else
			refPosition.x += (mousePosition.x - GConfig.SCREEN_WIDTH/2);
		
		if(mousePosition.y < GConfig.SCREEN_HEIGHT/2)
			refPosition.y -= (GConfig.SCREEN_HEIGHT/2 - mousePosition.y);
		else
			refPosition.y += (mousePosition.y - GConfig.SCREEN_HEIGHT/2);
	
		refPosition.x = refPosition.x/70;
		refPosition.y = refPosition.y/70;
		
		pointerPosition.set(refPosition);
	}
	
	public boolean mouseMoved(int screenX, int screenY) {
		mousePosition.set(screenX, GConfig.SCREEN_HEIGHT-screenY);
		updateMouse();
		loadCursor();
		return true;
	}
	
	public void loadCursor() {
		//System.out.println(GDatabase.getInstance().getItemsFromCursorIndex(cursorSelection));
		//System.out.println(GDatabase.getInstance().getItemsToCursor(GDatabase.getInstance().getItemsFromCursorIndex(cursorSelection)));
		//System.out.println(mousePosition + " " + new Vector2(GConfig.SCREEN_WIDTH/2, GConfig.SCREEN_HEIGHT/2) + " " + GConfig.PLAYER_RANGE);
		if(player.getState() == STATE.DEAD) {
			insideRange = false;
			Cursor customCursor = Gdx.graphics.newCursor(GResourcesLoader.getInstance().loadPixmap(GDatabase.getInstance().getItemsToCursor(GConfig.TYPES.DEAD)), 4, 0);
			Gdx.graphics.setCursor(customCursor);
			customCursor.dispose();
		}
		else if(checkRange(mousePosition, new Vector2(GConfig.SCREEN_WIDTH/2, GConfig.SCREEN_HEIGHT/2)) == true) {
			insideRange = true;
			Cursor customCursor = Gdx.graphics.newCursor(GResourcesLoader.getInstance().loadPixmap(GDatabase.getInstance().getItemsToCursor(GDatabase.getInstance().getItemsFromCursorIndex(cursorSelection))), 4, 0);
			Gdx.graphics.setCursor(customCursor);
			customCursor.dispose();
		}
		else {
			insideRange = false;
			Cursor customCursor = Gdx.graphics.newCursor(GResourcesLoader.getInstance().loadPixmap(GDatabase.getInstance().getItemsToCursor(GConfig.TYPES.ERR)), 4, 0);
			Gdx.graphics.setCursor(customCursor);
			customCursor.dispose();
		}
	}

	public boolean scrolled(int amount) {
		cursorSelection += amount;
		if(cursorSelection < 0)
			cursorSelection = GConfig.AMOUNT_ITEMS-1;
		cursorSelection = cursorSelection%GConfig.AMOUNT_ITEMS;
		loadCursor();
		return true;
	}

	public void setCreatureForRemoval(GCreature creature) {
		creaturesToRemove.add(creature);
	}

	public float getStateTime() {
		return stateTime;
	}
}
