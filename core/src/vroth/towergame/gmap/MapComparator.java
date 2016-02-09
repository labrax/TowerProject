package vroth.towergame.gmap;

import java.util.Comparator;

import com.badlogic.gdx.math.Vector2;

public class MapComparator implements Comparator<Vector2>{
	public static Vector2 refVector = new Vector2(0, 0); 
	
	public int compare(Vector2 a, Vector2 b) {
		return (int) (a.dst2(refVector) - b.dst2(refVector));
	}

}
