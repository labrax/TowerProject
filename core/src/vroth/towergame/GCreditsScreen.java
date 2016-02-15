package vroth.towergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import vroth.towergame.gobject.GCreature;
import vroth.towergame.gutil.GResourcesLoader;

public class GCreditsScreen implements IScreen {
	private TowerGame caller;
	private float stateTime = 0;
	
	private BitmapFont creditsFont;
	
	public GCreditsScreen(TowerGame caller) {
		this.caller = caller;
		
		creditsFont = GResourcesLoader.getInstance().getFont("kenpixel_blocks.fnt");
		creditsFont.getData().setScale(0.8f);
		creditsFont.setColor(new Color(0, 0, 0, 1));
	}

	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.ESCAPE || keycode == Input.Keys.ENTER) {
			caller.endState();
		}
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

	}

	public void update(float deltaTime) {
		stateTime += deltaTime;		
		if(GConfig.CREDITS_TIME < stateTime) {
			caller.endState();
		}
	}

	public void render(OrthographicCamera camera, Box2DDebugRenderer debugRenderer, SpriteBatch batch) {
		camera.update();
		Gdx.gl.glClearColor(GConfig.BACKGROUND_COLOR.r, GConfig.BACKGROUND_COLOR.g, GConfig.BACKGROUND_COLOR.b, GConfig.BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		String[] message = {"Credits", "Victor R. - coding", "L. A. - game design & testing", "kenney.nl - sprites (thanks!)"};
		GlyphLayout creditsLayout;
		
		batch.begin();
		for(int i = 0; i < message.length; i++) {
			creditsLayout = new GlyphLayout(creditsFont, message[i]);
	
			float creditsFontX = (GConfig.SCREEN_WIDTH - creditsLayout.width) / 2;
			float creditsFontY = 2*GConfig.SCREEN_HEIGHT/3 + creditsLayout.height / 2;
			
			creditsFont.draw(batch, creditsLayout, creditsFontX, creditsFontY - 60*i);
		}
		batch.end();
	}

	public void resize(int width, int height) {

	}

	public void pause() {

	}

	public void resume() {

	}

	public void dispose() {

	}

	public void setCreatureForRemoval(GCreature creature) {

	}

	public float getStateTime() {
		return 0;
	}

}
