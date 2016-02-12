package vroth.towergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.gmap.GMap;
import vroth.towergame.gobject.GObject;
import vroth.towergame.gobject.GObjectFactory;
import vroth.towergame.gobject.GPlayer;
import vroth.towergame.gutil.GResourcesLoader;

public class GTitleScreen implements IScreen {
	public static final int iron = GConfig.iron, gold = GConfig.gold, nothing = GConfig.nothing, dirt = GConfig.dirt;
	private TowerGame caller;
	private GMap map;
	private World world;
	private GPlayer player;
	
	private float stateTime = 0;
	private Vector2 refPosition;
	
	private int bHeight, bWidth, bTries, bRealWidth, bAmountJump;
	
	private BitmapFont titleFont;
	private float titleFontX, titleFontY;
	private GlyphLayout titleLayout;
	
	private BitmapFont startFont;
	private float startFontX, startFontY;
	private GlyphLayout startLayout;
	
	private float lastDown = 0, lastDirection = 0;
	private boolean isDown = false;
	
	public GTitleScreen(TowerGame caller) {
		this.caller = caller;
		
		titleFont = GResourcesLoader.getResourcesLoader().getFont("kenpixel_blocks.fnt");
		titleFont.getData().setScale(0.9f);
		titleFont.setColor(new Color(0, 0, 0, 1));
		titleLayout = new GlyphLayout(titleFont, GConfig.GAME_NAME);
		
		startFont = GResourcesLoader.getResourcesLoader().getFont("kenpixel_blocks.fnt");
		startFont.getData().setScale(0.6f);
	}
	
	public void updateFont(float stateTime) {
		titleFontX = (GConfig.SCREEN_WIDTH - titleLayout.width) / 2;
		titleFontY = 2*GConfig.SCREEN_HEIGHT/3 + titleLayout.height / 2;
		
		startFont.setColor(new Color((float) (1*Math.sin(stateTime)), (float) (1*Math.sin(stateTime)), (float) (1*Math.sin(stateTime)), (float) (1*Math.sin(stateTime))));
		startLayout = new GlyphLayout(startFont, "Press enter to start");
		startFontX = (GConfig.SCREEN_WIDTH - titleLayout.width) / 2;
		startFontY = GConfig.SCREEN_HEIGHT/3 - titleLayout.height / 2;
	}
	
	public void simpleMap() {
		this.map = new GMap(world);
		GObjectFactory factory = GObjectFactory.getInstance(world);
		
		bRealWidth = GConfig.MAP_WIDTH;
		bWidth = GConfig.GENERATION_WIDTH;
		bHeight = GConfig.GENERATION_HEIGHT;
		bTries = GConfig.GENERATION_TRIES;
		bAmountJump = GConfig.MAX_JUMP;
		
		GConfig.MAP_WIDTH = 70;
		GConfig.GENERATION_HEIGHT = 10;
		GConfig.GENERATION_WIDTH = 20;
		GConfig.GENERATION_TRIES = 3;
		GConfig.MAX_JUMP = 0;
		map.generateMapV2();
		
		//2 tiles down
		if(map.getSprite(0, GConfig.MAP_WIDTH/2, GConfig.GENERATION_HEIGHT-1, true) == null) {
			GObject object = factory.newDirt(new Vector2(GConfig.MAP_WIDTH/2*GConfig.TILE_SPACING, (GConfig.GENERATION_HEIGHT-1)*GConfig.TILE_SPACING), false);
			map.insertElement(object, new Vector2(GConfig.MAP_WIDTH/2, GConfig.GENERATION_HEIGHT-1), true);
		}
		
		//1 tiles down
		if(map.getSprite(0, GConfig.MAP_WIDTH/2, GConfig.GENERATION_HEIGHT, true) == null) {
			GObject object = factory.newDirt(new Vector2(GConfig.MAP_WIDTH/2*GConfig.TILE_SPACING, (GConfig.GENERATION_HEIGHT)*GConfig.TILE_SPACING), false);
			map.insertElement(object, new Vector2(GConfig.MAP_WIDTH/2, GConfig.GENERATION_HEIGHT), true);
		}
		
		player = factory.newPlayer("p1/", new Vector2(GConfig.MAP_WIDTH/2*GConfig.TILE_SPACING, (GConfig.GENERATION_HEIGHT+1)*GConfig.TILE_SPACING));
	}

	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.ENTER)
			caller.endTitleScreen();
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

	public void create(World world) {
		this.world = world;
		simpleMap();
	}

	public void update(float deltaTime) {

	}

	public void render(OrthographicCamera camera, Box2DDebugRenderer debugRenderer, SpriteBatch batch) {
		batch.begin();
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		Gdx.gl.glClearColor(GConfig.BACKGROUND_COLOR.r, GConfig.BACKGROUND_COLOR.g, GConfig.BACKGROUND_COLOR.b, GConfig.BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stateTime += Gdx.graphics.getDeltaTime();
		
		refPosition = player.getBody().getPosition();
		refPosition.x += player.getDimension().x/2;
		refPosition.y += player.getDimension().y/2;
		Vector2 drawReference = new Vector2(GConfig.SCREEN_WIDTH/2-refPosition.x, (float) ((GConfig.SCREEN_HEIGHT/2-refPosition.y) + 20*Math.sin(stateTime)));
		
		map.render(batch, stateTime, refPosition, drawReference);
		
		if(stateTime - lastDirection > 10) {
			player.changeDirection();
			player.keyDown(Input.Keys.UP);
			lastDirection = stateTime;
		}
		
		if(Math.sin(stateTime) < 0 && (stateTime-lastDown) > 1) {
			player.keyUp(Input.Keys.DOWN);
			isDown = !isDown;
			lastDown = stateTime;
		}
		else if(Math.cos(stateTime) > 0.4 && (stateTime - lastDown) > 1) {
			player.keyDown(Input.Keys.DOWN);
			isDown = !isDown;
			lastDown = stateTime;
		}
		player.update(stateTime, 0);
		player.render(batch, stateTime, drawReference);
	
		updateFont(stateTime);
		titleFont.draw(batch, titleLayout, titleFontX, titleFontY);
		startFont.draw(batch, startLayout, startFontX, startFontY);
		
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
		
		GConfig.MAP_WIDTH = bRealWidth;
		GConfig.GENERATION_WIDTH = bWidth;
		GConfig.GENERATION_HEIGHT = bHeight;
		GConfig.GENERATION_TRIES = bTries;
		GConfig.MAX_JUMP = bAmountJump;
		GConfig.DRAW_PLAYER_HEALTH = true;
	}

}
