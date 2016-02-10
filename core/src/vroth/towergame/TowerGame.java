package vroth.towergame;

import java.awt.DisplayMode;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.gmap.GMap;
import vroth.towergame.gobject.GObject;
import vroth.towergame.gobject.GObjectFactory;
import vroth.towergame.gobject.GPlayer;

public class TowerGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	World world;
	GPlayer player;
	
	Box2DDebugRenderer debugRenderer;
	OrthographicCamera camera;
	Matrix4 debugMatrix;
	
	float refX, refY;
	
	private float stateTime;

	private Array<GObject> gameObjects = null;
	private GMap gameMap = null;
	
	public void create() {
		//Gdx.graphics.setWindowedMode(800, 600);
		GConfig.SCREEN_WIDTH = Gdx.graphics.getWidth();
		GConfig.SCREEN_HEIGHT = Gdx.graphics.getHeight();
		
		world = new World(new Vector2(0, -98f), true);
		
		gameObjects = new Array<GObject>();
		gameMap = new GMap(world);
		
		GObjectFactory factory = GObjectFactory.getInstance();
		GObject tile;
		/*for(int i = 0; i < 30; i++) {
			tile = factory.newTile(world, "tiles/grass", i*70, 0);
			gameObjects.add(tile);
			tile = factory.newTile(world, "tiles/grass", i*70, 70);
			gameObjects.add(tile);
		}*/
		player = factory.newPlayer(world, "p1/", GConfig.MAP_WIDTH/2*70, (GConfig.GENERATION_HEIGHT+1)*70);
		//gameObjects.add(player);
		
		tile = factory.newBox(world, "tiles/boxCoin.png", GConfig.MAP_WIDTH/2*70+100, (GConfig.GENERATION_HEIGHT+1)*70 + 100);
		gameObjects.add(tile);
		
		tile = factory.newBox(world, "tiles/boxCoin.png", GConfig.MAP_WIDTH/2*70+100, (GConfig.GENERATION_HEIGHT+1)*70 + 150);
		gameObjects.add(tile);

		
		batch = new SpriteBatch();
		Gdx.input.setInputProcessor(this);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(GConfig.SCREEN_WIDTH, GConfig.SCREEN_HEIGHT);
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
	
	public void render() {
		camera.update();
		
		
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		stateTime += Gdx.graphics.getDeltaTime();
		
		player.update(stateTime);
		//System.out.println(player.getBody().getLinearVelocity());
		
		Gdx.gl.glClearColor(GConfig.BACKGROUND_COLOR.r, GConfig.BACKGROUND_COLOR.g, GConfig.BACKGROUND_COLOR.b, GConfig.BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//batch.setProjectionMatrix(camera.combined);
		//debugMatrix = batch.getProjectionMatrix().cpy();
		
		Vector2 refPosition;
		if(GConfig.DEBUG_CONTROLS)
			refPosition = new Vector2(refX, refY);
		else
			refPosition = player.getBody().getPosition();
		refPosition.x += player.getDimension().x/2;
		refPosition.y += player.getDimension().y/2;
		
		Vector2 drawReference = new Vector2(GConfig.SCREEN_WIDTH/2-refPosition.x, GConfig.SCREEN_HEIGHT/2-refPosition.y);
		
		camera.position.set(refPosition, 0);
		
		batch.begin();
		for(int i = 0; i < gameMap.size(); i++) {
			for(int j = 0; j < GConfig.MAP_WIDTH; j++) {
				GObject b = gameMap.getMapLine(i).getObjectBackground(j);
				Sprite toDraw = gameMap.getElement(stateTime, j, i, false);
				if(b != null && toDraw != null && isVisible(refPosition, b.getBody().getPosition(), b.getDimension())) {
					batch.draw(toDraw, b.getBody().getPosition().x + drawReference.x, b.getBody().getPosition().y + drawReference.y);
					Color c = batch.getColor();
					batch.setColor(new Color(0.6f, 0.2f, 0f, 0.7f));
					batch.draw(toDraw, b.getBody().getPosition().x + drawReference.x, b.getBody().getPosition().y + drawReference.y);
					batch.setColor(c);
				}
				
				toDraw = gameMap.getElement(stateTime, j, i, true);
				GObject o = gameMap.getMapLine(i).getObjectForeground(j);
				if(o != null && toDraw != null && isVisible(refPosition, o.getBody().getPosition(), o.getDimension())) {
					batch.draw(toDraw, o.getBody().getPosition().x + drawReference.x, o.getBody().getPosition().y + drawReference.y);
				}
			}
		}
		batch.flush();
		
		for(GObject o: gameObjects) {
			/*batch.draw(o.getSprite(stateTime), o.getBody().getPosition().x, o.getBody().getPosition().y, 
					o.getSprite(stateTime).getWidth(), o.getSprite(stateTime).getHeight(), 
					o.getSprite(stateTime).getScaleX(), o.getSprite(stateTime).getScaleY(), 
					o.getSprite(stateTime).getRotation());*/
			if(isVisible(refPosition, o.getBody().getPosition(), o.getDimension()))
				batch.draw(o.getSprite(stateTime), o.getBody().getPosition().x + drawReference.x, o.getBody().getPosition().y + drawReference.y);
		}
		batch.flush();

		batch.draw(player.getSprite(stateTime), player.getBody().getPosition().x + drawReference.x, player.getBody().getPosition().y + drawReference.y);
		batch.end();

		//cant change debugrenderer position :/
		//camera.position.set(player.getBody().getPosition().x + drawReference.x, player.getBody().getPosition().y + drawReference.y, 0);
		//Box2DDebugRenderer.setAxis(new Vector2(player.getBody().getPosition().x + drawReference.x, player.getBody().getPosition().y + drawReference.y));
		//debugRenderer.render(world, batch.getProjectionMatrix());
		
		if(GConfig.DEBUG_PHYSICS)
			debugRenderer.render(world, camera.combined);
	}
	
	public void resize (int width, int height) {
		GConfig.SCREEN_WIDTH = width;
		GConfig.SCREEN_HEIGHT = height;
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		camera = new OrthographicCamera(GConfig.SCREEN_WIDTH, GConfig.SCREEN_HEIGHT);
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
				refX = player.getBody().getPosition().x;
				refY = player.getBody().getPosition().y;
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

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
