package Entities;


import org.lwjgl.glfw.GLFW;

import Engine.Input;
import Maths.Vector3f;
import Models.TexturedModel;

public class Player extends Entity {
	
	public float xspeed = 0,zspeed = 0, yspeed = 0; 
	public Vector3f mousePos;
	public float yrotation = 0, zrotation = 0;
	public float maxYRotation = 75f;
	
	private double lastMousePosX = 600 , newMousePosX;
	private double lastMousePosY = 500 , newMousePosY;
	private float speed = 3f;
	
	
	private Vector3f tip = new Vector3f(0,0,0);
	private Vector3f light_point = new Vector3f(0,0,0);
	
	private boolean collision = true;
	boolean collided = false;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	public void move(Light light, Entity cube1){
		checkInput();
		object_x = (position.x/(16*16));
		object_z = (position.z/(16*16));
		object_x = (float) Math.floor(object_x);
		object_z = (float) Math.floor(object_z);
//		if(terrain != null){
//			System.out.println("Current Chunk ="+terrain.getChunkX()+","+terrain.getChunkZ());
//		}
		translate(tip,position,new Vector3f(9,9,0));
		
		if(collision){
			for(int i = 9; i>0; i--){
				Vector3f point = new Vector3f(0,0,0);
				translate(point,position,new Vector3f(i,i,0));
				collided = checkCollision(point);
				if(collided){
					position.x = position.x - (tip.x-point.x);
					position.y = position.y - (tip.y-point.y);
					position.z = position.z - (tip.z-point.z);
					break;
				}
			}
		}
		if(!collided){
			translate(position,position, new Vector3f(speed,speed,0));
		}
		track(light_point,position,new Vector3f(9,9,4));
		light.setPosition(light_point);
		cube1.setPosition(light_point);
	}
	
	public boolean checkCollision(Vector3f position){
		if(terrain != null){
			for(int i = 0; i<terrain.getVertices().length; i+=9){
				Vector3f vertex1 = new Vector3f(terrain.getVertices()[i],terrain.getVertices()[i+1],terrain.getVertices()[i+2]);
				Vector3f vertex2 = new Vector3f(terrain.getVertices()[i+3],terrain.getVertices()[i+4],terrain.getVertices()[i+5]);
				Vector3f vertex3 = new Vector3f(terrain.getVertices()[i+6],terrain.getVertices()[i+7],terrain.getVertices()[i+8]);
				
				if(inTriangle(position, vertex1, vertex2, vertex3)){
					return true;
				}
			}
		}
		return false;
	}
	public UVList getUV(Vector3f a, Vector3f b, Vector3f c){

        //First, calculate the unit normal vector (cross product).
		Vector3f ba = b.subtract(a);
		Vector3f ca = c.subtract(a);
        Vector3f nn = ba.cross(ca);
        float unitVector = (float) Math.sqrt(nn.x*nn.x+nn.y*nn.y+nn.z*nn.z);
        Vector3f n = nn.divide(unitVector);

        //Calculate the signed distance from origin (dot product).
        float d = n.dot(a);

        //Calculate the three possible divisors.
        float div_xy = a.x*(c.y-b.y) + b.x*(a.y-c.y) + c.x*(b.y-a.y);
        float div_xz = a.x*(c.z-b.z) + b.x*(a.z-c.z) + c.x*(b.z-a.z);
        float div_yz = a.y*(c.z-b.z) + b.y*(a.z-c.z) + c.y*(b.z-a.z);
        float abs_xy = Math.abs(div_xy);
        float abs_xz = Math.abs(div_xz);
        float abs_yz = Math.abs(div_yz);
        Vector3f u,v;
		float u0,v0;
        if(abs_xy >= abs_xz && abs_xy >= abs_yz){
            //d_xy has the largest absolute value; using xy plane
            u = new Vector3f((a.y-c.y)/div_xy, (c.x-a.x)/div_xy, 0);
            v = new Vector3f((b.y-a.y)/div_xy, (a.x-b.x)/div_xy, 0);
            u0 = (a.x*c.y - a.y*c.x)/div_xy;
            v0 = (a.y*b.x - a.x*b.y)/div_xy;
        }else if( abs_xz >= abs_xy && abs_xz >= abs_yz){
            //d_xz has the largest absolute value; using xz plane
            u = new Vector3f((a.z-c.z)/div_xz, 0, (c.x-a.x)/div_xz);
            v = new Vector3f((b.z-a.z)/div_xz, 0, (a.x-b.x)/div_xz);
            u0 = (a.x*c.z - a.z*c.x)/div_xz;
            v0 = (a.z*b.x - a.x*b.z)/div_xz;
        }else{
            //d_yz has the largest absolute value; using yz plane
            u = new Vector3f(0, (a.z-c.z)/div_yz, (c.y-a.y)/div_yz);
            v = new Vector3f(0, (b.z-a.z)/div_yz, (a.y-b.y)/div_yz);
            u0 = (a.y*c.z - a.z*c.y)/div_yz;
            v0 = (a.z*b.y - a.y*b.z)/div_yz;
        }
        return new UVList(u0,v0,u,v,d,n);
	}
	public class UVList{
		Vector3f u,v,n;
		float u0, v0,d;
		public UVList(float u0, float v0, Vector3f u, Vector3f v,float d,Vector3f n){
			this.u = u;
			this.v = v;
			this.u0 = u0;
			this.v0 = v0;
			this.d = d;
			this.n = n;
		}
		public Vector3f getN(){
			return n;
		}
		public Vector3f getU(){
			return u;
		}
		public Vector3f getV(){
			return v;
		}
		public float getU0(){
			return u0;
		}
		public float getV0(){
			return v0;
		}
		public float getD(){
			return d;
		}
	}
	public void translate(Vector3f position, Vector3f offset, Vector3f speed){
		float horizontalDistance = speed.y*(float)(Math.cos(Math.toRadians(rotX)));
		float verticleDistance = speed.x*(float)(Math.sin(Math.toRadians(rotX)));

		if(Input.isKeyDown(GLFW.GLFW_KEY_W)){
			position.x = (float)(offset.x+(horizontalDistance*Math.sin(Math.toRadians(-rotY))));
			position.z = (float)(offset.z-(horizontalDistance*Math.cos(Math.toRadians(-rotY))));
			position.y = (float)(offset.y+verticleDistance);
		}else if(Input.isKeyDown(GLFW.GLFW_KEY_S)){
			position.x = (float)(offset.x-(horizontalDistance*Math.sin(Math.toRadians(-rotY))));
			position.z = (float)(offset.z+(horizontalDistance*Math.cos(Math.toRadians(-rotY))));
			position.y = (float)(offset.y-verticleDistance);
		}
		
	}
	public void track(Vector3f position, Vector3f offset, Vector3f speed){
		float horizontalDistance = speed.y*(float)(Math.cos(Math.toRadians(rotX)));
		float verticleDistance = speed.x*(float)(Math.sin(Math.toRadians(rotX)));

		position.x = (float)(offset.x+(horizontalDistance*Math.sin(Math.toRadians(-rotY))));
		position.z = (float)(offset.z-(horizontalDistance*Math.cos(Math.toRadians(-rotY))));
		position.y = (float)(offset.y+verticleDistance);
		
		
	}
	public boolean inTriangle(Vector3f p, Vector3f v1, Vector3f v2, Vector3f v3){
		float ellipse = 1.5f;
		
		UVList uv = getUV(v1,v2,v3);
		
		
		if(Math.abs(p.dot(uv.getN())-uv.getD()) >= ellipse){
			return false;
		}
		
		float u = p.dot(uv.getU())+uv.getU0();
		
		float v = p.dot(uv.getV())+uv.getV0();

		if(u < 0 || u > 1){
			return false;
		}
		

		if(v < 0 || v > 1){
			return false;
		}
		
		if(u+v > 1){
			return false;
		}
		
		return true;
	}
	public void checkInput(){
		newMousePosX = Input.getMouseX();
		newMousePosY = Input.getMouseY();
		
		float dx = (float)(newMousePosX-lastMousePosX)*0.07f;
		float dy = (float)(newMousePosY-lastMousePosY)*0.07f;
		
		if(!Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1) && !Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_2)){

			this.rotY -= dx/2;
			this.rotX -= dy*0.8f;
		
		}
		
		if(Math.abs(rotX) > 50){
			this.rotX = Math.abs(rotX)/rotX*50;
		}
		
		if(this.rotY<0){
			this.rotY = 360;
		}
		
		lastMousePosX = newMousePosX;
		lastMousePosY = newMousePosY;		
		
	}

}
