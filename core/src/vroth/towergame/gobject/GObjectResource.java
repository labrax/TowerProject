package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class GObjectResource extends GObject {

	public GObjectResource(Fixture fixture, Body body, Sprite staticSprite, Vector2 dimension, float health, float creationTime) {
		super(fixture, body, staticSprite, dimension, health, health, creationTime);
	}

}
