package Render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Maths.Matrix4f;
import Maths.Vector3f;
import Models.TexturedModel;
import Shader.EntityShader;
import Shader.TerrainShader;
import Terrain.Terrain;

public class MasterRenderer {
	private EntityShader entityShader;
	private EntityRenderer entityRender;
	
	private TerrainShader terrainShader;
	private TerrainRenderer terrainRender;
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 3000;
	
	private Matrix4f projectionMatrix;
	
	private Vector3f skyColor = new Vector3f(0.1f,0.1f,0.5f);
		
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	//private List<Terrain> terrain = new ArrayList<Terrain>();
	
	public MasterRenderer(){
		createProjectionMatrix();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		entityShader = new EntityShader();
		entityRender = new EntityRenderer(entityShader, projectionMatrix);
		terrainShader = new TerrainShader();
		terrainRender = new TerrainRenderer(terrainShader, projectionMatrix);
	}
	public void render(List<Light> lights, Camera camera, List<Terrain> terrain){
		prepare();
		entityShader.start();
		entityShader.loadLights(lights);
		entityShader.loadViewMatrix(camera);
		entityShader.loadSkyColor(skyColor);
		entityRender.render(entities);
		entityShader.stop();
		entities.clear();
		terrainShader.start();
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadSkyColor(skyColor);
		terrainRender.render(terrain);
		terrainShader.stop();
	}
	
	public void sortEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch != null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	private void createProjectionMatrix(){
		projectionMatrix = Matrix4f.perspective(FOV, 920/900, NEAR_PLANE, FAR_PLANE); //Creates the projection matrix using matrix math
	}
	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(skyColor.x, skyColor.y, skyColor.z,0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void cleanUp(){
		entityShader.cleanUp();
		terrainShader.cleanUp();
	}
}
