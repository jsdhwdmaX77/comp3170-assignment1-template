package comp3170.ass1.sceneobjects;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;

public class Lander extends SceneObject {

    private static final String VERTEX_SHADER = "lander_vertex.glsl";
    private static final String FRAGMENT_SHADER = "lander_fragment.glsl";

    private final Shader shader;

    private final Vector4f[] bodyVertices;
    private final Vector3f[] bodyColours;
    private final int bodyVertexBuffer;
    private final int bodyColourBuffer;

    private final Vector4f[] baseVertices;
    private final Vector3f[] baseColours;
    private final int baseVertexBuffer;
    private final int baseColourBuffer;

    private final Vector4f[] leftLegVertices;
    private final Vector3f[] leftLegColours;
    private final int leftLegVertexBuffer;
    private final int leftLegColourBuffer;

    private final Vector4f[] rightLegVertices;
    private final Vector3f[] rightLegColours;
    private final int rightLegVertexBuffer;
    private final int rightLegColourBuffer;
    
    private float x = 0.0f;
    private float y = 0.35f;

    private float vx = 0.0f;
    private float vy = 0.0f;
    private float angle = 0.0f;

    private static final float THRUST_X = 0.0015f;
    private static final float THRUST_Y = 0.0038f;
    private static final float GRAVITY = 0.0008f;
    private static final float ROTATION_SPEED = 0.03f;
    private static final float MAX_ANGLE = (float) Math.toRadians(45.0);
    
    private final Vector4f[] flameVertices;
    private final Vector3f[] flameColours;
    private final int flameVertexBuffer;
    private final int flameColourBuffer;

    private boolean thrustUp = false;
    private boolean thrustLeft = false;
    private boolean thrustRight = false;
    
    private static final float MIN_X = -0.95f;
    private static final float MAX_X = 0.95f;
    private static final float MIN_Y = -0.75f;
    private static final float MAX_Y = 1.20f;

    public float getAngle() {
        return angle;
    }
    public Lander() {
        shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

        bodyVertices = createHexagon(0.08f, 0.12f);
        bodyColours = createBodyColours(bodyVertices.length);
        bodyVertexBuffer = GLBuffers.createBuffer(bodyVertices);
        bodyColourBuffer = GLBuffers.createBuffer(bodyColours);

        baseVertices = new Vector4f[] {
            new Vector4f(-0.09f, -0.08f, 0f, 1f),
            new Vector4f( 0.09f, -0.08f, 0f, 1f),
            new Vector4f( 0.09f, -0.16f, 0f, 1f),

            new Vector4f(-0.09f, -0.08f, 0f, 1f),
            new Vector4f( 0.09f, -0.16f, 0f, 1f),
            new Vector4f(-0.09f, -0.16f, 0f, 1f)
        };
        baseColours = new Vector3f[] {
            new Vector3f(0.95f, 0.95f, 0.95f),
            new Vector3f(0.80f, 0.80f, 0.80f),
            new Vector3f(0.55f, 0.55f, 0.55f),

            new Vector3f(0.95f, 0.95f, 0.95f),
            new Vector3f(0.55f, 0.55f, 0.55f),
            new Vector3f(0.75f, 0.75f, 0.75f)
        };
        baseVertexBuffer = GLBuffers.createBuffer(baseVertices);
        baseColourBuffer = GLBuffers.createBuffer(baseColours);

        leftLegVertices = new Vector4f[] {
            new Vector4f(-0.09f, -0.08f, 0f, 1f),
            new Vector4f(-0.20f, -0.22f, 0f, 1f),
            new Vector4f(-0.09f, -0.22f, 0f, 1f)
        };
        leftLegColours = new Vector3f[] {
            new Vector3f(0.45f, 0.90f, 0.25f),
            new Vector3f(0.20f, 0.60f, 0.15f),
            new Vector3f(0.30f, 0.75f, 0.20f)
        };
        leftLegVertexBuffer = GLBuffers.createBuffer(leftLegVertices);
        leftLegColourBuffer = GLBuffers.createBuffer(leftLegColours);

        rightLegVertices = new Vector4f[] {
            new Vector4f(0.09f, -0.08f, 0f, 1f),
            new Vector4f(0.09f, -0.22f, 0f, 1f),
            new Vector4f(0.20f, -0.22f, 0f, 1f)
        };
        rightLegColours = new Vector3f[] {
            new Vector3f(0.45f, 0.90f, 0.25f),
            new Vector3f(0.30f, 0.75f, 0.20f),
            new Vector3f(0.20f, 0.60f, 0.15f)
        };
        rightLegVertexBuffer = GLBuffers.createBuffer(rightLegVertices);
        rightLegColourBuffer = GLBuffers.createBuffer(rightLegColours);
        
        flameVertices = new Vector4f[] {
        	    new Vector4f(-0.03f, -0.16f, 0f, 1f),
        	    new Vector4f( 0.03f, -0.16f, 0f, 1f),
        	    new Vector4f( 0.00f, -0.28f, 0f, 1f)
        	};

        	flameColours = new Vector3f[] {
        	    new Vector3f(1.0f, 0.9f, 0.1f),
        	    new Vector3f(1.0f, 0.6f, 0.0f),
        	    new Vector3f(1.0f, 0.2f, 0.0f)
        	};

        	flameVertexBuffer = GLBuffers.createBuffer(flameVertices);
        	flameColourBuffer = GLBuffers.createBuffer(flameColours);
    }

    private Vector4f[] createHexagon(float radius, float yOffset) {
        Vector4f center = new Vector4f(0f, yOffset, 0f, 1f);
        Vector4f[] vertices = new Vector4f[18];

        for (int i = 0; i < 6; i++) {
            float angle1 = (float) (Math.PI / 3.0 * i + Math.PI / 6.0);
            float angle2 = (float) (Math.PI / 3.0 * (i + 1) + Math.PI / 6.0);

            Vector4f p1 = new Vector4f(
                radius * (float) Math.cos(angle1),
                yOffset + radius * (float) Math.sin(angle1),
                0f, 1f
            );

            Vector4f p2 = new Vector4f(
                radius * (float) Math.cos(angle2),
                yOffset + radius * (float) Math.sin(angle2),
                0f, 1f
            );

            vertices[i * 3] = center;
            vertices[i * 3 + 1] = p1;
            vertices[i * 3 + 2] = p2;
        }

        return vertices;
    }

    private Vector3f[] createBodyColours(int count) {
        Vector3f[] colours = new Vector3f[count];

        for (int i = 0; i < count; i += 3) {
            colours[i] = new Vector3f(0.20f, 1.00f, 0.30f);     // center
            colours[i + 1] = new Vector3f(0.45f, 1.00f, 0.45f); // edge 1
            colours[i + 2] = new Vector3f(0.10f, 0.75f, 0.20f); // edge 2
        }

        return colours;
    }
    public void update(boolean up, boolean left, boolean right) {
        thrustUp = up;
        thrustLeft = left;
        thrustRight = right;

        if (up) {
            vy += THRUST_Y;
        }
        if (left) {
            vx -= THRUST_X;
            angle += ROTATION_SPEED;
        }
        if (right) {
            vx += THRUST_X;
            angle -= ROTATION_SPEED;
        }

        if (!left && !right) {
            if (angle > 0) {
                angle -= ROTATION_SPEED;
                if (angle < 0) angle = 0;
            } else if (angle < 0) {
                angle += ROTATION_SPEED;
                if (angle > 0) angle = 0;
            }
        }

        if (angle > MAX_ANGLE) {
            angle = MAX_ANGLE;
        }
        if (angle < -MAX_ANGLE) {
            angle = -MAX_ANGLE;
        }
      
        vy -= GRAVITY;
       
        vx *= 0.98f;
        vy *= 0.98f;
        
        x += vx;
        y += vy;

        float floorY = -0.75f;
        x += vx;
        y += vy;

        if (x < MIN_X) {
            x = MIN_X;
            vx = 0.0f;
        }
        if (x > MAX_X) {
            x = MAX_X;
            vx = 0.0f;
        }

        if (y < MIN_Y) {
            y = MIN_Y;
            vy = 0.0f;
        }
        if (y > MAX_Y) {
            y = MAX_Y;
            vy = 0.0f;
        }

        getMatrix().identity();
        getMatrix().translate(x, y, 0.0f);
        getMatrix().rotateZ(angle);
    }
    
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    

    @Override
    protected void drawSelf(Matrix4f matrix) {
        shader.enable();
        shader.setUniform("u_mvpMatrix", matrix);

        shader.setAttribute("a_position", bodyVertexBuffer);
        shader.setAttribute("a_colour", bodyColourBuffer);
        glDrawArrays(GL_TRIANGLES, 0, bodyVertices.length);

        shader.setAttribute("a_position", baseVertexBuffer);
        shader.setAttribute("a_colour", baseColourBuffer);
        glDrawArrays(GL_TRIANGLES, 0, baseVertices.length);

        shader.setAttribute("a_position", leftLegVertexBuffer);
        shader.setAttribute("a_colour", leftLegColourBuffer);
        glDrawArrays(GL_TRIANGLES, 0, leftLegVertices.length);

        shader.setAttribute("a_position", rightLegVertexBuffer);
        shader.setAttribute("a_colour", rightLegColourBuffer);
        glDrawArrays(GL_TRIANGLES, 0, rightLegVertices.length);
        
        if (thrustUp || thrustLeft || thrustRight) {
            Matrix4f flameMatrix = new Matrix4f(matrix);

            float flameScaleY = 1.0f;
            if (thrustUp && (thrustLeft || thrustRight)) {
                flameScaleY = 1.8f;
            } else if (thrustUp) {
                flameScaleY = 1.5f;
            } else {
                flameScaleY = 1.0f;
            }

            if (thrustLeft) {
                flameMatrix.rotateZ((float)Math.toRadians(-25));
            } else if (thrustRight) {
                flameMatrix.rotateZ((float)Math.toRadians(25));
            }

            flameMatrix.scale(1.0f, flameScaleY, 1.0f);

            shader.setUniform("u_mvpMatrix", flameMatrix);
            shader.setAttribute("a_position", flameVertexBuffer);
            shader.setAttribute("a_colour", flameColourBuffer);
            glDrawArrays(GL_TRIANGLES, 0, flameVertices.length);

            shader.setUniform("u_mvpMatrix", matrix);
        }
    }
}