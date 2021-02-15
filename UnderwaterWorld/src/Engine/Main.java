package Engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Entities.Player;
import Entities.Boids.BoidHelper;
import Entities.Boids.BoidManager;
import Entities.Boids.Boids;
import Maths.Vector3f;
import Models.TexturedModel;
import Render.Loader;
import Render.MasterRenderer;
import Render.OBJLoader;
import Render.Window;
import Terrain.ChunkManager;
import Terrain.Terrain;
import Textures.ModelTexture;

public class Main implements Runnable{
	public Thread thread;
	public Window window;
	
	Loader loader;
	ChunkManager chunk;
	MasterRenderer renderer;
	List<Light> lights = new ArrayList<Light>();
	Camera camera;
	Player player;
	BoidManager boids;
	
	List<Terrain> terrain;
	
	List<Entity> entities = new ArrayList<Entity>();
	
	boolean running = true;
	
	long lastTime = System.nanoTime();
	long deltaTime = 0;
	
	public Main(){
		@SuppressWarnings("unused")
		Random rand = new Random();
		window = new Window(1200, 1000, "test");
		this.loader = new Loader();
		BoidHelper.init();
		window.create();
		player = new Player(new TexturedModel(OBJLoader.loadOBJModel("submarine", loader),new ModelTexture(loader.loadTexture("submarine"))),new Vector3f(0,2000,0),0,0,0,4f);
		renderer = new MasterRenderer();
		entities.add(player);
		entities.add(new Entity(new TexturedModel(OBJLoader.loadOBJModel("cube", loader),new ModelTexture(loader.loadTexture("blank"))),new Vector3f(0,0,0),0,0,0,1f));
		
//		for(int i = 0; i<BoidHelper.directions.length; i++){
//			entities.add(new Entity(new TexturedModel(OBJLoader.loadOBJModel("cube", loader),new ModelTexture(loader.loadTexture("blank"))),BoidHelper.directions[i].scale(15),0,0,0,1f));
//			Vector3f p = entities.get(entities.size()-1).getPosition();
//			entities.get(entities.size()-1).setPosition(p.add(new Vector3f(0,2300,0)));
//			System.out.println("x ="+p.x+"y ="+p.y+"z ="+p.z);
//		}
		List<Boids> flock = new ArrayList<Boids>();
//		for(int i = 0; i<1000; i++){
//			Boids boid = new Boids(new TexturedModel(OBJLoader.loadOBJModel("cube", loader),new ModelTexture(loader.loadTexture("blank"))),new Vector3f(rand.nextInt(150),2300-rand.nextInt(150),rand.nextInt(150)),0,0,0,1f);
//			entities.add(boid);
//			flock.add(boid);
//		}
		
		boids = new BoidManager(flock);
		
		chunk = new ChunkManager(player, loader);
		terrain = chunk.getTerrain();
		
		lights.add(new Light(new Vector3f(0,3000,0),new Vector3f(1f,1f,1f)));
		lights.add(new Light(new Vector3f(0,0,0),new Vector3f(2,2,2),new Vector3f(1,0.01f,0.00001f)));
		this.camera = new Camera();
		
		new Thread(new Runnable(){public void run(){
			while(!window.shoudlClose()){
				if(Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
					break;
				}
				chunk.run();
				for(int i = 0; i<chunk.getTerrain().size(); i++){
					Terrain t = chunk.getTerrain().get(i);
					if(!t.isDataLoaded()){
						t.loadMarchingTerrain();
					}
				}
			}
		}}).start();
	}
	public void start(){
		thread = new Thread(this);
		thread.run();
	}
	public void run(){
		while(!window.shoudlClose()){
			update();
			render();
			deltaTime = getDeltaTime();
			if(Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
				break;
			}
			if(Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
				window.mouseState(true);
			}
		}
		renderer.cleanUp();
		loader.cleanUp();
		window.destroy();
	}
	private void render() {
		camera.move(player);
		renderer.render(lights, camera, terrain);
		window.swapBuffers();
	}
	private void update() {
		for(Entity entity : entities){
			renderer.sortEntity(entity);
		}
		//boids.run();
		for(int i = 0; i<terrain.size(); i++){
			Terrain t = terrain.get(i);
			if(t.isDataLoaded()){
				if(!t.isTerrainLoaded()){
					t.loadTerrain();
				}
				for(Entity e : t.getCoral()){
					renderer.sortEntity(e);
				}
			}
		}
		player.move(lights.get(1),entities.get(1));
				
		window.update();
		
	}
	public long getDeltaTime(){
		long time = System.nanoTime();
		long delta = (time-lastTime)/1000000;
		lastTime = time;
		return delta;
	}
	public static void main(String[] args){
		Main main = new Main();
		main.start();
	}
}
