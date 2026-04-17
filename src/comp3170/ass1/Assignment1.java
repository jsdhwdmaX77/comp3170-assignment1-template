package comp3170.ass1;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.glfw.GLFW.*;

import java.io.File;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import comp3170.IWindowListener;
import comp3170.InputManager;
import comp3170.OpenGLException;
import comp3170.ShaderLibrary;
import comp3170.Window;

public class Assignment1 implements IWindowListener {

	final private File DIRECTORY = new File("src/comp3170/ass1/shaders");

	public static Assignment1 instance;
	private InputManager input;
	private Window window;	
	private int screenWidth = 800;
	private int screenHeight = 800;
	private Scene scene;
	private Matrix4f mvpMatrix = new Matrix4f();
	private static final float WORLD_HEIGHT = 40.0f;
	private static final float LEGACY_SCENE_SCALE = 20.0f;

	public Assignment1() throws OpenGLException {
		instance = this;
		window = new Window("Looney Lander", screenWidth, screenHeight, this);
		window.setResizable(true);
		window.run();
	}

	@Override
	public void init() {
		
		new ShaderLibrary(DIRECTORY);
		scene = new Scene();
		input = new InputManager(window);
		glClearColor(0.0f, 0.f, 0.1f, 1.0f); 	
	}

	public void update() {
	    boolean up = input.isKeyDown(GLFW_KEY_W);
	    boolean left = input.isKeyDown(GLFW_KEY_A);
	    boolean right = input.isKeyDown(GLFW_KEY_D);

	    if (input.isKeyDown(GLFW_KEY_1)) {
	        scene.setLocalCamera(false);
	    }
	    if (input.isKeyDown(GLFW_KEY_2)) {
	        scene.setLocalCamera(true);
	    }

	    scene.update(up, left, right);

	    input.clear();
	}

	@Override
	public void draw() {
	    update();

	    glViewport(0, 0, screenWidth, screenHeight);
	    glClear(GL_COLOR_BUFFER_BIT);

	    float aspect = (float) screenWidth / (float) screenHeight;

	    float worldWidth = WORLD_HEIGHT * aspect;

	    mvpMatrix.identity();
	    mvpMatrix.scale(2.0f / worldWidth, 2.0f / WORLD_HEIGHT, 1.0f);
	    mvpMatrix.scale(LEGACY_SCENE_SCALE, LEGACY_SCENE_SCALE, 1.0f);

	    scene.draw(mvpMatrix);
	}
	
	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) throws OpenGLException {
		new Assignment1();
	}
}
