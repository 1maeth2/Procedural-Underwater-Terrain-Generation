package Entities.Boids;

import java.util.ArrayList;
import java.util.List;

public class BoidManager {
	private List<Boids> flock = new ArrayList<Boids>();
	public BoidManager(List<Boids> f){
		this.flock = f;
	}
	
	public void run(){
		for(Boids boid : flock){
			boid.run(flock);
		}
	}
}
