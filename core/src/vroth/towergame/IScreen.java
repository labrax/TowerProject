package vroth.towergame;

import com.badlogic.gdx.InputProcessor;

public interface IScreen extends InputProcessor{
	public void create();
	public void update(float deltaTime);
	public void render();
	public void resize(int width, int height);
	public void pause();
	public void resume();
	public void dispose();
	
	public boolean keyDown(int keycode);
	public boolean keyUp(int keycode);
	public boolean keyTyped(char character);
	public boolean touchDown(int screenX, int screenY, int pointer, int button);
	public boolean touchUp(int screenX, int screenY, int pointer, int button);
	public boolean touchDragged(int screenX, int screenY, int pointer);
	public boolean mouseMoved(int screenX, int screenY);
	public boolean scrolled(int amount);
}
