package vroth.towergame.gobject;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

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
		Sprite front = rl.loadSprite(folder + "p1_front.png");
		Sprite duck = rl.loadSprite(folder + "p1_duck.png");
		Sprite hurt = rl.loadSprite(folder + "p1_hurt.png");
		Sprite jump = rl.loadSprite(folder + "p1_jump.png");
		Sprite stand = rl.loadSprite(folder + "p1_stand.png");
		
		Array<Sprite> walkArray = new Array<Sprite>();
		walkArray.add(rl.loadSprite(folder + "p1_walk01.png"));
		walkArray.add(rl.loadSprite(folder + "p1_walk02.png"));
		walkArray.add(rl.loadSprite(folder + "p1_walk03.png"));
		walkArray.add(rl.loadSprite(folder + "p1_walk04.png"));
		walkArray.add(rl.loadSprite(folder + "p1_walk05.png"));
		walkArray.add(rl.loadSprite(folder + "p1_walk06.png"));
		walkArray.add(rl.loadSprite(folder + "p1_walk07.png"));
		walkArray.add(rl.loadSprite(folder + "p1_walk08.png"));
		walkArray.add(rl.loadSprite(folder + "p1_walk09.png"));
		walkArray.add(rl.loadSprite(folder + "p1_walk10.png"));
		walkArray.add(rl.loadSprite(folder + "p1_walk11.png"));
		
		Animation walk = new Animation(0.5f, walkArray);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(front.getWidth()/2, front.getHeight()/2-10);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0.1f;

        body.createFixture(fixtureDef);
				
		shape.dispose();
		
		return new GPlayer(body, duck, front, hurt, jump, stand, walk); 
	}
	
	public GTile newTile(World world, String folder, int posX, int posY) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(posX, posY);
		Body body = world.createBody(bodyDef);
		
		//TODO: add other sprites
		Sprite staticSprite = ResourcesLoader.getResourcesLoader().loadSprite(folder);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        
        body.createFixture(fixtureDef);
        shape.dispose();
        
		return new GTile(body, staticSprite);
	}
	
	public GTile newBox(World world, String folder, int posX, int posY) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(posX, posY);
		Body body = world.createBody(bodyDef);
		
		//TODO: add other sprites
		Sprite staticSprite = ResourcesLoader.getResourcesLoader().loadSprite(folder);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(staticSprite.getWidth()/2, staticSprite.getHeight()/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        
        body.createFixture(fixtureDef);
        shape.dispose();
        
		return new GTile(body, staticSprite);
	}
}
