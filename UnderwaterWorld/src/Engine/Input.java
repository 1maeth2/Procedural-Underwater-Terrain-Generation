package Engine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Input {
	private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
	private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
	private static double mouseX,mouseY;
	private GLFWKeyCallback keyboard;
	private GLFWCursorPosCallback mouseMove;
	private GLFWMouseButtonCallback mouseButtons;
	private int num = 0;
	public Input(){
		keyboard = new GLFWKeyCallback(){//reads all keyboard inputs and within class uses invoke function
			@Override
			public void invoke(long window, int key, int scancode, int action, int arg4) {
				keys[key] = (action != GLFW.GLFW_RELEASE);
			}
		};
		mouseMove = new GLFWCursorPosCallback(){
			@Override
			public void invoke(long window, double mousex, double mousey) {
				mouseX = mousex;
				mouseY = mousey;
			}
		};
		mouseButtons = new GLFWMouseButtonCallback(){
			@Override
			public void invoke(long window, int button, int action, int mod) {
				buttons[button] = (action != GLFW.GLFW_RELEASE);
			}
		};
	}
	public void setNum(int i){
		this.num = i;
	}
	public int getNum(){
		return num;
	}
	public void destroy(){
		keyboard.free();
		mouseMove.free();
		mouseButtons.free();
	}
	public static boolean isKeyDown(int key){
		return keys[key];
	}
	public static boolean isMouseDown(int button){
		return buttons[button];
	}
	public static double getMouseX() {
		return mouseX;
	}
	public static double getMouseY() {
		return mouseY;
	}
	public GLFWKeyCallback getKeyboard() {
		return keyboard;
	}
	public GLFWCursorPosCallback getMouseMove() {
		return mouseMove;
	}
	public GLFWMouseButtonCallback getMouseButtons() {
		return mouseButtons;
	}
}
