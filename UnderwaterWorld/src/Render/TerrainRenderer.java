package Render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import Maths.Maths;
import Maths.Matrix4f;
import Maths.Vector3f;
import Models.RawModel;
import Shader.TerrainShader;
import Terrain.Terrain;

public class TerrainRenderer {
	
	private Matrix4f projectionMatrix;
	
	TerrainShader shader;
	
	public TerrainRenderer(TerrainShader terrainShader, Matrix4f projection){
		this.shader = terrainShader;
		projectionMatrix = projection;//create projection matrix
		terrainShader.start(); //Start shader program
		terrainShader.loadProjectionMatrix(projectionMatrix); //add projetionMatrix into vertexShader
		terrainShader.stop();//Stops shader program
	}

	public void render(List<Terrain> t){
		if(t!=null){
			for(int i = 0; i<t.size(); i++){
				Terrain terrain = t.get(i);
				if(terrain.isDataLoaded()){
					if(terrain.getRawModel() == null){
						terrain.loadModel();
					}
					prepareTexturedModel(terrain.getRawModel());
					prepareInstance(terrain);
					GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, terrain.getRawModel().getVertexCount());
					unbindTexturedModel();
				}
			}
		}
	}
	public void prepareTexturedModel(RawModel model){
		GL30.glBindVertexArray(model.getVaoID()); //Binds model VAO object which is currently stored in OpenGL allowing you to access and edit, sets as global vertex state
		GL20.glEnableVertexAttribArray(0); //enables index at which the vertex vbo is stored in the vao to be used
	}
	public void unbindTexturedModel(){
		GL20.glDisableVertexAttribArray(0); //disables the index at which the vbo is stored in the vao
		GL30.glBindVertexArray(0);// resets global state, no longer have access to vao
	}
	public void prepareInstance(Terrain terrain){
		Matrix4f transformationMatrix = Maths.createTransformationalMatrix(new Vector3f(0,0,0),0,0,0,1);//Load up the transformation matrix
		shader.loadTransformationMatrix(transformationMatrix);//Load the transformation matrix into the vertexShader
	}
	

}
