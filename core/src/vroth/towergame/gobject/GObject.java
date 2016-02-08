package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class GObject {
	
	protected GObject() {

	}
	
	public abstract Sprite getSprite();
	
	public abstract void update();

}
