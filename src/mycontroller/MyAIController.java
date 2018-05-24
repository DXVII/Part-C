package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;
import tiles.HealthTrap;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
public class MyAIController extends CarController {
	private float rotatingSpeed = (float) 0.1;
	private boolean movingForward = true;
	private boolean avoidLava = true;
	private boolean healthTileFound = false;
	private Database database;
	// Car Speed to move at
	private final float CAR_SPEED = (float) 4;
	private boolean keyJustFound = false;
	private int keyTracker;
	private boolean heal = false;
	private int healththreshold = 40;
	public MyAIController(Car car) {
		super(car);
		this.database = new Database(new HashMap<Coordinate,MapTile>(getMap()), car.getKey());
		this.keyTracker = car.getKey();
	}
	
	@Override
	public void update(float delta) {
		int currKey = car.getKey() - 1;
		if (currKey == 0 & database.exitFound) {
			healththreshold = 0;
		}
		ArrayList<Coordinate> pathway;
 		HashMap<Coordinate,MapTile> view = car.getView();
 		for (MapTile tile: view.values()) {
 			if (tile.isType(MapTile.Type.TRAP)){
 				if (((TrapTile)tile).getTrap().equals("health")) {
 					healthTileFound = true;
 				}
 			}
 		}
		database.update(view);
		if (keyTracker != car.getKey()) {
			keyTracker = car.getKey();
			keyJustFound = true;
		}
		if (keyJustFound) {
			pathway = bfSearchTile("no lava");
			if (pathway.size() == 1) {
				keyJustFound = false;
			}
			
		}
		else {
			
			
			
			
		if (car.getHealth()< healththreshold & healthTileFound) {
			this.heal = true;
		}
		Node currNode = database.getNode(new Coordinate(car.getPosition()));
		if (currNode.tileType.isType(MapTile.Type.TRAP)){
			if (((TrapTile)currNode.tileType).getTrap().equals("health")) {
				this.heal = true;
			}
		}
		if (this.heal) {
			if (car.getHealth() < 100) {
				avoidLava = true;
				pathway = bfSearchTile("health");
			}
			else {
				this.heal = false;
				this.avoidLava = true;
				pathway = bfSearchKeys();
			}
			
		}
		else if (database.checkKeyFound(currKey)) {
			if (currKey == 0) {
				if (database.exitFound){
					pathway = bfSearchTile("FINISH");
				}
				else {
					this.avoidLava = true;
					pathway = bfSearchKeys();
				}
			}
			else {
				avoidLava = false;
				pathway = bfSearch(database.getKeyCoordinates(currKey));
			}
		}
		else {
			this.avoidLava = true;
			pathway = bfSearchKeys();
		}
		}
		
		if (pathway.size() == 1) {
			avoidLava = false;
		}
		else {
			travelPath(pathway, delta);
		}
		
	}
	
	

	private void travelPath(ArrayList<Coordinate> path, float delta) {
		Coordinate currentPosition = path.get(0);
		Coordinate nextPosition = path.get(1);
		int diffX = nextPosition.x - currentPosition.x;
		int diffY = nextPosition.y - currentPosition.y;

		if (diffY == 0) {
			if (diffX == 1) {
				moveEast(delta);
			}
			else{
				
				moveWest(delta);
			}
		}
		else {
			if (diffY == 1) {
				moveNorth(delta);
			}
			else {
				moveSouth(delta);
			}
		}
		
	}

	private void moveNorth(float delta) {

		float currentAngle = car.getAngle();
		if (currentAngle == 90) {
			if(getSpeed() <= CAR_SPEED){
				applyForwardAcceleration();
				movingForward = true;
			}

			
		}
		else if (currentAngle <= 90 | currentAngle > 270){
			rotateLeft(delta);
		}
		else {
			rotateRight(delta);
		}
	}
	private void moveSouth(float delta) {
		float currentAngle = car.getAngle();
		if (currentAngle == 270 ) {
			if(getSpeed() <= CAR_SPEED){
				applyForwardAcceleration();
				movingForward = true;
			}

			
		}

		else if (currentAngle <= 90 | currentAngle >= 270){
			rotateRight(delta);
		}
		else {
			rotateLeft(delta);
		}
	}
	private void moveEast(float delta) {
		
		float currentAngle = car.getAngle();
		if (currentAngle == 0) {
			if(getSpeed() <= CAR_SPEED ){
				applyForwardAcceleration();		
				movingForward = true;
			}
		}

		else if (currentAngle <= 180){
			rotateRight(delta);
		}
		else {
			rotateLeft(delta);
		}
	}
	private void moveWest(float delta) {
		float currentAngle = car.getAngle();
		if (currentAngle == 180) {
			if(getSpeed() <= CAR_SPEED ){
				applyForwardAcceleration();
				movingForward = true;
			}		
		}

		else if (currentAngle > 180){
			rotateRight(delta);
		}
		else {
			rotateLeft(delta);
		}
	}
	private void rotateRight(float delta) {
		if (getSpeed() > this.rotatingSpeed | getSpeed() == 0) {
			applyReverseAcceleration();
			turnLeft(delta);

		}
		else {
			turnRight(delta);
			applyForwardAcceleration();
			movingForward = true;
		}

		
	}
	private void rotateLeft(float delta) {
		if (getSpeed()>this.rotatingSpeed | getSpeed() == 0) {	
			applyReverseAcceleration();
			turnRight(delta);	
		}
		else {
			turnLeft(delta);
			applyForwardAcceleration();
			movingForward = true;
		}
	}
	

	



	private ArrayList<Coordinate> bfSearch(Coordinate dest){
		ArrayList<Coordinate> path;
		ArrayList<Coordinate> queue = new ArrayList<Coordinate>();
		Coordinate currPosition = new Coordinate(getPosition());
		queue.add(currPosition);
		Node currNode = database.getNode(currPosition);
		currNode.visited = true;
		while (!currPosition.equals(dest)) {
			currNode = database.getNode(currPosition);
			for (Node neighbour: database.sortNeighbours(currNode)) {
				if (!neighbour.visited) {
					path = new ArrayList<Coordinate>(currNode.pathway);
					path.add(neighbour.coordinate);
					neighbour.pathway = path;
					queue.add(neighbour.coordinate);
					neighbour.visited = true;
				}
			}
			
			queue.remove(0);
			currPosition = queue.get(0);
		}
		ArrayList<Coordinate> destination = new ArrayList<Coordinate>(database.getNode(dest).pathway);
		database.resetBFS();
		return destination;
	}
	
	private ArrayList<Coordinate> bfSearchKeys(){
		ArrayList<Coordinate> path;
		ArrayList<Coordinate> queue = new ArrayList<Coordinate>();
		Coordinate currPosition = new Coordinate(getPosition());
		queue.add(currPosition);
		Node currNode = database.getNode(currPosition);
		currNode.visited = true;
		while (currNode.explored) {
			database.sortNeighbours(currNode, car.getOrientation());
			
			
			
			for (Node neighbour: database.sortNeighbours(currNode, car.getOrientation())) {
				if (avoidLava) {
					if (neighbour.tileType.isType(MapTile.Type.TRAP)){
						if (((TrapTile)neighbour.tileType).getTrap().equals("lava")){
							neighbour.visited = true;
						}
					}
					else if (!neighbour.visited) {
						path = new ArrayList<Coordinate>(currNode.pathway);
						path.add(neighbour.coordinate);
						neighbour.pathway = path;
						queue.add(neighbour.coordinate);
						neighbour.visited = true;
					}
				}
				
				else if (!neighbour.visited) {
					path = new ArrayList<Coordinate>(currNode.pathway);
					path.add(neighbour.coordinate);
					neighbour.pathway = path;
					queue.add(neighbour.coordinate);
					neighbour.visited = true;
					
				}
			}
			
			queue.remove(0);
			if (queue.isEmpty()){
				avoidLava = false;
				queue.add(currPosition);
				database.resetBFS();
				return bfSearchKeys();
			}
			currPosition = queue.get(0);
			currNode = database.getNode(currPosition);
		}
		ArrayList<Coordinate> destination = new ArrayList<Coordinate>(currNode.pathway);
		database.resetBFS();
		return destination;
	}

	private boolean checkType(String type, Node node) {
		if (type.equals("FINISH")) {
			
			if (node.tileType.getType().equals((MapTile.Type.FINISH))){
				
				return true;
			}
		}

		else if (node.tileType instanceof TrapTile){
			if (((TrapTile)node.tileType).getTrap().equals(type)){
				return true;
			}
			else {
				if (type.equals("no lava")) {
					if (!((TrapTile)node.tileType).getTrap().equals("lava")) {
						
						return true;
					}
				}
			}
		}
		else {
			if (type.equals("no lava")) {
				return true;
			}
		}
		return false;
	}
	
	private ArrayList<Coordinate> bfSearchTile(String type){
		ArrayList<Coordinate> path;
		ArrayList<Coordinate> queue = new ArrayList<Coordinate>();
		Coordinate currPosition = new Coordinate(getPosition());
		queue.add(currPosition);
		Node currNode = database.getNode(currPosition);
		currNode.visited = true;
		while (!checkType(type, currNode)) {
			
			for (Node neighbour: database.sortNeighbours(currNode, car.getOrientation())) {
				if (!neighbour.visited) {
					path = new ArrayList<Coordinate>(currNode.pathway);
					path.add(neighbour.coordinate);
					neighbour.pathway = path;
					queue.add(neighbour.coordinate);
					neighbour.visited = true;
					
				}
			}
			
			queue.remove(0);
			if (queue.isEmpty()){
				avoidLava = false;
				queue.add(currPosition);
				database.resetBFS();
				return queue;
			}
			currPosition = queue.get(0);
			currNode = database.getNode(currPosition);
		}
		ArrayList<Coordinate> destination = new ArrayList<Coordinate>(currNode.pathway);
		database.resetBFS();
		return destination;
	}
}
