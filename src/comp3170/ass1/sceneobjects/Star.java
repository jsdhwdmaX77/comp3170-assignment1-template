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

public class Star extends SceneObject {

    private static final String VERTEX_SHADER = "simple_vertex.glsl";
    private static final String FRAGMENT_SHADER = "simple_fragment.glsl";

    private final Shader shader;
    private final Vector4f[] vertices;
    private final int vertexBuffer;
    private final Vector3f colour;

    public Star(float outerRadius, float innerRadius, Vector3f colour) {
        shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
        this.colour = colour;

        vertices = createStarVertices(outerRadius, innerRadius);
        vertexBuffer = GLBuffers.createBuffer(vertices);
    }

    private Vector4f[] createStarVertices(float outerRadius, float innerRadius) {
        Vector4f[] points = new Vector4f[10];

        for (int i = 0; i < 10; i++) {
            float angle = (float) (-Math.PI / 2.0 + i * Math.PI / 5.0);
            float radius = (i % 2 == 0) ? outerRadius : innerRadius;

            points[i] = new Vector4f(
                radius * (float) Math.cos(angle),
                radius * (float) Math.sin(angle),
                0f,
                1f
            );
        }

        Vector4f center = new Vector4f(0f, 0f, 0f, 1f);
        Vector4f[] starVertices = new Vector4f[30]; // 10 triangles × 3 vertices

        for (int i = 0; i < 10; i++) {
            starVertices[i * 3] = center;
            starVertices[i * 3 + 1] = points[i];
            starVertices[i * 3 + 2] = points[(i + 1) % 10];
        }

        return starVertices;
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