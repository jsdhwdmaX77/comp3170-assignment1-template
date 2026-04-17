#version 330 core

in vec4 a_position;
in vec3 a_colour;

uniform mat4 u_mvpMatrix;

out vec3 v_colour;

void main() {
    gl_Position = u_mvpMatrix * a_position;
    v_colour = a_colour;
}