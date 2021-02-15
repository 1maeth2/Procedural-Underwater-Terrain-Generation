package Render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import Models.RawModel;
import Textures.Texture;

public class Loader {
	//Binding VBO to a ARRAY_BUFFER allows for easy access and editing
	//Binding a VBO puts it into global state allowing access by vao automatically
	//BindBuffer is not apart of VAO states and therefore have to be added while Element Arrays are apart of VAO state
	//Each VAO has one special slot for index buffers(Element Arrays)
	
	private List<Integer> vaos = new ArrayList<Integer>();// List of VAOs for organisation
	private List<Integer> vbos = new ArrayList<Integer>();// List of VBOs for organisation
	private List<Integer> textures = new ArrayList<Integer>();// List of textures for organisation
	
	public RawModel loadToVao(float[] positions, float[] textureCoords, int[] indices){
		int vaoID = createVAO(); // creates a VAO
		bindIndicesBuffer(indices);//stores indices buffer into the VAO
		storeDataInAttributeList(0,3,positions); //Stores VBO in VAO
		if(textureCoords != null){
			storeDataInAttributeList(1,2,textureCoords); //Stores VBO in VAO
		}
		unbindVAO(); // unbinds VAO
		return new RawModel(vaoID, indices.length);
	}
	
	public int loadTexture(String fileName){//Loads texture into a OpenGL readable texture file
		Texture texture = new Texture("res/"+fileName+".png");
		textures.add(texture.getTextureID());
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		return texture.getTextureID();
	}
	public void cleanUp(){
		for(int vao: vaos){
			GL30.glDeleteVertexArrays(vao); //deletes all vaos saved in video memory, no longer references attached vbos(does not delete the vbos)
		}
		for(int vbo: vbos){
			GL15.glDeleteBuffers(vbo); //deletes all vbos saved in video memory
		}
		for(int texture: textures){
			GL11.glDeleteTextures(texture);//deletes all textures saved in video memory
		}
	}
	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays(); // creates VAO and stores it in OpenGL memory
		vaos.add(vaoID); // adds VAO to list for organization
		GL30.glBindVertexArray(vaoID); // finds OpenGl object vaoID and sets it as a global state, VAO can be edited whenever glvertex is called
		return vaoID;
	}
	
	public void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();// creates empty VBO
		vbos.add(vboID);//adds VBO to list for organization
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);//Binds vboID to element array. Element array is apart of VAO state and therefore is automatically saved
		IntBuffer buffer = storeDataInIntBuffer(indices); //converts int array into int buffer
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); //stores int buffer into vbo/index array
		//Do not unbind the element array buffer as it completely removes the index buffer
	}
	
	public IntBuffer storeDataInIntBuffer(int[] data){//converts int array into int buffer
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
		int vboID = GL15.glGenBuffers();// creates empty VBO as an object and stores it in OpenGl memory
		vbos.add(vboID);// adds VBO into list for organization
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID); // binds object vboID to ArrayBuffer, sets Global state to vboID, allowing you to edit VBO
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); //use ArrayBuffer which has vboID bound to it and adds buffer data to the vboID
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0); //retrieves Global State vboID and determines which index to store in the VAO, how many components per attribute, data type
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); //unbind vboID from GL_ARRAY_BUFFER
	}
	private void unbindVAO(){
		GL30.glBindVertexArray(0);// clears global VAO state, no more object is binded, VAO cant be edited
	}
	private FloatBuffer storeDataInFloatBuffer(float[] data){// converts float array into FloatBuffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
