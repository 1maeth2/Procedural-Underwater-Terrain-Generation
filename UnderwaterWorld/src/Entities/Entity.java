package Entities;

import Maths.Vector3f;
import Models.TexturedModel;
import Terrain.Terrain;

public class Entity {
	private TexturedModel model;
	protected Vector3f position;
	protected float rotX,rotY,rotZ;
	private float scale;
	protected Terrain terrain;
	float object_x,object_z;

	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}
	public void run(){}
	public void increasePosition(float dx, float dy, float dz){
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	public void increaseRotation(float rx, float ry, float rz){
		this.rotX += rx;
		this.rotY += ry;
		this.rotZ += rz;
	}
	public TexturedModel getModel() {
		return model;
	}
	public void setModel(TexturedModel model) {
		this.model = model;
	}
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public float getRotX() {
		return rotX;
	}
	public void setRotX(float rotX) {
		this.rotX = rotX;
	}
	public float getRotY() {
		return rotY;
	}
	public void setRotY(float rotY) {
		this.rotY = rotY;
	}
	public float getRotZ() {
		return rotZ;
	}
	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	public void setTerrain(Terrain t){
		this.terrain = t;
	}
	public Terrain getTerrain(){
		return terrain;
	}
	public synchronized float getObject_x() {
		return object_x;
	}
	public synchronized void setObject_x(float object_x) {
		this.object_x = object_x;
	}
	public synchronized float getObject_z() {
		return object_z;
	}
	public void setObject_z(float object_z) {
		this.object_z = object_z;
	}
	
}
