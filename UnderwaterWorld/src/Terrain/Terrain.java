package Terrain;

import java.util.ArrayList;
import java.util.List;import java.util.Random;

import Entities.Entity;
import Maths.Vector3f;
import Models.RawModel;
import Models.TexturedModel;
import Render.Loader;
import Render.OBJLoader;
import Textures.ModelTexture;


public class Terrain {
	
	private String[] colors = {"red","purple"};
	private int max_coral_size = 10, min_coral_size = 1;
	private int max_coral_num = 450, min_coral_num = 200;
	
	
	private int width = 16, height = 200, interval = 16;
	
	private float x,y,z = 2f;
	private int x1,z1;
	private float CHUNK_X, CHUNK_Z;
	
	private Loader loader;
	
	private List<Entity> coral = new ArrayList<Entity>();
	private List<Entity> cubes = new ArrayList<Entity>();
	private float[] vertices;
	private int[] indice;
	
	private RawModel model;

//	private double perlin2DScale = 0.01, perlin3DScale = 0.01;//, maskScale = 0.00;
	
	private double perlin3Dratio = 0;// ratio of 3d noise to 2d;
	
	private double amp = 1; //height of mountains
		
	List<Vector3f> verticeArray = new ArrayList<Vector3f>();
	float[][][] terrainMap = new float[width+1][height+1][width+1];
	float[] SimplexMap3D = new float[(width+1)*(height+1)*(width+1)];
	float[] SimplexMap2D = new float[(width+1)*(width+1)];

	private float surfaceLevel = 0.3f;
	
	private boolean isTerrainLoaded = false;
	private boolean isDataLoaded = false;
		
	int seed;
	
//	SimplexComputeBuffer compute = new SimplexComputeBuffer();
//	MarchingCubesComputeBuffer marchingCompute = new MarchingCubesComputeBuffer();

	public Terrain(float x, float z, Loader loader){
		this.loader = loader; 
		this.x1 = (int) (x*width);
		this.z1 = (int) (z*width);
		this.x = x*width*interval;
		this.z = z*width*interval;
		this.CHUNK_X = x;
		this.CHUNK_Z = z;
	}
//	public void loadTerrainMap(){
//		System.out.println("loading");
//		float[][] SimplexMap = compute.useProgram(width+1, height+1, width+1,(float) x1, (float) z1);
//		this.SimplexMap3D = SimplexMap[0];
//		this.SimplexMap2D = SimplexMap[1];
//		isDataLoaded = true;
//	}
	
	public void loadTerrain(){
		Random rand = new Random();
		for(int i = 0; i<rand.nextInt(max_coral_num-min_coral_num+1)+min_coral_num; i++){
			int num = rand.nextInt(verticeArray.size()/3)*3;
			Vector3f pos1 = verticeArray.get(num).scale(interval);
			Vector3f pos2 = verticeArray.get(num+1).scale(interval);
			Vector3f pos3 = verticeArray.get(num+2).scale(interval);
			
			Vector3f pos = pos1.add(pos2).add(pos3).divide(3);
			Vector3f a = pos2.subtract(pos1);
			Vector3f b = pos3.subtract(pos1);
			
			Vector3f normal = a.cross(b);
			Vector3f plane = new Vector3f(0,1,0);
			
			Vector3f axis = plane.cross(normal).normalize();
			
			double angle = Math.acos(plane.dot(normal)/plane.length()/normal.length());
			
			if(Math.toDegrees(angle) < 40){
				Vector3f rotation = toEuler(axis.x,axis.y,axis.z,angle);
				TexturedModel texturedModel = new TexturedModel(OBJLoader.loadOBJModel("coral"+(rand.nextInt(2)+1), loader),new ModelTexture(loader.loadTexture(colors[rand.nextInt(2)])));
				Vector3f position = new Vector3f(this.x+pos.x,this.y+pos.y,this.z+pos.z);
				float size = rand.nextInt(max_coral_size-min_coral_size+1)+min_coral_size;
				coral.add(new Entity(texturedModel,position,(float)Math.toDegrees(rotation.y),(float)Math.toDegrees(rotation.x),(float)Math.toDegrees(rotation.z),size));
			}
		}
		isTerrainLoaded = true;
	}
	public Vector3f toEuler(double x,double y,double z,double angle) {
		Vector3f rotation = new Vector3f(0,0,0);
		double s=Math.sin(angle);
		double c=Math.cos(angle);
		double t=1-c;
		if ((x*y*t + z*s) > 0.998) { // north pole singularity detected
			rotation.x = (float) (2*Math.atan2(x*Math.sin(angle/2),Math.cos(angle/2)));
			rotation.y = (float) (Math.PI/2);
			rotation.z = 0;
			return rotation;
		}
		if ((x*y*t + z*s) < -0.998) { // south pole singularity detected
			rotation.x = (float) (-2*Math.atan2(x*Math.sin(angle/2),Math.cos(angle/2)));
			rotation.y = (float) (-Math.PI/2);
			rotation.z = 0;
			return rotation;
		}
		rotation.x = (float) Math.atan2(y * s- x * z * t , 1 - (y*y+ z*z ) * t);
		rotation.y = (float) Math.asin(x * y * t + z * s) ;
		rotation.z = (float) Math.atan2(x * s - y * z * t , 1 - (x*x + z*z) * t);
		return rotation;
	}
	public float[] flatten(float[][][] array){
		float[] converted = new float[(width+1)*(height+1)*(width+1)];
		for(int y = 0; y<height+1; y++){
			for(int x = 0; x<width+1; x++){
				for(int z = 0; z<width+1; z++){	
					converted[x+y*(width+1)*(width+1)+z*(width+1)] = array[x][y][z];
				}
			}
		}
		return converted;
	}
	public void loadMarchingTerrain(){
		for(int y = 0; y<height+1; y++){
			for(int x = x1; x<x1+width+1; x++){
				for(int z = z1; z<z1+width+1; z++){	
					double noise3d = this.sumOctaves(3,x,y,z,0.5,0.01,0,1); // creates 3d terrain like caves and overhangs
					double noise2d = this.sumOctaves(3,x,z,0.5,0.01,0,1); // creates 2d terrain like mountains and hills (gives only height)
					double cave = this.sumOctaves(1,x,y,z,0.5,0.035,0,1); // creates 3d terrain like caves and overhangs
					double mask =  this.sumOctaves(1,x,z,0.1,0.0001,0,1); // creates a 2d mask to vary heights of regions, lower the scale more diversity in regions, higher the scale more mountains and canyons
					float curHeight = -y+height/3+(float)height*(float)(noise2d*(1-perlin3Dratio)+noise3d*perlin3Dratio)*(float)mask; // mixing them together with correct ratio of 3d and 2d data
					if(cave < surfaceLevel){//checks if the noise value is empty space
						curHeight = (float)cave;
					}
					terrainMap[x-x1][y][z-z1] = curHeight;
				}
			}
		}

		for(int y = 0; y<height; y++){
			for(int z = 0; z<width; z++){
				for(int x = 0; x<width; x++){
					marchCube(new Vector3f(x,y,z));	
				}
			}
		}		
		vertices = new float[verticeArray.size()*3];
		indice = new int[verticeArray.size()];
		int vertexCount = 0;
		for(Vector3f v : verticeArray){
			vertices[vertexCount++] = this.x+v.x*interval;
			vertices[vertexCount++] = this.y+v.y*interval;
			vertices[vertexCount++] = this.z+v.z*interval;
		}
		for(int i = 0; i<indice.length; i++){
			indice[i] = i;
		}
		terrainMap = new float[0][0][0];
		isDataLoaded = true;
 
		System.out.println(vertices.length);
//		vertices = marchingCompute.useProgram(width, height, width, flatten(terrainMap), surfaceLevel,(float)interval);
//		indice = new int[vertices.length];
//		
//		for(int i = 0; i<indice.length; i++){
//			indice[i] = i;
//		}
//		terrainMap = new float[0][0][0];
//		isTerrainLoaded = true;
	}
	
	public void loadModel(){
		model = loader.loadToVao(vertices, null, indice);
	}
	
	public int configIndex(float[] cube){
		int configIndex = 0;
		for(int i = 0; i<8; i++){
			if(cube[i] > surfaceLevel){
				configIndex |= 1 << i;
			}
		}		
		return configIndex;
	}
	
	public Vector3f smoothPoint(Vector3f vert1, Vector3f vert2, int indice, float[] cube){
		float sampleVert1 = cube[MarchingCubeTable.edges[indice][0]];
		float sampleVert2 = cube[MarchingCubeTable.edges[indice][1]];
		
		float difference = sampleVert1-sampleVert2;
		
		if(difference == 0){
			difference = surfaceLevel;
		}else{
			difference = (surfaceLevel-sampleVert1)/difference;
		}
		Vector3f a2 = vert1.subtract(vert2).scale(difference);
		
		Vector3f vertPos = vert1.add(a2);

		return vertPos;
	}
	public void marchCube(Vector3f position){
		//create cube
		float[] cube = new float[8];
		for(int i = 0; i<8; i++){		
			Vector3f corner = position.add(MarchingCubeTable.cornerTable[i]);		
			cube[i] = terrainMap[(int) corner.x][(int) corner.y][(int) corner.z];		
		}
		//search through index
		int currentConfigIndex = configIndex(cube);
		if(currentConfigIndex == 0 || currentConfigIndex == 255){
			return;
		}		
		//search through points
		int edgeIndex = 0;
		for(int j = 0; j<5; j++){
			for(int i = 0; i<3; i++){
				int indice = MarchingCubeTable.triTable[currentConfigIndex][edgeIndex];			
				if(indice == -1){
					return;
				}
				Vector3f vert1 = position.add(MarchingCubeTable.cornerTable[MarchingCubeTable.edges[indice][1]]);
				Vector3f vert2 = position.add(MarchingCubeTable.cornerTable[MarchingCubeTable.edges[indice][0]]);			
				Vector3f vertPos = this.smoothPoint(vert1, vert2, indice, cube);				
				verticeArray.add(vertPos);
				edgeIndex++;
			}
		}
	}
	
	/*
	 * Simplex Noise functions
	 */
	public double sumOctaves(int iterations, double x, double y, double z, double persistance, double scale, double low, double high){
		double maxamp = 0;
		double amp = this.amp;
		double frequency = scale;
		double noise = 0;
		
		for(int i = 0; i<iterations; i++){
			noise += SimplexNoise.noise((x)*frequency, (y)*frequency, (z)*frequency)*amp;
			maxamp += amp;
			amp *= persistance;
			frequency *= 2;
		}
		
		noise /= maxamp;
		
		noise = noise * (high - low) / 2 + (high + low) / 2;
		return noise;
	}
	
	public double sumOctaves(int iterations, int x, int y, double persistance, double scale, double low, double high){
		double maxamp = 0;
		double amp = this.amp;
		double frequency = scale;
		double noise = 0;
		
		for(int i = 0; i<iterations; i++){
			noise += SimplexNoise.noise((x)*frequency, (y)*frequency)*amp;
			maxamp += amp;
			amp *= persistance;
			frequency *= 2;
		}
		
		noise /= maxamp;
		
		noise = noise * (high - low) / 2 + (high + low) / 2;
		return noise;
	}
	
	public List<Entity> getCubes(){
		return cubes;
	}
	public float getX() {
		return x;
	}
	public float getZ() {
		return z;
	}
	public float getY() {
		return y;
	}
	public boolean isDataLoaded(){
		return this.isDataLoaded;
	}
	public boolean isTerrainLoaded(){
		return this.isTerrainLoaded;
	}
	public int getChunkX(){
		return (int) this.CHUNK_X;
	}
	public int getChunkZ(){
		return (int) this.CHUNK_Z;
	}
	public RawModel getRawModel(){
		return model;
	}
	public int getSize(){
		return width*interval;
	}
	public float[] getVertices(){
		return vertices;
	}
	public int getInterval(){
		return this.interval;
	}
	public List<Entity> getCoral(){
		return coral;
	}
}
