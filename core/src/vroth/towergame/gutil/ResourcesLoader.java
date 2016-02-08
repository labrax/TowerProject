package vroth.towergame.gutil;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ResourcesLoader {
	private static ResourcesLoader instance = null;
	private Sprite errSprite = null;
	
	private ResourcesLoader() {
		Pixmap p = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
		Texture errTexture = new Texture(p);
		p.setColor(Color.WHITE);
		p.fillRectangle(0, 0, 63, 63);
		p.setColor(Color.RED);
		p.fillRectangle(1, 1, 61, 61);
		p.setColor(Color.BLACK);
		p.drawLine(0, 0, 63, 63);
		p.drawLine(0, 63, 63, 0);
		errTexture.draw(p, 0, 0);
		errSprite = new Sprite(errTexture);
	}

	public static ResourcesLoader getResourcesLoader() {
		if(instance == null)
			instance = new ResourcesLoader();
		return instance;
	}
	
	//TODO: create a cache?
	public Sprite loadSprite(String file) {
		try {
			Texture t = new Texture(file);
			return new Sprite(t);
		}
		catch(Exception e) {
			System.err.println("Error loading file " + file);
			return errSprite;
		}
	}
}
