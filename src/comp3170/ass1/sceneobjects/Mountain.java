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

public class Mountain extends SceneObject {

    private static final String VERTEX_SHADER = "simple_vertex.glsl";
    private static final String FRAGMENT_SHADER = "simple_fragment.glsl";

    private final Shader shader;
    private final Vector4f[] vertices;
    private final int vertexBuffer;
    private final Vector3f colour;

    public Mountain(float baseWidth, float height, float topWidth, Vector3f colour) {
        shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
        this.colour = colour;

        float halfBase = baseWidth / 2.0f;
        float halfTop = topWidth / 2.0f;

        vertices = new Vector4f[] {
            
            new Vector4f(-halfBase, 0.0f, 0.0f, 1.0f),
            new Vector4f( halfBase, 0.0f, 0.0f, 1.0f),
            new Vector4f( halfTop,  height, 0.0f, 1.0f),

            new Vector4f(-halfBase, 0.0f, 0.0f, 1.0f),
            new Vector4f( halfTop,  height, 0.0f, 1.0f),
            new Vector4f(-halfTop,  height, 0.0f, 1.0f)
        };

        vertexBuffer = GLBuffers.createBuffer(vertices);
    }

    @Override
    protected void drawSelf(Matrix4f matrix) {
        shader.enable();
        shader.setAttribute("a_position", vertexBuffer);
        shader.setUniform("u_mvpMatrix", matrix);
        shader.setUniform("u_colour", colour);

        glDrawArrays(GL_TRIANGLES, 0, vertices.length);
    }
}