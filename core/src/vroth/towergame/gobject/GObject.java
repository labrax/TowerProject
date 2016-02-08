package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class GObject {
	protected Body body;
	protected Sprite staticSprite;
	
	public GObject(Body body, Sprite staticSprite) {
		this.body = body;
		this.staticSprite = staticSprite;
	}
	
	public Sprite getSprite(float stateTime) {
		return staticSprite;
	}
	
	public Body getBody() {
		return body;
	}
}
