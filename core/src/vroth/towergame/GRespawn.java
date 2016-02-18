package vroth.towergame;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import vroth.towergame.gmap.GMap;
import vroth.towergame.gobject.GCreature;
import vroth.towergame.gobject.GObjectFactory;
import vroth.towergame.gutil.GDatabase;

public class GRespawn {
	Array<GCreature> gameCreatures = null;
	GObjectFactory factory = null;
	Random random = null;
	
	public GRespawn(Array<GCreature> gameCreatures) {
		this.gameCreatures = gameCreatures;
		this.factory = GObjectFactory.getInstance(null);
		this.random = GDatabase.getInstance().getRandom();
	}
	
	private void respawnWalking(Vector2 position) {
		int rand = random.nextInt(GConfig.WALKING_MONSTERS.length);
		GCreature creature;
		creature = factory.newCreature("enemies/" + GConfig.WALKING_MONSTERS[rand] + "/", position, GConfig.HP_WALKING_MONSTERS[rand], true, false);
		gameCreatures.add(creature);
	}
	
	private void respawnFlying(Vector2 position) {
		int rand = random.nextInt(GConfig.FLYING_MONSTERS.length);
		GCreature creature;
		creature = factory.newCreature("enemies/" + GConfig.FLYING_MONSTERS[rand] + "/", position, GConfig.HP_FLYING_MONSTERS[rand], false, true);
		creature.setFly();
		gameCreatures.add(creature);
	}
	
	//private void respawnSpecial(Vector2 position) {
		//the creature can't move, so i cant add on the game :/
	//}
	
	private boolean isPlaceable(Vector2 monsterPosition, GMap gameMap) {
		if(monsterPosition.y > 0 && monsterPosition.x > 0)
		if(gameMap.getHeight() > monsterPosition.y) {
			if(gameMap.getMapLine((int) monsterPosition.y).getStaticObject((int) monsterPosition.x) == null && gameMap.getMapLine((int) monsterPosition.y).getTileForeground((int) monsterPosition.x) == null) {
				if(gameMap.getHeight() > monsterPosition.y+1) {
					if(gameMap.getMapLine((int) monsterPosition.y + 1).getStaticObject((int) monsterPosition.x) == null && gameMap.getMapLine((int) monsterPosition.y + 1).getTileForeground((int) monsterPosition.x) == null)
						return true;
				}
				else
					return true;
			}
		}
		else
			return true;
		return false;
	}
	
	public void respawnSomething(Vector2 playerPosition, GMap gameMap) {
		Vector2[] visibleRange = gameMap.rangeVisible(playerPosition);
		boolean respawn = false;
		for(int i = 0; i < GConfig.TRIES_RESPAWN; i++) {
			boolean up = GDatabase.getInstance().getRandom().nextBoolean();
			boolean right = GDatabase.getInstance().getRandom().nextBoolean();
			float x, y;
			if(up)
				y = visibleRange[1].y + GDatabase.getInstance().getRandom().nextInt(4);
			else
				y = visibleRange[0].y - GDatabase.getInstance().getRandom().nextInt(4);
			if(right)
				x = visibleRange[1].x + GDatabase.getInstance().getRandom().nextInt(4);
			else
				x = visibleRange[0].x - GDatabase.getInstance().getRandom().nextInt(4);
			
			if(isPlaceable(new Vector2(x, y), gameMap)) {
				if(GDatabase.getInstance().getRandom().nextBoolean())
					respawnWalking(new Vector2(x*70, y*70));
				else
					respawnFlying(new Vector2(x*70, y*70));
				respawn = true;
				break;
			}
		}
		if(!respawn)
			System.err.println("Couldn't respawn");
	}
	
	public void respawnNearby(Vector2 playerPosition, GMap gameMap) {
		respawnFlying(new Vector2(playerPosition.x + 10, playerPosition.y + 50));
	}
}
