package vroth.towergame.gutil;

import java.util.HashMap;

import com.badlogic.gdx.audio.Music;

//TODO: implement
public class GSound {
	private static GSound instance = null; 
	
	private HashMap<SOUND, Music> musicData = new HashMap<SOUND, Music>();
	public enum SOUND {GAME_START, HIT, DAMAGE, MONSTER_NEARBY, MONSTER_RESPAWN, DESTROY_TILE, PLACE_TILE, GET_RESOURCE, GET_COIN, CREDITS};
	
	private GSound() {
		
	}

	public static GSound getInstance() {
		if(instance == null)
			instance = new GSound();
		return instance;
	}
	
	public void playSound(SOUND sound) {
		Music wantedMusic = musicData.get(sound); 
		if(wantedMusic != null && wantedMusic.isPlaying() == false) {
			wantedMusic.play();
		}
	}
}
