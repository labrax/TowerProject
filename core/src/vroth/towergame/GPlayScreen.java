package vroth.towergame;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.gmap.GMap;
import vroth.towergame.gobject.GObject;
import vroth.towergame.gobject.GObjectFactory;
import vroth.towergame.gobject.GObjectResource;
import vroth.towergame.gobject.GPlayer;

public class GPlayScreen implements IScreen {
	private World world;
	private GPlayer player;

	float refX, refY;
	
	private Vector2 mousePosition;
	private Vector2 pointerPosition;
	private boolean isClicking = false;
	
	private Vector2 refPosition;
	
	private float stateTime;

	private Array<GObject> gameObjects = null;
	private GMap gameMap = null;
	
	private Random random;
	
	public void create(World world) {
		pointerPosition = new Vector2(0, 0);
		mousePosition = new Vector2(0, 0);
		
		this.world = world;
		this.random = new Random();
		
		gameObjects = new Array<GObject>();
		gameMap = new GMap(world);
		gameMap.generateMapV2();
		
		GObjectFactory factory = GObjectFactory.getInstance(world);
		player = factory.newPlayer("p1/", new Vector2(GConfig.MAP_WIDTH/2*GConfig.TILE_SPACING, (GConfig.GENERATION_HEIGHT+1)*GConfig.TILE_SPACING));
		
		/*GObject tile;
		tile = factory.newBox(new Vector2(GConfig.MAP_WIDTH/2*70+100, (GConfig.GENERATION_HEIGHT+1)*70 + 100));
		gameObjects.add(tile);
		tile = factory.newBox(new Vector2(GConfig.MAP_WIDTH/2*70+100, (GConfig.GENERATION_HEIGHT+1)*70 + 150));
		gameObjects.add(tile);*/
	}
	
	public void update(float deltaTime) {
		world.step(deltaTime, 6, 2);
		stateTime += deltaTime;
		
		if(player.getBody().getLinearVelocity().x != 0 || player.getBody().getLinearVelocity().y != 0)
			updateMouse();
		if(isClicking) {
			GObject object = gameMap.getObject(pointerPosition);
			if(object != null) {
				int objectType = object.getType();
				float hp = object.hit(player.getDamage() * deltaTime);
				//System.out.print(" " + hp);
				if(hp < 0) {
					Array<GObject> newObjects = GObjectFactory.getInstance(world).newResource(objectType, new Vector2(object.getBody().getPosition().x + object.getDimension().x/2, object.getBody().getPosition().y + object.getDimension().y/2));
					for(GObject o : newObjects)
						gameObjects.add(o);
					world.destroyBody(object.getBody());
					gameMap.destroyObject(pointerPosition);
				}
			}
		}
		
		//player bellow water line
		if(player.getBody().getPosition().y < 0) {
			player.hit(random.nextInt(GConfig.MAX_WATER_DAMAGE-GConfig.MIN_WATER_DAMAGE) + GConfig.MIN_WATER_DAMAGE);
		}
		player.update(stateTime, deltaTime);
		
		//iterate through objects
		for(GObject o : gameObjects) {
			//object bellow water line
			if(o.getBody().getPosition().y < 0) {
				float hp = o.hit(random.nextInt(GConfig.MAX_WATER_DAMAGE-GConfig.MIN_WATER_DAMAGE) + GConfig.MIN_WATER_DAMAGE);
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
		//render the map
		gameMap.render(batch, stateTime, refPosition, drawReference);
		
		//render the objects
		for(GObject o: gameObjects) {
			/*batch.draw(o.getSprite(stateTime), o.getBody().getPosition().x, o.getBody().getPosition().y, 
					o.getSprite(stateTime).getWidth(), o.getSprite(stateTime).getHeight(), 
					o.getSprite(stateTime).getScaleX(), o.getSprite(stateTime).getScaleY(), 
					o.getSprite(stateTime).getRotation());*/
			if(o instanceof GObjectResource)
				continue;
			if(gameMap.isVisible(refPosition, o.getBody().getPosition(), o.getDimension()))
				batch.draw(o.getSprite(stateTime), o.getBody().getPosition().x + drawReference.x, o.getBody().getPosition().y + drawReference.y);
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

		if(GConfig.DEBUG_PHYSICS)
			debugRenderer.render(world, camera.combined);
	}
	
	public void resize(int width, int height) {
		
	}
	
	public void pause() {
		
	}
	
	public void resume() {
		
	}
	
	public void dispose() {
		gameObjects.clear();
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		for(Body b : bodies)
			world.destroyBody(b);
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
		return player.keyUp(keycode);
	}

	public boolean keyTyped(char character) {
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		isClicking = true;
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
		return true;
	}

	public boolean scrolled(int amount) {
		return false;
	}
}
