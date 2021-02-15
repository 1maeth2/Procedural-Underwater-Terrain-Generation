package Entities.Boids;

import java.util.Arrays;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;

import Maths.Vector3f;
import Shader.ComputeBuffer;

public class BoidsComputeBuffer extends ComputeBuffer{
	private static final String FILE = "src/Shader/boidsComputeShader.txt";
	public BoidsComputeBuffer() {
		super(FILE);
	}

	public float[][] useProgram(int size, Vector3f[] direction, Vector3f[] position, float avoidance_radius, float perception_radius){
		
		this.group_x_size = size;
		this.group_y_size = 1;
		this.group_z_size = 1;
				
		int alignmentSSBO = generateSSBO(position.length*3,0);
		int cohesionSSBO = generateSSBO(position.length*3,1);	
		int seperationSSBO = generateSSBO(position.length*3,2);	

		generateSSBO(3,this.vec3toFloat(direction));
		generateSSBO(4,this.vec3toFloat(position));
		
		int ar = getUniformLocation("avoidance_radius");
		int pr = getUniformLocation("perception_radius");
		
		this.loadFloat(ar, avoidance_radius);
		this.loadFloat(pr, perception_radius);

//		
//		int p = getUniformLocation("positions");
//		int d = getUniformLocation("directions");
//		
//		loadFloatArray(p,this.vec3toFloat(position));
//		loadFloatArray(d,this.vec3toFloat(direction));
//	
		
		GL20.glUseProgram(computeProgram);//start the shader program
		
		
		GL43.glDispatchCompute(group_x_size,group_y_size,group_z_size);
		GL43.glMemoryBarrier(GL43.GL_BUFFER_UPDATE_BARRIER_BIT );

		float[][] data = new float[3][0];
		
		data[0] = getDataFromSSBO(alignmentSSBO, position.length*3);
		data[1] = getDataFromSSBO(cohesionSSBO, position.length*3);
		data[2] = getDataFromSSBO(seperationSSBO, position.length*3);

		System.out.println(Arrays.deepToString(data));
		GL20.glUseProgram(0);
		
		return data;
	}
}
