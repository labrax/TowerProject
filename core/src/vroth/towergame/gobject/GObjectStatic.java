package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class GObjectStatic extends GObject {
	private Sprite sprite = null;
	
	public GObjectStatic(Sprite sprite) {
		this.sprite = sprite;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void update() {
		
	}
}
