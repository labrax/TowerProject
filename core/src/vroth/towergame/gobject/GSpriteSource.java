package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class GSpriteSource {
	public abstract Sprite getSprite(float stateTime);
	public abstract Sprite getSprite();
}
