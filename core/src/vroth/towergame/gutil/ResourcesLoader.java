package vroth.towergame.gutil;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class ResourcesLoader {
	private static ResourcesLoader instance = null;
	private Texture errTexture = null;
	
	private ResourcesLoader() {
		Pixmap p = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
		errTexture = new Texture(p);
		p.setColor(Color.WHITE);
		p.fillRectangle(0, 0, 63, 63);
		p.setColor(Color.RED);
		p.fillRectangle(1, 1, 61, 61);
		p.setColor(Color.BLACK);
		p.drawLine(0, 0, 63, 63);
		p.drawLine(0, 63, 63, 0);
		errTexture.draw(p, 0, 0);
	}

	public static ResourcesLoader getResourcesLoader() {
		if(instance == null)
			instance = new ResourcesLoader();
		return instance;
	}
	
	public Texture loadTexture(String file) {
		try {
			return new Texture(file);
		}
		catch(Exception e) {
			System.err.println("Error loading file " + file);
			return errTexture;
		}
	}
}
