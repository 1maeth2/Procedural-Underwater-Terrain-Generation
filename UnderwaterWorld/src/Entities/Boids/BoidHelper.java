package Entities.Boids;

import java.util.Random;

import Maths.Vector3f;

public class BoidHelper {
	public static Vector3f[] directions;
	static int numViewDirections = 300;
	public static void init(){
		directions = new Vector3f[BoidHelper.numViewDirections];
		
        float goldenRatio = (float) ((1 + Math.sqrt (5)) / 2);
        float angleIncrement = (float) (Math.PI * 2 * goldenRatio);

        for (int i = 0; i < numViewDirections; i++) {
            float t = (float) i / numViewDirections;
            float inclination = (float) Math.acos (1 - 2 * t);
            float azimuth = angleIncrement * i;

            float x = (float) (Math.sin (inclination) * Math.cos (azimuth));
            float y = (float) (Math.sin (inclination) * Math.sin (azimuth));
            float z = (float) Math.cos (inclination);
            directions[i] = new Vector3f (x, y, z);
        }
	}
	public static float distance(Vector3f p1, Vector3f p2){
		return (float) Math.sqrt(Math.pow(p2.x-p1.x, 2)+Math.pow(p2.y-p1.y, 2)+Math.pow(p2.z-p1.z, 2));
	}
	public static float getRandom(Random rand){
		if(rand.nextFloat() > 0.5){
			return 1;
		}else{
			return -1;
		}
	}
}
