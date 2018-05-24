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
	
	private boolean movingForward = false;
	
	
	private Database database;
	// Car Speed to move at
	private final float CAR_SPEED = (float) 2;

	public MyAIController(Car car) {
		super(car);
		this.database = new Database(new HashMap<Coordinate,MapTile>(getMap()), car.getKey());

	}
	
	Coordinate initialGuess;
	boolean notSouth = true;
	@Override
	public void update(float delta) {
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
	        if (Gdx.input.isKeyPressed(Input.Keys.B)) {
	            applyBrake();
	        }
	        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
	        	applyForwardAcceleration();
	        }
	        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
	        	applyReverseAcceleration();
	        }
	        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
	        	turnLeft(delta);
	        }
	        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
	        	turnRight(delta);
	        }
		}
		else {
		int currKey = car.getKey() - 1;
		ArrayList<Coordinate> pathway;
 		HashMap<Coordinate,MapTile> view = car.getView();
		database.update(view);
		if (database.checkKeyFound(currKey)) {
			if (currKey == 0) {
				if (database.exitFound){
					pathway = bfSearchTile("FINISH");
				}
				else {
					pathway = bfSearchKeys();
				}
			}
			else {
				pathway = bfSearch(database.getKeyCoordinates(currKey));
			}
		}
		else {
			pathway = bfSearchKeys();
		}
		if (pathway.size() == 1) {
			applyBrake();
		}
		else {
			travelPath(pathway, delta);
		}
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
			if(getSpeed() < CAR_SPEED){
				applyForwardAcceleration();
			}
			
		}
		else if (currentAngle <= 90 | currentAngle >= 270){
			rotateLeft(delta);
		}
		else {
			rotateRight(delta);
		}
	}
	private void moveSouth(float delta) {
		float currentAngle = car.getAngle();
		if (currentAngle == 270) {
			if(getSpeed() < CAR_SPEED){
				applyForwardAcceleration();
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
			if(getSpeed() < CAR_SPEED ){
				applyForwardAcceleration();
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
			if(getSpeed() < CAR_SPEED ){
				applyForwardAcceleration();
			}
			
		}

		else if (currentAngle >= 180){
			rotateRight(delta);
		}
		else {
			rotateLeft(delta);
		}
	}
	private void rotateRight(float delta) {
		if (getSpeed() > 1 ) {
			applyReverseAcceleration();
			turnLeft(delta);
			movingForward = false;
		}
		else if (getSpeed() < 1 & getSpeed() != 0){
			turnRight(delta);
			applyForwardAcceleration();
			movingForward = true;
		}
		else {
			if (movingForward) {
				applyReverseAcceleration();
				turnLeft(delta);
			}
			else {
				turnRight(delta);
				applyForwardAcceleration();
			}
		}
		
	}
	private void rotateLeft(float delta) {
		if (getSpeed()>1) {	
			applyReverseAcceleration();
			turnRight(delta);
			movingForward = false;

			
		}
		else if (getSpeed()<1 & getSpeed() != 0){
			turnLeft(delta);
			applyForwardAcceleration();
			movingForward = true;
		}
		else {
			if (movingForward) {
				applyReverseAcceleration();
				turnRight(delta);
			}
			else {
				turnLeft(delta);
				applyForwardAcceleration();
			}
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
			for (Node neighbour: currNode.neighbours) {
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
			
			for (Node neighbour: currNode.neighbours) {
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
			
			for (Node neighbour: currNode.neighbours) {
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
			currNode = database.getNode(currPosition);
		}
		ArrayList<Coordinate> destination = new ArrayList<Coordinate>(currNode.pathway);
		database.resetBFS();
		return destination;
	}
}
