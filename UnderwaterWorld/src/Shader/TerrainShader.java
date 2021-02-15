package Shader;

import java.util.List;

import Entities.Camera;
import Entities.Light;
import Maths.Matrix4f;
import Maths.Vector3f;
import Maths.Maths;


public class TerrainShader extends ShaderProgram {

	public static final String VERTEX_FILE = "src/Shader/terrainVertexShader.txt";
	public static final String FRAGEMENT_FILE = "src/Shader/terrainFragementShader.txt";
	public static final String GEOMETRY_FILE = "src/Shader/terrainGeometryShader.txt";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPosition;
	private int[] location_lightColor;
	private int[] location_attenuation;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColor;
	

	public TerrainShader() {
		super(VERTEX_FILE, FRAGEMENT_FILE, GEOMETRY_FILE);//Inputs file directory
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");//binds VAO attribute vertex coords to position variable in vertexShader program
	}

	@Override
	protected void getAllUniformLocation() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");//gets the location for the uniform variable transformationMatrix
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");//gets the location for the uniform variable projectionMatrix
		location_viewMatrix = super.getUniformLocation("viewMatrix");//gets the location for the uniform variable viewMatrix
		
		location_shineDamper = super.getUniformLocation("shineDamper");//gets the location for the uniform variable viewMatrix
		location_reflectivity = super.getUniformLocation("reflectivity");//gets the location for the uniform variable viewMatrix
		location_skyColor = super.getUniformLocation("skyColor");//gets the location for the uniform variable viewMatrix
		
		location_lightPosition = new int[2];
		location_lightColor = new int[2];
		location_attenuation = new int[2];

		for(int i = 0; i<2; i++){
			location_lightPosition[i] = super.getUniformLocation("lightPosition["+i+"]");//gets the location for the uniform variable viewMatrix
			location_lightColor[i] = super.getUniformLocation("lightColor["+i+"]");//gets the location for the uniform variable viewMatrix
			location_attenuation[i] = super.getUniformLocation("attenuation["+i+"]");//gets the location for the uniform variable viewMatrix
		}
	}
	public void loadSkyColor(Vector3f color){
		super.loadVector3f(location_skyColor, color);
	}
	public void loadShine(float shineDamper, float reflectivity){
		super.loadFloat(location_shineDamper, shineDamper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	public void loadLights(List<Light> l){//set the uniform variable lightPosition in shader program to new position
		for(int i = 0 ;i < 2; i++){
			if(i<l.size()){
				super.loadVector3f(location_lightPosition[i], l.get(i).getPosition());
				super.loadVector3f(location_lightColor[i], l.get(i).getColor());
				super.loadVector3f(location_attenuation[i], l.get(i).getAttenuation());
			}else{
				super.loadVector3f(location_lightPosition[i], new Vector3f(0,0,0));
				super.loadVector3f(location_lightColor[i], new Vector3f(0,0,0));
				super.loadVector3f(location_attenuation[i], new Vector3f(1,0,0));
			}
		}
	}
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);//set the uniform variable transformationMatrix to new matrix
	}
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);//set the uniform variable projectionMatrix to new matrix
	}
	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, matrix);//set the uniform variable transformationMatrix to new matrix
	}

}
