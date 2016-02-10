package vroth.towergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.gmap.GMap;
import vroth.towergame.gobject.GObject;
import vroth.towergame.gobject.GObjectFactory;
import vroth.towergame.gobject.GPlayer;

public class GPlayScreen implements IScreen {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Box2DDebugRenderer debugRenderer;

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
	
	protected GPlayScreen(OrthographicCamera camera, SpriteBatch batch, Box2DDebugRenderer debugRenderer) {
		this.camera = camera;
		this.batch = batch;
		this.debugRenderer = debugRenderer;
	}
	
	public void create() {
		pointerPosition = new Vector2(0, 0);
		mousePosition = new Vector2(0, 0);
		
		world = new World(new Vector2(0, -98f), true);
		
		gameObjects = new Array<GObject>();
		gameMap = new GMap(world);
		
		GObjectFactory factory = GObjectFactory.getInstance(world);
		player = factory.newPlayer("p1/", new Vector2(GConfig.MAP_WIDTH/2*70, (GConfig.GENERATION_HEIGHT+1)*70));
		
		/*GObject tile;
		tile = factory.newBox(new Vector2(GConfig.MAP_WIDTH/2*70+100, (GConfig.GENERATION_HEIGHT+1)*70 + 100));
		gameObjects.add(tile);
		tile = factory.newBox(new Vector2(GConfig.MAP_WIDTH/2*70+100, (GConfig.GENERATION_HEIGHT+1)*70 + 150));
		gameObjects.add(tile);*/
	}
	
	private boolean isVisible(Vector2 reference, Vector2 position, Vector2 size) {
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
		
		player.update(stateTime);
		
		if(GConfig.DEBUG_CONTROLS)
			refPosition = new Vector2(refX, refY);
		else {
			refPosition = player.getBody().getPosition();
			refPosition.x += player.getDimension().x/2;
			refPosition.y += player.getDimension().y/2;
		}
	}
	
	public void render() {
		Gdx.gl.glClearColor(GConfig.BACKGROUND_COLOR.r, GConfig.BACKGROUND_COLOR.g, GConfig.BACKGROUND_COLOR.b, GConfig.BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Vector2 drawReference = new Vector2(GConfig.SCREEN_WIDTH/2-refPosition.x, GConfig.SCREEN_HEIGHT/2-refPosition.y);
		
		camera.position.set(refPosition, 0);
		
		batch.begin();
		for(int i = 0; i < gameMap.size(); i++) {
			for(int j = 0; j < GConfig.MAP_WIDTH; j++) {
				GObject b = gameMap.getMapLine(i).getObjectBackground(j);
				Sprite toDraw = gameMap.getSprite(stateTime, j, i, false);
				if(b != null && toDraw != null && isVisible(refPosition, b.getBody().getPosition(), b.getDimension())) {
					batch.draw(toDraw, b.getBody().getPosition().x + drawReference.x, b.getBody().getPosition().y + drawReference.y);
					Color c = batch.getColor();
					batch.setColor(new Color(0.6f, 0.2f, 0f, 0.7f));
					batch.draw(toDraw, b.getBody().getPosition().x + drawReference.x, b.getBody().getPosition().y + drawReference.y);
					batch.setColor(c);
				}
				
				toDraw = gameMap.getSprite(stateTime, j, i, true);
				GObject o = gameMap.getMapLine(i).getObjectForeground(j);
				if(o != null && toDraw != null && isVisible(refPosition, o.getBody().getPosition(), o.getDimension())) {
					batch.draw(toDraw, o.getBody().getPosition().x + drawReference.x, o.getBody().getPosition().y + drawReference.y);
				}
			}
		}
		
		for(GObject o: gameObjects) {
			/*batch.draw(o.getSprite(stateTime), o.getBody().getPosition().x, o.getBody().getPosition().y, 
					o.getSprite(stateTime).getWidth(), o.getSprite(stateTime).getHeight(), 
					o.getSprite(stateTime).getScaleX(), o.getSprite(stateTime).getScaleY(), 
					o.getSprite(stateTime).getRotation());*/
			if(isVisible(refPosition, o.getBody().getPosition(), o.getDimension()))
				batch.draw(o.getSprite(stateTime), o.getBody().getPosition().x + drawReference.x, o.getBody().getPosition().y + drawReference.y);
		}

		batch.draw(player.getSprite(stateTime), player.getBody().getPosition().x + drawReference.x, player.getBody().getPosition().y + drawReference.y);
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
		world.dispose();
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
