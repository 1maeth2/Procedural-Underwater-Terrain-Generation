package Maths;

import Entities.Camera;

public class Maths {
	public static Matrix4f createTransformationalMatrix(Vector3f translation, float rx, float ry, float rz, float scale){
		
		Matrix4f translationMatrix = Matrix4f.translate(translation.x, translation.y, translation.z);
				
		Matrix4f scaleMatrix = Matrix4f.scale(scale, scale, scale);
		
		Matrix4f rotX = Matrix4f.rotate(rx, 1f, 0f, 0f);
		Matrix4f rotY = Matrix4f.rotate(ry, 0f, 1f, 0f);
		Matrix4f rotZ = Matrix4f.rotate(rz, 0f, 0f, 1f);
		
		Matrix4f rotMatrix = rotY.multiply(rotX.multiply(rotZ));
		
		Matrix4f matrix = translationMatrix.multiply(scaleMatrix.multiply(rotMatrix));
		
		return matrix;
	}
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		Matrix4f rotX = Matrix4f.rotate(camera.getRotation().x, 1f, 0f, 0f);
		Matrix4f rotY = Matrix4f.rotate(camera.getRotation().y, 0f, 1f, 0f);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f translationMatrix = Matrix4f.translate(negativeCameraPos.x,negativeCameraPos.y,negativeCameraPos.z);
		viewMatrix = rotX.multiply(rotY.multiply(translationMatrix));
		
		return viewMatrix;
	}
}
