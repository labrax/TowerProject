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
}
