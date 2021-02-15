package Render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import Entities.Entity;
import Maths.Maths;
import Maths.Matrix4f;
import Models.RawModel;
import Models.TexturedModel;
import Shader.EntityShader;

public class EntityRenderer {
	
	private Matrix4f projectionMatrix;
	
	EntityShader shader;
	
	public EntityRenderer(EntityShader shader, Matrix4f projection){
		this.shader = shader;
		projectionMatrix = projection;//create projection matrix
		shader.start(); //Start shader program
		shader.loadProjectionMatrix(projectionMatrix); //add projetionMatrix into vertexShader
		shader.stop();//Stops shader program
	}

	public void render(Map<TexturedModel, List<Entity>> entities){
		for(TexturedModel model : entities.keySet()){
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity : batch){
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT,0);
			}
			unbindTexturedModel();
		}
	}
	public void prepareTexturedModel(TexturedModel texturedModel){
		RawModel model = texturedModel.getRawModel(); //Gets model from entity
		GL30.glBindVertexArray(model.getVaoID()); //Binds model VAO object which is currently stored in OpenGL allowing you to access and edit, sets as global vertex state
		GL20.glEnableVertexAttribArray(0); //enables index at which the vertex vbo is stored in the vao to be used
		GL20.glEnableVertexAttribArray(1); //enables index at which the texture coord vbo is stored in the vao to be used
				
		GL13.glActiveTexture(GL13.GL_TEXTURE0);//Get texture from existing bank in the VAO (sampleTexture)
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getID());//Bind Texture to Texture_2D
	}
	public void unbindTexturedModel(){
		GL20.glDisableVertexAttribArray(0); //disables the index at which the vbo is stored in the vao
		GL20.glDisableVertexAttribArray(1); //disables the index at which the vbo is stored in the vao
		GL30.glBindVertexArray(0);// resets global state, no longer have access to vao
	}
	public void prepareInstance(Entity entity){
		Matrix4f transformationMatrix = Maths.createTransformationalMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());//Load up the transformation matrix
		shader.loadTransformationMatrix(transformationMatrix);//Load the transformation matrix into the vertexShader
	}
	

}
