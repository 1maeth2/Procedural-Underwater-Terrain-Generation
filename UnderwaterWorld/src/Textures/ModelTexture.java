package Textures;

public class ModelTexture {
	
	private int textureID;
	
	private float shineDamper = 1f;
	private float reflectivity = 1f;
	
	public ModelTexture(int id){
		this.textureID = id;
	}
	
	public float getShineDamper() {
		return shineDamper;
	}


	public float getReflectivity() {
		return reflectivity;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public int getID(){
		return textureID;
	}

}
