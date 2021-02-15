package Terrain;

import java.util.ArrayList;
import java.util.List;

import Entities.Player;
import Render.Loader;

public class ChunkManager{
	
	Player player; 
	private List<Terrain> loadedChunks = new ArrayList<Terrain>();
	private List<Terrain> unloadedChunks = new ArrayList<Terrain>();
	Terrain sampleTerrain = new Terrain(0,0, null);
	Loader loader;
	float player_x,player_z;
	
	public ChunkManager(Player player, Loader loader){
		this.player = player;
		this.loader = loader;
		for(int x = -1; x<1; x++){
			for(int z = -1; z<1; z++){
				Terrain t = new Terrain(x,z,loader);
				t.loadMarchingTerrain();
				loadedChunks.add(t);
			}
		}
	}
	public void run(){
		player_x = player.getObject_x();
		player_z = player.getObject_z();
		unloadChunks();
		loadChunks();
		setChunks();
	}
	public void setChunks(){
		for(Terrain t : loadedChunks){
			if(t.getChunkX() == player_x && t.getChunkZ() == player_z){
				player.setTerrain(t);
				break;
			}
		}
	}
	public void unloadChunks(){
		for(int i = 0; i<loadedChunks.size(); i++){
			Terrain t = loadedChunks.get(i);
			if(t.getChunkX() < player_x-2 || t.getChunkX() > player_x+2 || t.getChunkZ() < player_z-2 || t.getChunkZ() > player_z+2){
					loadedChunks.remove(t);
					unloadedChunks.add(t);
			}
		}
	}
	public void loadChunks(){
		for(int x = (int) player_x-2; x<=player_x+2; x++){
			for(int z = (int) player_z-2; z<=player_z+2; z++){
				boolean contains = false;
				for(Terrain t : loadedChunks){
					if(t.getChunkX() == x && t.getChunkZ() == z){
						contains = true;
					}
				}
				for(int i = 0; i<unloadedChunks.size(); i++){
					Terrain t = unloadedChunks.get(i);
					if(t.getChunkX() == x && t.getChunkZ() == z){
						unloadedChunks.remove(t);
						loadedChunks.add(t);
						contains = true;
					}
				}
				if(!contains){
					Terrain t = new Terrain(x,z,loader);
					loadedChunks.add(t);
				}
			}
		}
	}
	public List<Terrain> getTerrain(){
		return loadedChunks;
	}
}
