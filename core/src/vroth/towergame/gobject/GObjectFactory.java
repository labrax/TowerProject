package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.GConfig;
import vroth.towergame.gutil.ResourcesLoader;

public class GObjectFactory {
	private static GObjectFactory instance = null; 
	
	private GObjectFactory() {
		
	}

	public static GObjectFactory getInstance() {
		if(instance == null)
			instance = new GObjectFactory();
		return instance;
	}
	
	public GPlayer newPlayer(World world, String folder, int posX, int posY) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(posX, posY);
		Body body = world.createBody(bodyDef);
		
		ResourcesLoader rl = ResourcesLoader.getResourcesLoader();
		Sprite front = rl.loadSprite(folder + "front.png");
		Sprite duck = rl.loadSprite(folder + "duck.png");
		Sprite hurt = rl.loadSprite(folder + "hurt.png");
		Sprite jump = rl.loadSprite(folder + "jump.png");
		Sprite stand = rl.loadSprite(folder + "stand.png");
		
		Array<Sprite> walkArray = new Array<Sprite>();
		walkArray.add(rl.loadSprite(folder + "walk01.png"));
		walkArray.add(rl.loadSprite(folder + "walk02.png"));
		walkArray.add(rl.loadSprite(folder + "walk03.png"));
		walkArray.add(rl.loadSprite(folder + "walk04.png"));
		walkArray.add(rl.loadSprite(folder + "walk05.png"));
		walkArray.add(rl.loadSprite(folder + "walk06.png"));
		walkArray.add(rl.loadSprite(folder + "walk07.png"));
		walkArray.add(rl.loadSprite(folder + "walk08.png"));
		walkArray.add(rl.loadSprite(folder + "walk09.png"));
		walkArray.add(rl.loadSprite(folder + "walk10.png"));
		walkArray.add(rl.loadSprite(folder + "walk11.png"));
		Animation walk = new Animation(0.2f, walkArray);
		
		Sprite badge1 = rl.loadSprite(folder + "badge1.png");
		Sprite badge2 = rl.loadSprite(folder + "badge2.png");
		
		Array<Sprite> swimArray = new Array<Sprite>();
		swimArray.add(rl.loadSprite(folder + "swim1.png"));
		swimArray.add(rl.loadSprite(folder + "swim2.png"));
		Animation swim = new Animation(0.2f, swimArray);
		
		Array<Sprite> climbArray = new Array<Sprite>();
		climbArray.add(rl.loadSprite(folder + "climb1.png"));
		climbArray.add(rl.loadSprite(folder + "climb2.png"));
		Animation climb = new Animation(0.2f, climbArray);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(front.getWidth()/2, front.getHeight()/2, new Vector2(front.getWidth()/2, front.getHeight()/2), 0);
		
		Vector2 dimension = new Vector2(front.getWidth(), front.getHeight());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.filter.categoryBits = GConfig.CATEGORY_PLAYER;
        fixtureDef.filter.maskBits = GConfig.MASK_PLAYER;

        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
				
		shape.dispose();
		
		return new GPlayer(body, duck, front, hurt, jump, stand, walk, swim, climb, badge1, badge2, dimension); 
	}
	
	public GTile newTile(World world, String filePrefix, int posX, int posY) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(posX, posY);
		Body body = world.createBody(bodyDef);
		
		Sprite[] sprites = new Sprite[16];
		for(int i = 0; i < 16; i++){
			sprites[i] = ResourcesLoader.getResourcesLoader().loadSprite(filePrefix + i + ".png");
		}
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(sprites[0].getWidth()/2, sprites[0].getHeight()/2, new Vector2(sprites[0].getWidth()/2, sprites[0].getHeight()/2), 0);

		Vector2 dimension = new Vector2(sprites[0].getWidth(), sprites[0].getHeight());
		
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GConfig.CATEGORY_FTILE;
        fixtureDef.filter.maskBits = GConfig.MASK_FTILE;
        
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
        
		return new GTile(body, sprites, dimension);
	}
	
	public GTile newTile(World world, String filePrefix, int posX, int posY, boolean physics) {
		if(physics)
			return newTile(world, filePrefix, posX, posY);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(posX, posY);
		Body body = world.createBody(bodyDef);
		
		Sprite[] sprites = new Sprite[16];
		for(int i = 0; i < 16; i++){
			sprites[i] = ResourcesLoader.getResourcesLoader().loadSprite(filePrefix + i + ".png");
		}
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(sprites[0].getWidth()/2, sprites[0].getHeight()/2, new Vector2(sprites[0].getWidth()/2, sprites[0].getHeight()/2), 0);

		Vector2 dimension = new Vector2(sprites[0].getWidth(), sprites[0].getHeight());
		
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = GConfig.CATEGORY_BTILE;
        fixtureDef.filter.maskBits = GConfig.MASK_NO_TOUCH;
        
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
        
		return new GTile(body, sprites, dimension);
	}
	
	public GObject newStaticObject(World world, String file, int posX, int posY) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(posX, posY);
		Body body = world.createBody(bodyDef);
		
		Sprite staticSprite = ResourcesLoader.getResourcesLoader().loadSprite(file);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2, new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2), 0);
		
		Vector2 dimension = new Vector2(staticSprite.getWidth(), staticSprite.getHeight());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = GConfig.CATEGORY_FTILE;
        fixtureDef.filter.maskBits = GConfig.MASK_FTILE;
        
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
        
		return new GObject(body, staticSprite, dimension);
	}
	
	public GObject newStaticObject(World world, String file, int posX, int posY, boolean physics) {
		if(physics)
			newStaticObject(world, file, posX, posY);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(posX, posY);
		Body body = world.createBody(bodyDef);
		
		Sprite staticSprite = ResourcesLoader.getResourcesLoader().loadSprite(file);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2, new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2), 0);
		
		Vector2 dimension = new Vector2(staticSprite.getWidth(), staticSprite.getHeight());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = GConfig.CATEGORY_BTILE;
        fixtureDef.filter.maskBits = GConfig.MASK_NO_TOUCH;
        
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
        
		return new GObject(body, staticSprite, dimension);
	}
	
	public GObject newBox(World world, String folder, int posX, int posY) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(posX, posY);
		Body body = world.createBody(bodyDef);
		
		Sprite staticSprite = ResourcesLoader.getResourcesLoader().loadSprite(folder);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2, new Vector2(staticSprite.getWidth()/2, staticSprite.getHeight()/2), 0);

		Vector2 dimension = new Vector2(staticSprite.getWidth(), staticSprite.getHeight());
		
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0.5f;
        
        fixtureDef.filter.categoryBits = GConfig.CATEGORY_FTILE;
        fixtureDef.filter.maskBits = GConfig.MASK_FTILE;
        
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
        
		return new GObject(body, staticSprite, dimension);
	}
}
