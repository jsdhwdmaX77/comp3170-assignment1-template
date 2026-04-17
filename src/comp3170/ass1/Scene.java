package comp3170.ass1;

import java.awt.Color;
import java.util.Random;

import org.joml.Vector3f;
import org.joml.Matrix4f;

import comp3170.SceneObject;
import comp3170.ass1.sceneobjects.Mountain;
import comp3170.ass1.sceneobjects.Star;
import comp3170.ass1.sceneobjects.Lander;

public class Scene extends SceneObject {
	
	public static Scene theScene;
    private final Random random = new Random();
    private Lander lander;
    private float cameraY = 0.0f;
    private boolean localCamera = false;
    private SceneObject backgroundLayer;
    private SceneObject worldLayer;
    
    public void setLocalCamera(boolean enabled) {
        localCamera = enabled;
    }

    public boolean isLocalCamera() {
        return localCamera;
    }
		
    public Scene() {
        theScene = this;

        backgroundLayer = new SceneObject();
        backgroundLayer.setParent(this);

        worldLayer = new SceneObject();
        worldLayer.setParent(this);

        createStarfield();
        createTerrain();

        lander = new Lander();
        lander.setParent(worldLayer);
        lander.getMatrix().translate(0.0f, 0.35f, 0.0f);
    }
    
    public Lander getLander() {
        return lander;
    }
    
    public void update(boolean up, boolean left, boolean right) {
        lander.update(up, left, right);

        float landerY = lander.getY();

        float topThreshold = cameraY + 0.5f;
        float bottomThreshold = cameraY - 0.5f;

        if (landerY > topThreshold) {
            cameraY = landerY - 0.5f;
        } else if (landerY < bottomThreshold) {
            cameraY = landerY + 0.5f;
        }
    }

    @Override
    public void draw(Matrix4f parentMatrix) {
        Matrix4f backgroundMatrix = new Matrix4f(parentMatrix);
        Matrix4f worldMatrix = new Matrix4f(parentMatrix);

        if (localCamera) {
            backgroundMatrix.translate(0.0f, -lander.getY() * 0.3f, 0.0f);
            backgroundMatrix.rotateZ(-lander.getAngle() * 0.3f);
            backgroundMatrix.translate(0.0f, 0.35f, 0.0f);

            worldMatrix.translate(0.0f, -lander.getY(), 0.0f);
            worldMatrix.rotateZ(-lander.getAngle());
            worldMatrix.translate(0.0f, 0.35f, 0.0f);
        } else {
            backgroundMatrix.translate(0.0f, -cameraY * 0.3f, 0.0f);
            worldMatrix.translate(0.0f, -cameraY, 0.0f);
        }

        backgroundLayer.draw(backgroundMatrix);
        worldLayer.draw(worldMatrix);
    }

    
    private void createStarfield() {
        for (int i = 0; i < 70; i++) {
            float outerRadius = randomRange(0.008f, 0.02f);
            float innerRadius = outerRadius * randomRange(0.35f, 0.55f);

            Vector3f colour = new Vector3f(
                randomRange(0.9f, 1.0f),
                randomRange(0.75f, 0.95f),
                randomRange(0.2f, 0.5f)
            );

            Star star = new Star(outerRadius, innerRadius, colour);
            star.setParent(backgroundLayer);

            float x = randomRange(-0.98f, 0.98f);
            float y = randomRange(-0.2f, 0.95f);

            star.getMatrix().translate(x, y, 0.0f);

            float angle = randomRange(0.0f, (float)(Math.PI * 2.0));
            star.getMatrix().rotateZ(angle);
        }
    }

	private void createTerrain() {
	    for (int i = 0; i < 40; i++) {
	        float baseWidth = randomRange(0.08f, 0.18f);
	        float height = randomRange(0.08f, 0.28f);
	        float topWidth = baseWidth * randomRange(0.25f, 0.55f);

	        float hue = 0.12f; 
	        float saturation = randomRange(0.0f, 1.0f);
	        float brightness = randomRange(0.0f, 1.0f);

	        Color rgb = Color.getHSBColor(hue, saturation, brightness);

	        Vector3f colour = new Vector3f(
	            rgb.getRed() / 255.0f,
	            rgb.getGreen() / 255.0f,
	            rgb.getBlue() / 255.0f
	        );

	        Mountain mountain = new Mountain(baseWidth, height, topWidth, colour);
	        mountain.setParent(worldLayer);

	        float x = -1.05f + i * 0.055f + randomRange(-0.015f, 0.015f);
	        float y = -0.95f;

	        mountain.getMatrix().translate(x, y, 0.0f);
	    }
	}

    private float randomRange(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
    
  }