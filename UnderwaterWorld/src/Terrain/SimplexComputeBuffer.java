package Terrain;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;

import Shader.ComputeBuffer;

public class SimplexComputeBuffer extends ComputeBuffer{
	public static final String FILE = "src/Shader/noiseComputeShader.txt";
	public SimplexComputeBuffer(){
		super(FILE);
	}
	public float[][] useProgram(int x_size, int y_size, int z_size, float x_offset, float z_offset){
		
		this.group_x_size = (int)Math.ceil(x_size/10.0);
		this.group_y_size = (int)Math.ceil(y_size/10.0);
		this.group_z_size = (int)Math.ceil(z_size/10.0);
				
		int ssbo3d = generateSSBO(x_size*y_size*z_size,0);
		int ssbo2d = generateSSBO(x_size*z_size,1);
				
		GL20.glUseProgram(computeProgram);//start the shader program
		int xsizeLocation = this.getUniformLocation("xsize");
		loadFloat(xsizeLocation, x_size);
		int ysizeLocation = this.getUniformLocation("ysize");
		loadFloat(ysizeLocation, y_size);
		int x_offsetLocation = this.getUniformLocation("x_offset");
		loadFloat(x_offsetLocation, x_offset);
		int z_offsetLocation = this.getUniformLocation("z_offset");
		loadFloat(z_offsetLocation, z_offset);
		GL43.glDispatchCompute(group_x_size, group_y_size, group_z_size);
		GL43.glMemoryBarrier(GL43.GL_BUFFER_UPDATE_BARRIER_BIT );

		float[] noise3d = this.getDataFromSSBO(ssbo3d, x_size*y_size*z_size);
		
		float[] noise2d = this.getDataFromSSBO(ssbo2d, x_size*z_size);

		
		float[][] values = {noise3d, noise2d};
		
		GL20.glUseProgram(0);
		
		return values;
	}
	
}
