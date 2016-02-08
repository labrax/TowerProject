package vroth.towergame;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import vroth.towergame.gmap.GMap;
import vroth.towergame.gobject.GAnimation;
import vroth.towergame.gobject.GObject;
import vroth.towergame.gutil.GSprite;
import vroth.towergame.gutil.ResourcesLoader;

public class TowerGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Texture background;
	Texture player;
	Sprite splayer;
	Texture bb;
	GAnimation ga;
	
	Body body;
	
	World world;

	private ArrayList<GObject> gameObjects = null;
	private GMap gameMap = null;
	
	public void create() {
		gameObjects = new ArrayList<GObject>();
		gameMap = new GMap();
		GConfig.SCREEN_WIDTH = Gdx.graphics.getWidth();
		GConfig.SCREEN_HEIGHT = Gdx.graphics.getHeight();
		
		batch = new SpriteBatch();
		img = ResourcesLoader.getResourcesLoader().loadTexture("tile.jpg");
		background = ResourcesLoader.getResourcesLoader().loadTexture("grassMid.png");
		player = ResourcesLoader.getResourcesLoader().loadTexture("p1_front.png");
		bb = ResourcesLoader.getResourcesLoader().loadTexture("grassCenter.png");
		GSprite[] sprites = new GSprite[2];
		sprites[0] = new GSprite(ResourcesLoader.getResourcesLoader().loadTexture("1.png"));
		sprites[1] = new GSprite(ResourcesLoader.getResourcesLoader().loadTexture("2.png"));
		ga = new GAnimation(sprites, 600, 300, 140);
		
		splayer = new Sprite(player);
		
		world = new World(new Vector2(0, -98f), true);
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(GConfig.SCREEN_WIDTH/2, GConfig.SCREEN_HEIGHT);
		body = world.createBody(bodyDef);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(splayer.getWidth()/2, splayer.getHeight()/2);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		
		Fixture fixture = body.createFixture(fixtureDef);
		
		shape.dispose();
	}
	
	public void render() {
		
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		splayer.setPosition(body.getPosition().x, body.getPosition().y);
		
		Gdx.gl.glClearColor(GConfig.BACKGROUND_COLOR.r, GConfig.BACKGROUND_COLOR.g, GConfig.BACKGROUND_COLOR.b, GConfig.BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for(int i = 0; i < 30; i++) {
			batch.draw(bb, i*64, 0, 64, 64, 0, 0, bb.getWidth(), bb.getHeight(), false, false);
			batch.draw(background, i*64, 64, 64, 64, 0, 0, background.getWidth(), background.getHeight(), false, false);
		}
		Color c = new Color(batch.getColor());
		batch.setColor(1.0f,0f,0f,1.0f);
		batch.draw(splayer, splayer.getX(), splayer.getY());
		//batch.draw(player, 231, 128, player.getWidth(), player.getHeight(), 0, 0, player.getWidth(), player.getHeight(), false, false);
		batch.setColor(c);

		Texture t = ga.getSprite().getTexture();
		batch.draw(t, ga.getPosX(), ga.getPosY(), t.getWidth(), t.getHeight(), 0, 0, t.getWidth(), t.getHeight(), false, false);
		
		
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
		
	}
}
