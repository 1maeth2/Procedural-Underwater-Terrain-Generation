package Shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;

import Maths.Vector3f;

public abstract class ComputeBuffer {
	public int computeShader, computeProgram, group_x_size, group_y_size,group_z_size;
;
	public ComputeBuffer(String FILE){
		computeProgram = GL20.glCreateProgram();
		computeShader = loadShader(FILE, GL43.GL_COMPUTE_SHADER);
		GL20.glAttachShader(computeProgram, computeShader);
		GL20.glLinkProgram(computeProgram);
		GL20.glValidateProgram(computeProgram);
	}
	public float[] getDataFromSSBO(int ssbo, int size){
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssbo);
		float[] data = new float[size];
		GL43.glGetBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, data);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
		return data;
	}

	protected int getUniformLocation(String name){//Gets variable from the vertex shader or fragment shader
		return GL20.glGetUniformLocation(computeProgram, name);
	}
	public void loadFloat(int location, float value){//Changes float variables in shader program
		GL20.glUniform1f(location, value);
	}
	public void loadFloatArray(int location, float[] value){//Changes float variables in shader program
		GL20.glUniform1fv(location, value);
	}
	
	public float[] vec3toFloat(Vector3f[] array){
		int count = 0;
		float[] output = new float[array.length*3];
		for(int i = 0; i<array.length; i++){
			output[count++] = array[i].x;
			output[count++] = array[i].y;
			output[count++] = array[i].z;
		}
		return output;
	}
	public int generateSSBO(int size, int index){
		float[] data = new float[size];
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		int ssbo = GL15.glGenBuffers();// creates empty VBO as an object and stores it in OpenGl memory
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssbo);
		GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, index, ssbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER,buffer,GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
		
		return ssbo;
	}
	public int generateSSBO(int index, float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		int ssbo = GL15.glGenBuffers();// creates empty VBO as an object and stores it in OpenGl memory
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssbo);
		GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, index, ssbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER,buffer,GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
		
		return ssbo;
	}
	private static int loadShader(String file, int type){
		StringBuilder shaderSource = new StringBuilder();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null){
				shaderSource.append(line).append("\n");
			}
			reader.close();
		}catch (IOException e){
			System.err.println("Could not read file");
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);//creates a shader object
		GL20.glShaderSource(shaderID, shaderSource);//inputs shader code into shader object
		GL20.glCompileShader(shaderID);//Compiles shader code into shader object
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE){//Checks and prints any compilation errors
			System.out.println(GL20.glGetShaderInfoLog(shaderID,500));
			System.err.println("Could not compile shader");
			System.exit(-1);
		}
		return shaderID; //Returns shader object
	}
}
