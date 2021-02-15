package Shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import Maths.Matrix4f;
import Maths.Vector3f;

public abstract class ShaderProgram {
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	private int geometryShaderID;
	
	public ShaderProgram(String vertexFile, String fragmentFile, String geomotryFile){
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);//Loads vertexShader program
		fragmentShaderID = loadShader(fragmentFile,GL20.GL_FRAGMENT_SHADER);//Loads fragement shader program
		geometryShaderID = loadShader(geomotryFile,GL32.GL_GEOMETRY_SHADER);//Loads fragement shader program
		programID = GL20.glCreateProgram();// creates shader program
		GL20.glAttachShader(programID, vertexShaderID);// attaches vertex shader to shader program
		GL20.glAttachShader(programID, fragmentShaderID);// attaches fragment shader to shader program
		GL20.glAttachShader(programID, geometryShaderID);// attaches fragment shader to shader program
		bindAttributes();//binds VAO attribute to program variables
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocation();
	}
	
	public void start(){
		GL20.glUseProgram(programID);//start the shader program
	}
	
	public void stop(){
		GL20.glUseProgram(0);//end the shader program
	}
	
	public void cleanUp(){//memory cleanup
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteShader(geometryShaderID);
		GL20.glDeleteProgram(programID);
	}
	protected void bindAttribute(int attribute, String variableName){//binds the attribute that we wanbt to get from the VAO to the shader program
		GL20.glBindAttribLocation(programID, attribute, variableName);//bind the VAO attribute to the variable of the shader program
	}
	protected abstract void bindAttributes();
	
	protected abstract void getAllUniformLocation();
	
	protected int getUniformLocation(String name){//Gets variable from the vertex shader or fragment shader
		return GL20.glGetUniformLocation(programID, name);
	}
	
	public void loadFloat(int location, float value){//Changes float variables in shader program
		GL20.glUniform1f(location, value);
	}
	
	public void loadMatrix(int location, Matrix4f matrix){//Changes matrix variables in shader program
		GL20.glUniformMatrix4fv(location, false, matrix.toBuffer());
	}
	
	public void loadVector3f(int location, Vector3f value){//Changes vector variables in shader program
		GL20.glUniform3f(location, value.x, value.y, value.z);
	}
	
	public void loadBoolean(int location, boolean value){//Changes boolean variables in shader program
		float toLoad = 0;
		if(value){
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
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
