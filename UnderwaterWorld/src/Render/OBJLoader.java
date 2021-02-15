package Render;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import Models.RawModel;
import Maths.Vector2f;
import Maths.Vector3f;


public class OBJLoader {
	/*
	 * 
	 * .OBJ files organizes models into 4 groups of data
	 * - Vertex coordinates 
	 * - UV(texture) coordinates 
	 * - normal coordinates
	 * - faces(indices) instructions
	 * Vertex, UV and Normals are ordered randomly and therefore needs the face element instructions in order to properly group together
	 * Instead of using data positions to organise data, face elements uses the index which the data is placed within their own list 
	 * In this case we are using the vertex positions and indices instruction to correctly group UV, vertex, and normals together for OpenGL to use
	 * Face elements also give us the indices instructions to build the model
	 * TLDR; UV,Vertex data and Normals are not listed in any order, Vertex data is used to order data, using faces element data we group the UV and normals to their correct vertex
	 * 
	 */
	public static RawModel loadOBJModel(String file, Loader loader){//Loads obj file into textured model and raw model
		FileReader fr = null;
		try {
			fr = new FileReader(new File("res/"+file+".obj"));//Gets OBJ file
		} catch (FileNotFoundException e) {
			System.err.println("couldn't find file");
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(fr);//Puts file into buffered reader
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>(); //creates List for all the vertices ordered
		List<Vector2f> textures = new ArrayList<Vector2f>(); //creates List for all the texture coords unordered
		List<Integer> indices = new ArrayList<Integer>(); //creates List for all the indices ordered
		
		float[] verticesArray = null; //creates float array for all the vertices ordered
		float[] texturesArray = null; //creates float array for all the texture coords ordered
		int[] indicesArray = null; //creates float array for all the indices ordered
		
		try{
			while(true){
				line = reader.readLine();
				String[] currentLine = line.split(" ");//splits line into sections based on spaces
				if(line.startsWith("v ")){ //checks if line is a vertice element holder
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),Float.parseFloat(currentLine[2]),Float.parseFloat(currentLine[3])); //converts text to float
					vertices.add(vertex); //places vertex into unordered vertice list
				}else if(line.startsWith("vt ")){ //checks if line is a texture coord element holder
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),Float.parseFloat(currentLine[2])); //converts text into float
					textures.add(texture); //adds texture coord into unordered texture coord list;
				}else if(line.startsWith("f ")){ //checks if line is a face element holder
					texturesArray = new float[vertices.size()*2]; //sets size of ordered texture coord array
					break;
				}
			}		
			while(line != null){
				if(!line.startsWith("f ")){ //checks if line is not a face element holder
					line = reader.readLine(); // if line is not a indices holder continue
					continue;
				}
				String[] currentLine = line.split(" "); //splits face data into 3 vertices
				String[] vertex1 = currentLine[1].split("/"); //splits the data into vertices data
				String[] vertex2 = currentLine[2].split("/"); //splits the data into texture coord data
				String[] vertex3 = currentLine[3].split("/"); //splits the data into normals data

				processVertexData(vertex1, indices, textures, texturesArray); //connects vertex with corresponding texture coord and normal based on indice data
				processVertexData(vertex2, indices, textures, texturesArray); //connects vertex with corresponding texture coord and normal based on indice data
				processVertexData(vertex3, indices, textures, texturesArray); //connects vertex with corresponding texture coord and normal based on indice data
				line = reader.readLine(); //continue to next line
			}
			
			verticesArray = new float[vertices.size()*3];
			indicesArray = new int[indices.size()];
			
			int verticesNum = 0;
			for(Vector3f vertice : vertices){
				verticesArray[verticesNum++] = vertice.x;
				verticesArray[verticesNum++] = vertice.y;
				verticesArray[verticesNum++] = vertice.z;
			}
			
			for(int i = 0; i<indices.size(); i++){
				indicesArray[i] = indices.get(i);
			}
			reader.close(); //close reader
		}catch(Exception e){
			e.printStackTrace();
		}
		return loader.loadToVao(verticesArray, texturesArray, indicesArray); //Loads data into vao
	}
	public static void processVertexData(String[] vertexData, List<Integer> indices, List<Vector2f> textures, float[] texturesArray ){
		int vertexPointer = Integer.parseInt(vertexData[0])-1;
		indices.add(vertexPointer);
		
		Vector2f textureCoords = textures.get(Integer.parseInt(vertexData[1])-1);
		texturesArray[vertexPointer*2] = textureCoords.x;
		texturesArray[vertexPointer*2+1] = textureCoords.y;


	}
	public static void expandData(float[] normalsArray, float[] texturesArray, float[] vertexArray, int[] indicesArray){
		float[] newNormals = new float[indicesArray.length];
		float[] newTextures = new float[indicesArray.length/3*2];;
		float[] newVertex = new float[indicesArray.length];;
		for(int i = 0; i<indicesArray.length/3; i++){
			newVertex[i*3] = vertexArray[indicesArray[i*3]];
			newVertex[i*3+1] = vertexArray[indicesArray[i*3]+1];
			newVertex[i*3+2] = vertexArray[indicesArray[i*3]+2];

			newNormals[i*3] = normalsArray[indicesArray[i*3]];
			newNormals[i*3+1] = normalsArray[indicesArray[i*3]+1];
			newNormals[i*3+2] = normalsArray[indicesArray[i*3]+2];

			newTextures[i*2] = texturesArray[indicesArray[i*2]];
			newTextures[i*2+1] = texturesArray[indicesArray[i*2]+1];
		}
	}
}
