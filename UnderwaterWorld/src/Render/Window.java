package Render;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import Engine.Input;

public class Window {
	private Input input;
	private static int WIDTH;
	private static int HEIGHT;
	private String TITLE;
	
	private long window = 0;
	
	private long lastFrameTime;
	private float delta;

	public Window(int width, int height, String title){
		Window.WIDTH = width;
		Window.HEIGHT = height;
		this.TITLE = title;
	}
	public void create(){
		if(!GLFW.glfwInit()){//intialising GLFW
			throw new IllegalStateException("Could not initialize glfw");
		}
		input = new Input();//Creating input
		GLFW.glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);//Setting window hint
		window = GLFW.glfwCreateWindow(WIDTH,HEIGHT,TITLE,0,0);//Creating window
		if(window == 0){
			throw new IllegalStateException("Could not create window");
		}
		
		glfwSetKeyCallback(window, input.getKeyboard());//Setting call backs (keyboard and mouse inputs)
		glfwSetCursorPosCallback(window, input.getMouseMove());
		glfwSetMouseButtonCallback(window, input.getMouseButtons());
		
		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(glfwGetPrimaryMonitor());//getting monitor
		
		glfwSetWindowPos(window,(videoMode.width() - WIDTH)/2,(videoMode.height() - HEIGHT)/2);
		
		glfwMakeContextCurrent(window);//setting current context for rendering
		GL.createCapabilities();//Setting window up for OpenGL
		
		GLFW.glfwShowWindow(window); // show window
		
		GLFW.glfwSwapInterval(1);// capping frame rate
		
	}
	
	public void destroy(){//cleaning up after game closes
		input.destroy();
		GLFW.glfwWindowShouldClose(window);
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}
	public void update(){//updates
		GLFW.glfwPollEvents(); // basically update;
		long currentFrameTime = System.currentTimeMillis();
		delta = (currentFrameTime-lastFrameTime)/1000;
		lastFrameTime = currentFrameTime;
	}
	public void mouseState(boolean lock){
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, lock ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
	}
	public float getDelta(){
		return delta;
	}
	public void swapBuffers(){
		GLFW.glfwSwapBuffers(window); // basically repaint();
	}
	public boolean shoudlClose(){
		return GLFW.glfwWindowShouldClose(window);
	}
	
	public static int getWIDTH() {
		return WIDTH;
	}
	public static int getHEIGHT() {
		return HEIGHT;
	}
	public long getWindow(){
		return window;
	}
	
}
