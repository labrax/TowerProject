package vroth.towergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.gmap.GMap;
import vroth.towergame.gobject.GObjectFactory;
import vroth.towergame.gobject.GPlayer;

public class GTitleScreen implements IScreen {
	private TowerGame caller;
	private GMap map;
	private World world;
	private GPlayer player;
	
	private SpriteBatch batch;
	
	private float stateTime = 0;
	private Vector2 refPosition;
	
	public GTitleScreen(TowerGame caller, SpriteBatch batch) {		
		this.batch = batch;
		
		world = new World(new Vector2(0, -98f), true);
		this.caller = caller;
		simpleMap();
	}
	
	public void simpleMap() {
		this.map = new GMap(world);
		GObjectFactory factory = GObjectFactory.getInstance(world);
		
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 30; j++) {
				Vector2 position = new Vector2(j*70, i*70); 
				map.insertElement(factory.newDirt(position, false), j, i, true);
			}
		}
		player = factory.newPlayer("p1/", new Vector2(30/2*70, 10*70));
		refPosition = player.getBody().getPosition();
		refPosition.x += player.getDimension().x/2;
		refPosition.y += player.getDimension().y/2;
		
		player.keyDown(Input.Keys.LEFT);
	}

	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.ENTER)
			;
		return true;
	}

	public boolean keyUp(int keycode) {
		return false;
	}

	public boolean keyTyped(char character) {
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	public boolean scrolled(int amount) {
		return false;
	}

	public void create() {

	}

	public void update(float deltaTime) {

	}

	public void render() {
		batch.begin();
		Gdx.gl.glClearColor(GConfig.BACKGROUND_COLOR.r, GConfig.BACKGROUND_COLOR.g, GConfig.BACKGROUND_COLOR.b, GConfig.BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stateTime += Gdx.graphics.getDeltaTime();
		Vector2 drawReference = new Vector2(GConfig.SCREEN_WIDTH/2-refPosition.x, GConfig.SCREEN_HEIGHT/2-refPosition.y);
		
		map.render(batch, stateTime, refPosition, drawReference);
		
		player.render(batch, stateTime, drawReference);
		batch.end();
	}

	public void resize(int width, int height) {

	}

	public void pause() {

	}

	public void resume() {

	}

	public void dispose() {
		Array<Body> allBodies = new Array<Body>();
		world.getBodies(allBodies);
		for(Body b: allBodies)
			world.destroyBody(b);
	}

}
