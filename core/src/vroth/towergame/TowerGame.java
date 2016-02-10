package vroth.towergame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class TowerGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	
	GPlayScreen playScreen = null;
	IScreen currScreen;
	
	public void create() {
		//Gdx.graphics.setWindowedMode(800, 600);
		GConfig.SCREEN_WIDTH = Gdx.graphics.getWidth();
		GConfig.SCREEN_HEIGHT = Gdx.graphics.getHeight();
		
		batch = new SpriteBatch();
		camera = new OrthographicCamera(GConfig.SCREEN_WIDTH, GConfig.SCREEN_HEIGHT);
		debugRenderer = new Box2DDebugRenderer();
		
		playScreen = new GPlayScreen(camera, batch, debugRenderer);
		currScreen = playScreen;
		
		currScreen.create();
		Gdx.input.setInputProcessor(this);
	}
	
	public void render() {
		currScreen.update(Gdx.graphics.getDeltaTime());
		camera.update();
		currScreen.render();
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
		if(playScreen != null)
			playScreen.dispose();
	}
	
	public boolean keyDown(int keycode) {
		return currScreen.keyDown(keycode);
	}

	public boolean keyUp(int keycode) {
		return currScreen.keyUp(keycode);
	}

	public boolean keyTyped(char character) {
		return currScreen.keyTyped(character);
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return currScreen.touchDown(screenX, screenY, pointer, button);
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return currScreen.touchUp(screenX, screenY, pointer, button);
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return currScreen.touchDragged(screenX, screenY, pointer);
	}

	public boolean mouseMoved(int screenX, int screenY) {
		return currScreen.mouseMoved(screenX, screenY);
	}

	public boolean scrolled(int amount) {
		return currScreen.scrolled(amount);
	}

}
