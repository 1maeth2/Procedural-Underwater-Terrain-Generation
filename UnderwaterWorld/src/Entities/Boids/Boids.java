package Entities.Boids;

import java.util.List;
import java.util.Random;

import Entities.Entity;
import Maths.Vector3f;
import Models.TexturedModel;

public class Boids extends Entity {
	Vector3f forwards;
	Vector3f velocity;
	Vector3f acceleration = new Vector3f();
	float speed = 1f;
	float maxforce = 0.1f;
	float seperationMultiplier = 1.3f, alignmentMultiplier = 1f, cohesionMultiplier = 0.8f;
	int perception = 20;
	public Boids(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		Random rand = new Random();
		this.velocity = new Vector3f(rand.nextFloat()*BoidHelper.getRandom(rand),rand.nextFloat()*BoidHelper.getRandom(rand),rand.nextFloat()*BoidHelper.getRandom(rand));
	}
	public void run(float[] a, float[] c, float[] s){
		this.acceleration = new Vector3f(0,0,0);
		Vector3f alignment = this.getAlteredData(new Vector3f(a[0],a[1],a[2])).scale(alignmentMultiplier);
		Vector3f cohesion = this.getAlteredData(new Vector3f(c[0],c[1],c[2])).scale(cohesionMultiplier);
		Vector3f seperation = this.getAlteredData(new Vector3f(s[0],s[1],s[2])).scale(seperationMultiplier);
		this.acceleration = this.acceleration.add(seperation);
		this.acceleration = this.acceleration.add(alignment);
		this.acceleration = this.acceleration.add(cohesion);
		this.position = this.position.add(velocity);
		this.velocity = this.velocity.add(acceleration);
	}
	public void run(List<Boids> flock){
		this.acceleration = new Vector3f(0,0,0);
		flock(flock);
		this.position = this.position.add(velocity);
		this.velocity = this.velocity.add(acceleration);
	}
	public void flock(List<Boids> flock){
		Vector3f alignment = align(flock).scale(alignmentMultiplier);
		Vector3f cohesion = cohesion(flock).scale(cohesionMultiplier);
		Vector3f seperation = seperation(flock).scale(seperationMultiplier);
		this.acceleration = this.acceleration.add(seperation);
		this.acceleration = this.acceleration.add(alignment);
		this.acceleration = this.acceleration.add(cohesion);
	}
	public Vector3f align(List<Boids> flock){
		int total = 0;
		Vector3f steering = new Vector3f();
		for(Boids other : flock){
			if(other != this){
				Vector3f offset = other.position.subtract(this.position);
				float d = offset.length();
				if(d < perception){
					steering = steering.add(other.velocity);
					total++;
				}
			}
		}
		if(total > 0){
			steering = steering.divide(total);
			steering = steering.normalize();
			steering = steering.scale(speed);
			steering = steering.subtract(this.velocity);
			steering.limit(maxforce);
		}
		return steering;
	}
	public Vector3f cohesion(List<Boids> flock){
		int total = 0;
		Vector3f steering = new Vector3f();
		for(Boids other : flock){
			if(other != this){
				Vector3f offset = other.position.subtract(this.position);
				float d = offset.length();
				if(d < perception){
					steering = steering.add(other.position);
					total++;
				}
			}
		}
		if(total > 0){
			steering = steering.divide(total);
			steering = steering.subtract(this.position);
			steering = steering.normalize();
			steering = steering.scale(speed);
			steering = steering.subtract(this.velocity);
			steering.limit(maxforce);
		}
		return steering;
	}
	public Vector3f seperation(List<Boids> flock){
		int total = 0;
		Vector3f steering = new Vector3f();
		for(Boids other : flock){
			if(other != this){
				Vector3f offset = other.position.subtract(this.position);
				float d = offset.length();
				if(d < perception-10){
					Vector3f diff = this.position.subtract(other.position);
					diff = diff.divide(d*d);
					steering = steering.add(diff);
					total++;
				}
			}
		}
		if(total > 0){
			steering = steering.divide(total);
			steering = steering.normalize();
			steering = steering.scale(speed);
			steering = steering.subtract(this.velocity);
			steering.limit(maxforce);
		
		}
		return steering;
	}
	public Vector3f getAlteredData(Vector3f v){
		Vector3f alt = v.normalize().scale(speed).subtract(this.velocity);
		alt.limit(maxforce);
		return alt;
	}
	public Vector3f getVelocity(){
		return this.velocity;
	}
	public Vector3f getAcceleration(){
		return this.acceleration;
	}
}
