package Terrain;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;

import Shader.ComputeBuffer;

public class MarchingCubesComputeBuffer extends ComputeBuffer {
	public static final String FILE = "src/Shader/marchingCubeComputeShader.txt";
	public MarchingCubesComputeBuffer(){
		super(FILE);
	}
	public float[] useProgram(int x_size, int y_size, int z_size, float[] terrainMap, float surface, float size){
		
		this.group_x_size = (int)Math.ceil(x_size/10.0);
		this.group_y_size = (int)Math.ceil(y_size/10.0);
		this.group_z_size = (int)Math.ceil(z_size/10.0);
		
		
		int ssbo = generateSSBO(x_size*y_size*z_size*48,0);	
		generateSSBO(1,terrainMap);	
		generateSSBO(2,flatten(MarchingCubeTable.triTable));		
	
		
		GL20.glUseProgram(computeProgram);//start the shader program
		
		int x_sizeLocation = this.getUniformLocation("x_size");
		loadFloat(x_sizeLocation, x_size);
		int y_sizeLocation = this.getUniformLocation("y_size");
		loadFloat(y_sizeLocation, y_size);
		int size_Location = this.getUniformLocation("size");
		loadFloat(size_Location, size);
		int surface_Location = this.getUniformLocation("surfaceLevel");
		loadFloat(surface_Location, surface);
		
		GL43.glDispatchCompute(group_x_size, group_y_size, group_z_size);
		GL43.glMemoryBarrier(GL43.GL_BUFFER_UPDATE_BARRIER_BIT );

		float[] data = this.getDataFromSSBO(ssbo, x_size*y_size*z_size*48);
		
		List<Float> tempList = new ArrayList<Float>();
		for(int i = 0; i<data.length; i++){
			if(data[i] > -1){
				tempList.add(data[i]);
			}
		}
		float[] value = new float[tempList.size()];
		int counter = 0;
		for(Float i : tempList){
			value[counter++] = i;
		}
		
		GL20.glUseProgram(0);
		
		return value;
	}
	public float[] flatten(int[][] triTable){
		float[] converted = new float[256*16];
		for(int y = 0; y<256; y++){
			for(int x = 0; x < 16; x++){
				converted[16*y+x] = triTable[y][x];
			}
		}
		return converted;
	}
	
}

