package vroth.towergame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
	
	private float stateTime;

	private Array<GObject> gameObjects = null;
	private GMap gameMap = null;
	
	public void create() {
		GConfig.SCREEN_WIDTH = Gdx.graphics.getWidth();
		GConfig.SCREEN_HEIGHT = Gdx.graphics.getHeight();
		
		gameObjects = new Array<GObject>();
		gameMap = new GMap();
		
		world = new World(new Vector2(0, -98f), true);
		
		GObjectFactory factory = GObjectFactory.getInstance();
		GObject tile;
		for(int i = 0; i < 30; i++) {
			tile = factory.newTile(world, "tiles/grass", i*70, 0);
			gameObjects.add(tile);
			tile = factory.newTile(world, "tiles/grass", i*70, 70);
			gameObjects.add(tile);
		}
		player = factory.newPlayer(world, "p2/", GConfig.SCREEN_WIDTH/2, GConfig.SCREEN_HEIGHT);
		gameObjects.add(player);
		
		tile = factory.newBox(world, "tiles/grass0.png", GConfig.SCREEN_WIDTH/2+80, GConfig.SCREEN_HEIGHT/2 + 50);
		gameObjects.add(tile);
		
		tile = factory.newBox(world, "tiles/grass0.png", GConfig.SCREEN_WIDTH/2+80, GConfig.SCREEN_HEIGHT/2);
		gameObjects.add(tile);

		
		batch = new SpriteBatch();
		Gdx.input.setInputProcessor(this);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public void render() {
		camera.update();
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		stateTime += Gdx.graphics.getDeltaTime();
		player.update(stateTime);
		//splayer.setPosition(body.getPosition().x, body.getPosition().y);
		
		Gdx.gl.glClearColor(GConfig.BACKGROUND_COLOR.r, GConfig.BACKGROUND_COLOR.g, GConfig.BACKGROUND_COLOR.b, GConfig.BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//batch.setProjectionMatrix(camera.combined);
		//debugMatrix = batch.getProjectionMatrix().cpy();
		batch.begin();
		
		for(GObject o: gameObjects) {
			/*batch.draw(o.getSprite(stateTime), o.getBody().getPosition().x, o.getBody().getPosition().y, 
					o.getSprite(stateTime).getWidth(), o.getSprite(stateTime).getHeight(), 
					o.getSprite(stateTime).getScaleX(), o.getSprite(stateTime).getScaleY(), 
					o.getSprite(stateTime).getRotation());*/
			batch.draw(o.getSprite(stateTime), o.getBody().getPosition().x, o.getBody().getPosition().y);
			batch.flush();
		}
		
		debugRenderer.render(world, batch.getProjectionMatrix());
		
		batch.end();
	}
	
	public void resize (int width, int height) {
		GConfig.SCREEN_WIDTH = width;
		GConfig.SCREEN_HEIGHT = height;
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}
	
	public void pause() {
		
	}
	
	public void resume() {
		
	}
	
	public void dispose() {
		gameObjects.clear();
		world.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.RIGHT) {
			player.movePlayer(new Vector2(50f, player.getBody().getLinearVelocity().y));
			System.out.println("right");
			
		}
        if(keycode == Input.Keys.LEFT) {
        	player.movePlayer(new Vector2(-50f, player.getBody().getLinearVelocity().y));
        	System.out.println("left");
        }
        if(keycode == Input.Keys.SPACE) {
        	player.movePlayer(new Vector2(player.getBody().getLinearVelocity().x, 200f));
        	System.out.println("space");
        }
		return true;
	}

	public boolean keyUp(int keycode) {
		

		return false;
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
