package entities;

import java.util.List;

import org.lwjgl.input.Keyboard;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import randerEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {
	
	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private boolean isInAir = false;

	public Player(TexturedModel model, Vector3f position, float rotx, float roty, float rotz, float scale) {
		super(model, position, rotx, roty, rotz, scale);
	}
	public void move(List <Terrain> terrains) {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRoty())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRoty())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float terrainHeight = checkTerrainLocation(terrains);
		if(super.getPosition().y<terrainHeight) {
			upwardsSpeed = 0;
			isInAir=false;
			super.getPosition().y = terrainHeight;
		}
	}
	private void jump() {
		if(!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = RUN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
		}else {
			this.currentSpeed = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		}else {
			this.currentTurnSpeed = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}
	public float checkTerrainLocation(List<Terrain> terrains) {
		int xCoord =  (int) Math.floor((this.getPosition().x/800f));
		int zCoord =  (int) Math.floor((this.getPosition().z/800f));
		
		
		for(Terrain terrain:terrains) {
			
			
			
			if((((int)(terrain.getX()/800)) == xCoord) && (((int)(terrain.getZ()/800)) == zCoord)) {
				
				return terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
			}
			
		}
		return 0;
		
	}
	
	

}
