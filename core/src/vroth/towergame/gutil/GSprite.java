package vroth.towergame.gutil;

import com.badlogic.gdx.graphics.Texture;

public class GSprite {
	Texture img = null;
	
	public GSprite(Texture img) {
		this.img = img;
	}
	
	public Texture getTexture() {
		return img;
	}

}
