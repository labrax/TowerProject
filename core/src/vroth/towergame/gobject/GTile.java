package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class GTile extends GObject {
	
	public GTile(Body body, Sprite staticSprite) {
		super(body, staticSprite);
	}

	public Sprite getSprite() {
		//TODO: use http://gamedevelopment.tutsplus.com/tutorials/how-to-use-tile-bitmasking-to-auto-tile-your-level-layouts--cms-25673
		return null;
	}
	
	//TODO: all
}
