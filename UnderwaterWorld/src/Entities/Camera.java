package Entities;

import org.lwjgl.glfw.GLFW;

import Engine.Input;
import Maths.Vector3f;

public class Camera {
	private Vector3f position = new Vector3f(0,10,0);
	private Vector3f rotation = new Vector3f(0,0,0);

	Player player;
	
	private double lastMousePosX = 600 , newMousePosX;
	private double lastMousePosY = 500 , newMousePosY;
	
	private float sideAngle = 0, topAngle = 0,distance = 30f; 

	public Vector3f getPosition() {
		return position;
	}
	public Vector3f getRotation() {
		return rotation;
	}
	public void move(Entity entity){
		newMousePosX = Input.getMouseX();
		newMousePosY = Input.getMouseY();
		float dx = (float)(newMousePosX-lastMousePosX)*0.07f;
		float dy = (float)(newMousePosY-lastMousePosY)*0.07f;
		if(Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_2)){
			distance += dy;
			if(distance < 18){
				distance = 18f;
			}
		}else if(Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1)){
			sideAngle -= dy;
			topAngle += dx;
		}else{
			sideAngle = 0;
			topAngle = 0;
		}
		float horizontalDistance = (float)(distance*Math.cos(Math.toRadians(sideAngle+entity.getRotX()*0.8f)));
		float verticalDistance = (float)(distance*Math.sin(Math.toRadians(sideAngle+entity.getRotX()*0.8f)));
		
		float disx = (float)(horizontalDistance*Math.sin(Math.toRadians(entity.getRotY()-topAngle)));
		float disz = (float)(horizontalDistance*Math.cos(Math.toRadians(entity.getRotY()-topAngle)));

		this.position = new Vector3f(entity.getPosition().x+disx, entity.getPosition().y-verticalDistance+10, entity.getPosition().z+disz);
		this.rotation = new Vector3f(-sideAngle-entity.getRotX()*0.8f, topAngle-entity.getRotY(), 0);
		
		lastMousePosX = newMousePosX;
		lastMousePosY = newMousePosY;
	}
	public void move(){
		newMousePosX = Input.getMouseX();
		newMousePosY = Input.getMouseY();
		this.rotation.y += (float)(newMousePosX-lastMousePosX)*0.07;
		this.rotation.x += (float)(newMousePosY-lastMousePosY)*0.07;
		lastMousePosX = newMousePosX;
		lastMousePosY = newMousePosY;
		if(this.rotation.y<0){
			this.rotation.y = 360;
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_W)){
			this.position.z -= Math.cos(Math.toRadians(this.rotation.y%360));
			this.position.x += Math.sin(Math.toRadians(this.rotation.y%360));
		}else if(Input.isKeyDown(GLFW.GLFW_KEY_S)){
			this.position.z += Math.cos(Math.toRadians(this.rotation.y%360));
			this.position.x -= Math.sin(Math.toRadians(this.rotation.y%360));
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_D)){
			this.position.z += Math.sin(Math.toRadians(this.rotation.y%360));
			this.position.x += Math.cos(Math.toRadians(this.rotation.y%360));
		}else if(Input.isKeyDown(GLFW.GLFW_KEY_A)){
			this.position.z -= Math.sin(Math.toRadians(this.rotation.y%360));
			this.position.x -= Math.cos(Math.toRadians(this.rotation.y%360));
		}
		if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)){
			this.position.y -= 1;
		}else if(Input.isKeyDown(GLFW.GLFW_KEY_SPACE)){
			this.position.y += 1;
		}
	}
	
	
}
