package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Lamp extends Entity {

	private Vector3f attenuation = new Vector3f(1,0,0);
	private Entity lampEntity;
	public Lamp(TexturedModel model, int textureIndex, Vector3f position, float rotx, float roty, float rotz,
			float scale, Vector3f attenuation) {
		super(model, textureIndex, position, rotx, roty, rotz, scale);
		lampEntity = new Entity(model,position, rotx,roty,rotz,scale);
		this.attenuation = attenuation;

	}
	public Entity getLampEntity() {
		return lampEntity;
	}
	/*public Light getLampLight() {
		return new Light()
	}*/
	

}
