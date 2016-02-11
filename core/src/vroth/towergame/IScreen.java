package vroth.towergame;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public interface IScreen extends InputProcessor{
	public void create(World world);
	public void update(float deltaTime);
	public void render(OrthographicCamera camera, Box2DDebugRenderer debugRenderer, SpriteBatch batch);
	public void resize(int width, int height);
	public void pause();
	public void resume();
	public void dispose();
}
