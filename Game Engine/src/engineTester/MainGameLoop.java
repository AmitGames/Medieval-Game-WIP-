package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import randerEngine.DisplayManager;
import randerEngine.Loader;
import randerEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;


public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		
		  ModelData treeData = OBJFileLoader.loadOBJ("tree");
		  ModelData fernData = OBJFileLoader.loadOBJ("fern");
		  ModelData lowPolyTreeData = OBJFileLoader.loadOBJ("lowPolyTree");
		  ModelData grassData = OBJFileLoader.loadOBJ("grassModel");
		  ModelData lampData = OBJFileLoader.loadOBJ("lamp");
		  //ModelData boxData = OBJFileLoader.loadOBJ("box");
		  
		  /***************Terrain Texture Stuff**************/
		  TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass")); 
		  TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		  TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		  TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		  
		  TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		  TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		  
		
		  /**************************************************/
		  /*********************Gui Textures*****************/
		  List<GuiTexture> guis = new ArrayList<GuiTexture>();
		  GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.8f,0.9f), new Vector2f(0.35f,0.35f));
		  guis.add(gui);
		  GuiRenderer guiRenderer = new GuiRenderer(loader);
		  
		  /*****************Raw Models***********************/
		  RawModel treeModel = loader.loadToVAO
		  (treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
		  RawModel fernModel = loader.loadToVAO
		  (fernData.getVertices(), fernData.getTextureCoords(), fernData.getNormals(), fernData.getIndices());
		  RawModel lowPolyTreeModel = loader.loadToVAO
		  (lowPolyTreeData.getVertices(), lowPolyTreeData.getTextureCoords(), lowPolyTreeData.getNormals(), lowPolyTreeData.getIndices());
		  RawModel grassModel = loader.loadToVAO
		  (grassData.getVertices(), grassData.getTextureCoords(), grassData.getNormals(), grassData.getIndices());
		  RawModel lampModel = loader.loadToVAO(lampData.getVertices(),lampData.getTextureCoords(),lampData.getNormals(),lampData.getIndices());
		  //RawModel boxModel = loader.loadToVAO(boxData.getVertices(), boxData.getTextureCoords(), boxData.getNormals(), boxData.getIndices());
		  /**********************Textures*************************/
		  ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		  fernTextureAtlas.setNumberOfRows(2);
		  
		  TexturedModel lampTexture = new TexturedModel(lampModel,new ModelTexture(loader.loadTexture("lamp")));
		  TexturedModel treeTexture = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
		  TexturedModel fernTexture = new TexturedModel(fernModel, fernTextureAtlas);
		  TexturedModel lowPolyTreeTexture = new TexturedModel(lowPolyTreeModel, new ModelTexture(loader.loadTexture("lowPolyTree")));
		  //TexturedModel boxTexture = new TexturedModel(boxModel, new ModelTexture(loader.loadTexture("box")));
		  TexturedModel grassTexture = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
		  
		  grassTexture.getTexture().setHasTransparency(true);
		  grassTexture.getTexture().setUseFakeLighting(true);
		  fernTexture.getTexture().setHasTransparency(true);
		  
		  ModelTexture textureTree = treeTexture.getTexture();
		  textureTree.setShineDamper(1);
		  textureTree.setReflectivity((float)0.1);
		  ModelTexture textureLowPolyTree = lowPolyTreeTexture.getTexture();
		  textureLowPolyTree.setShineDamper(3);
		  textureLowPolyTree.setReflectivity((float)0.2);
		  Random random = new Random();
		  List<Entity> entities = new ArrayList<Entity>();
		  List<Terrain> terrains = new ArrayList<Terrain>();
		  Terrain terrain1 = new Terrain(0,-1,loader, texturePack, blendMap, "heightmap");
		  Terrain terrain2 = new Terrain(-1,-1,loader, texturePack, blendMap, "heightmap");
		  terrains.add(terrain1);
		  terrains.add(terrain2);
		  
		  List<Light> lights = new ArrayList<Light>();
		  lights.add(new Light(new Vector3f(0,1000,-7000), new Vector3f(0.7f,0.7f,0.7f)));
		  lights.add(new Light(new Vector3f(185,10,-293), new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
		  lights.add(new Light(new Vector3f(370,17, -300), new Vector3f(0,2,2),new Vector3f(1,0.01f,0.002f)));
		  lights.add(new Light(new Vector3f(293,7,-305), new Vector3f(2,2,0),new Vector3f(1,0.01f,0.002f)));
		  
		  entities.add(new Entity(lampTexture, new Vector3f(185,-4.7f,-293),0,0,0,1));
		  entities.add(new Entity(lampTexture, new Vector3f(370,4.2f,-300),0,0,0,1));
		  entities.add(new Entity(lampTexture, new Vector3f(293,-6.8f,-305),0,0,0,1));
		  
		  for (int i = 0; i<100; i++) {
			  float x = random.nextFloat() * 800;
			  float z = random.nextFloat() * -600;
			  float y = checkTerrainLocation(terrains, x,z);
			  entities.add(new Entity(treeTexture, new Vector3f(x,y,z), 0,random.nextFloat() * 1-0,0,7));
			  
		  }
		  for (int i = 0; i<100; i++) {
			  float x = random.nextFloat() * 800;
			  float z = random.nextFloat() * -600;
			  float y = checkTerrainLocation(terrains, x,z);
			  entities.add(new Entity(lowPolyTreeTexture, new Vector3f(x,y,z), 0,random.nextFloat() * 1-0,0,1));
			  
		  }
		  
		  /*for (int i = 0; i<200; i++) {
			  float x = random.nextFloat() * 800-400;
			  float z = random.nextFloat() * -600;
			  float y = terrain.getHeightOfTerrain(x, z);
			  entities.add(new Entity(grassTexture, new Vector3f(x,y-1,z), 0,random.nextFloat() * 1-0,0,3));
			  
		  }*/
		  for (int i = 0; i<200; i++) {
			  float x = random.nextFloat() * 800;
			  float z = random.nextFloat() * -600;
			  float y = checkTerrainLocation(terrains, x,z);
			  entities.add(new Entity(fernTexture, random.nextInt(4), new Vector3f(x,y,z), 0,random.nextFloat() * 1-0,0,1));
			  
		  }
		  for (int i = 0; i<100; i++) {
			  float x = random.nextFloat() * -800;
			  float z = random.nextFloat() * -600;
			  float y = checkTerrainLocation(terrains, x,z);
			  entities.add(new Entity(treeTexture, new Vector3f(x,y,z), 0,random.nextFloat() * 1-0,0,7));
			  
		  }
		  for (int i = 0; i<100; i++) {
			  float x = random.nextFloat() * 800;
			  float z = random.nextFloat() * -600;
			  float y = checkTerrainLocation(terrains, x,z);
			  entities.add(new Entity(lowPolyTreeTexture, new Vector3f(x,y,z), 0,random.nextFloat() * 1-0,0,1));
			  
		  }
		  
		  /*for (int i = 0; i<200; i++) {
			  float x = random.nextFloat() * 800-400;
			  float z = random.nextFloat() * -600;
			  float y = terrain.getHeightOfTerrain(x, z);
			  entities.add(new Entity(grassTexture, new Vector3f(x,y-1,z), 0,random.nextFloat() * 1-0,0,3));
			  
		  }*/
		  for (int i = 0; i<200; i++) {
			  float x = random.nextFloat() * 800;
			  float z = random.nextFloat() * -600;
			  float y = checkTerrainLocation(terrains, x,z);
			  entities.add(new Entity(fernTexture, random.nextInt(4), new Vector3f(x,y,z), 0,random.nextFloat() * 1-0,0,1));
			  
		  }
		  
		  
		  

		  
		  
		  MasterRenderer renderer = new MasterRenderer(loader);
		  ModelData bunnyData = OBJFileLoader.loadOBJ("person");
		  RawModel bunnyModel= loader.loadToVAO
				  (bunnyData.getVertices(), bunnyData.getTextureCoords(), bunnyData.getNormals(), bunnyData.getIndices());
		  TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("playerTexture")));
		  Player player = new Player(stanfordBunny, new Vector3f(100, 0, -200), 0,180,0,1.0f);
		  Camera camera = new Camera(player);
		  
		  MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix());
		  
		
		while(!Display.isCloseRequested()) {
			
			
			
			player.move(terrains);
			camera.move();
			picker.update();
			//System.out.println(picker.getCurrentRay());
			renderer.processEntity(player);
			renderer.processTerrain(terrain1);
			renderer.processTerrain(terrain2);

			for(Entity entity:entities) {
				renderer.processEntity(entity);
			}
			//renderer.processEntity(dragonEntity);
			renderer.render(lights, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
			
		}
		
		renderer.cleanup();
		loader.cleanUp();
		guiRenderer.cleanUp();
		DisplayManager.closeDisplay();

	}
	public static float checkTerrainLocation(List<Terrain> terrains, float x, float z) {
		int xCoord =  (int) Math.floor((x/800f));
		int zCoord =  (int) Math.floor((z/800f));
		for(Terrain terrain:terrains) {
			if((((int)(terrain.getX()/800)) == xCoord) && (((int)(terrain.getZ()/800)) == zCoord)) {
				return terrain.getHeightOfTerrain(x, z);
			}
		}
		return 0;
	}

}
