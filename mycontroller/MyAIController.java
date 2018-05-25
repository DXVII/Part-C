package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
public class MyAIController extends CarController {
	
	//The speed the car will be at when rotating
	private final float rotatingSpeed = (float) 0.1;
	
	//Whether the car should avoid lava
	private boolean avoidLava = true;
	
	//Whether the health tile has been found
	private boolean healthTileFound = false;
	
	//A database of the map
	private Database database;

	// Car Speed to move at
	private final float CAR_SPEED = 4;
	
	//Whether the car just found a key
	private boolean keyJustFound = false;
	
	//Tracks the number of keys before the update
	private int keyTracker;
	
	//Whether the car should be healing
	private boolean heal = false;
	
	//What health the car should be below before actively searching for a health tile
	//Inaccurate, best implementation would be a constant
	private int healththreshold = 40;
	
	//Constructor
	public MyAIController(Car car) {
		super(car);
		//Create new database with existing road and wall tiles
		this.database = new Database(new HashMap<Coordinate,MapTile>(getMap()), car.getKey());
		this.keyTracker = car.getKey();
	}
	
	@Override
	public void update(float delta) {
		//Get the current key
		int currKey = car.getKey() - 1;
		//Check if all keys are found and the exit is found
		if (currKey == 0 & database.isExitFound()) {
			//If it is, ignore health tiles and go straight to exit
			
			//Inaccurate, can be further improved (See report)
			healththreshold = 0;
		}
		
		//The pathway required to get to a destination
		ArrayList<Coordinate> pathway;
		
		//Get the view of the car
 		HashMap<Coordinate,MapTile> view = car.getView();
 		//Check if health tile is found
 		for (MapTile tile: view.values()) {
 			if (tile.isType(MapTile.Type.TRAP)){
 				if (((TrapTile)tile).getTrap().equals("health")) {
 					healthTileFound = true;
 				}
 			}
 		}
 		
 		//Update the database
		database.update(view);
		
		//Check if the car just found a key
		if (keyTracker != car.getKey()) {
			keyTracker = car.getKey();
			keyJustFound = true;
		}
		
		//If the key was just found, move off the lava before doing anything else
		if (keyJustFound) {
			pathway = bfSearchTile("no lava");
			if (pathway.size() == 1) {
				keyJustFound = false;
			}
			
		}
		else {
			
			//Check if the car needs healing
			if (car.getHealth()< healththreshold & healthTileFound) {
				this.heal = true;
			}
			
			//Get the current tile the car is on
			Node currNode = database.getNode(new Coordinate(car.getPosition()));
			
			//If the car is already on a health tile, start to heal
			if (currNode.tileType.isType(MapTile.Type.TRAP)){
				if (((TrapTile)currNode.tileType).getTrap().equals("health")) {
					this.heal = true;
				}
			}
			
			//If the car should be healing
			if (this.heal) {
				//Check if the health is less than 100
				if (car.getHealth() < 100) {
					//Keep healing if it is
					avoidLava = true;
					pathway = bfSearchTile("health");
				}
				else {
					//Stop healing and keep searching
					//If all keys are found, pathway will be invalid and the next update
					//will simply start finding the next key/exit
					this.heal = false;
					this.avoidLava = true;
					pathway = bfSearchKeys();
				}
				
			}
			
			//Check if the next target is found
			else if (database.checkKeyFound(currKey)) {
				
				//Just the exit left
				if (currKey == 0) {
					if (database.isExitFound()){
						//Find the path to the finish tile
						pathway = bfSearchTile("FINISH");
					}
					else {
						//Try to avoid lava while looking for the exit
						this.avoidLava = true;
						pathway = bfSearchKeys();
					}
				}
				else {
					//Move towards the key
					avoidLava = false;
					pathway = bfSearch(database.getKeyCoordinates(currKey));
				}
			}
			else {
				//Current key still not found, try to avoid lava while searching for it
				this.avoidLava = true;
				pathway = bfSearchKeys();
			}
		}
		
		//Only occurs when there is no valid path or while healing
		//Stop avoiding lava now if first case to find more keys
		if (pathway.size() == 1) {
			avoidLava = false;
		}
		else {
			//Travel the pathway
			travelPath(pathway, delta);
		}
		
	}
	
	
	//Method called used to generate movement according to the pathway created
	private void travelPath(ArrayList<Coordinate> path, float delta) {
		//Current position
		Coordinate currentPosition = path.get(0);
		
		//Tile to go to
		Coordinate nextPosition = path.get(1);
		
		//Get the direction
		int diffX = nextPosition.x - currentPosition.x;
		int diffY = nextPosition.y - currentPosition.y;
		
		//Move accordingly
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

	
	//Moves the car North
	private void moveNorth(float delta) {
		
		//Get the angle
		float currentAngle = car.getAngle();
		if (currentAngle == 90) {
			//If its already facing north, move forward
			if(getSpeed() <= CAR_SPEED){
				applyForwardAcceleration();
			}

			
		}
		
		//Rotate whichever way is faster
		else if (currentAngle <= 90 | currentAngle > 270){
			rotateLeft(delta);
		}
		else {
			rotateRight(delta);
		}
	}
	
	//Moves the car South
	private void moveSouth(float delta) {
		
		//Get the angle
		float currentAngle = car.getAngle();
		if (currentAngle == 270 ) {
			//If its already facing South, move forward
			if(getSpeed() <= CAR_SPEED){
				applyForwardAcceleration();
			}

			
		}

		//Rotate whichever way is faster
		else if (currentAngle <= 90 | currentAngle >= 270){
			rotateRight(delta);
		}
		else {
			rotateLeft(delta);
		}
	}
	
	//Moves the car East
	private void moveEast(float delta) {
		
		//Get the angle
		float currentAngle = car.getAngle();
		if (currentAngle == 0) {
			if(getSpeed() <= CAR_SPEED ){
				//If its already facing East, move forward
				applyForwardAcceleration();		
			}
		}

		//Rotate whichever way is faster
		else if (currentAngle <= 180){
			rotateRight(delta);
		}
		else {
			rotateLeft(delta);
		}
	}
	
	//Moves the car West
	private void moveWest(float delta) {
		
		//Get the angle
		float currentAngle = car.getAngle();
		if (currentAngle == 180) {
			if(getSpeed() <= CAR_SPEED ){
				//If its already facing West, move forward
				applyForwardAcceleration();
			}		
		}

		//Rotate whichever way is faster
		else if (currentAngle > 180){
			rotateRight(delta);
		}
		else {
			rotateLeft(delta);
		}
	}
	
	//Rotates the car right
	private void rotateRight(float delta) {
		
		//Reverse the car to slow it down to rotating speed
		//Or reverse the car if moving it forward makes it stuck
		if (getSpeed() > this.rotatingSpeed | getSpeed() == 0) {
			applyReverseAcceleration();
			turnLeft(delta);

		}
		
		else {
			turnRight(delta);
			applyForwardAcceleration();
		}

		
	}
	
	//Rotates the car left
	private void rotateLeft(float delta) {
		
		//Reverse the car to slow it down to rotating speed
		//Or reverse the car if moving it forward makes it stuck
		if (getSpeed()>this.rotatingSpeed | getSpeed() == 0) {	
			applyReverseAcceleration();
			turnRight(delta);	
		}
		else {
			turnLeft(delta);
			applyForwardAcceleration();
		}
	}
	

	


	//A breadth first search with the input of the destination coordinate
	//Used mainly to get to keys that have been found
	private ArrayList<Coordinate> bfSearch(Coordinate dest){
		//Generate the path
		ArrayList<Coordinate> path;
		
		//Create a queue
		ArrayList<Coordinate> queue = new ArrayList<Coordinate>();
		
		//Get the current position
		Coordinate currPosition = new Coordinate(getPosition());
		
		//Add it to the queue
		queue.add(currPosition);
		
		//Get the node of the coordinate
		Node currNode = database.getNode(currPosition);
		
		//Node visited, set it to true
		currNode.visited = true;
		
		//Loop through while destination isn't found
		while (!currPosition.equals(dest)) {
			//Get the next node in the queue
			currNode = database.getNode(currPosition);
			
			//Sort the neighbours, with lava tiles being searched last
			//Used in attempt to avoid lava
			
			//Inaccurate, can be further improved (See report)
			for (Node neighbour: database.sortNeighbours(currNode)) {
				
				//Add the node if it isn't visited
				if (!neighbour.visited) {
					//Create the copy of the pathway to get to the current node
					path = new ArrayList<Coordinate>(currNode.pathway);
					//Include the new node. This is now the pathway to get to the
					//new node from current position
					path.add(neighbour.coordinate);
					//Add it to the pathway
					neighbour.pathway = path;
					//Put the new coordinate into the queue
					queue.add(neighbour.coordinate);
					//Neighbour has now been visited
					neighbour.visited = true;
				}
			}
			//Remove the first item
			queue.remove(0);
			//Get the next position in the queue
			currPosition = queue.get(0);
		}
		
		//Create a copy and return the pathway
		ArrayList<Coordinate> destination = new ArrayList<Coordinate>(database.getNode(dest).pathway);
		
		//Reset the nodes so that they can be used again for future searches
		database.resetBFS();
		return destination;
	}
	
	
	//Method used to search for keys using breadth first search
	private ArrayList<Coordinate> bfSearchKeys(){
		
		//Pathway to get to a specific node
		ArrayList<Coordinate> path;
		
		//Queue of coordinates to check in breadth first search
		ArrayList<Coordinate> queue = new ArrayList<Coordinate>();
		
		//Get current position
		Coordinate currPosition = new Coordinate(getPosition());

		//Add it to queue
		queue.add(currPosition);
		
		//Get the node. 
		Node currNode = database.getNode(currPosition);
		currNode.visited = true;
		
		//Look for tiles that haven't been seen by getView 
		while (currNode.explored) {
			
			//Sort the neighbours by the current orientation of the car
			//Used to avoid as much turning as possible
			for (Node neighbour: database.sortNeighbours(currNode, car.getOrientation())) {
				//Check if lava should be avoided
				if (avoidLava) {
					//Ignore it if it is a lava tile
					if (neighbour.tileType.isType(MapTile.Type.TRAP)){
						if (((TrapTile)neighbour.tileType).getTrap().equals("lava")){
							neighbour.visited = true;
						}
					}
					//Check if the neighbour has been visited
					else if (!neighbour.visited) {
						//Create the pathway to get to the neighbour
						path = new ArrayList<Coordinate>(currNode.pathway);
						path.add(neighbour.coordinate);
						neighbour.pathway = path;
						queue.add(neighbour.coordinate);
						neighbour.visited = true;
					}
				}
				//Similar case. Lava isn't avoided in this scenario
				else if (!neighbour.visited) {
					path = new ArrayList<Coordinate>(currNode.pathway);
					path.add(neighbour.coordinate);
					neighbour.pathway = path;
					queue.add(neighbour.coordinate);
					neighbour.visited = true;
					
				}
			}
			
			queue.remove(0);
			//Queue is empty is there is no more possible movement
			//due to trying to avoid lava
			if (queue.isEmpty()){
				//Stop avoiding lava now
				avoidLava = false;
				//Return the same method but this time without avoiding lava
				database.resetBFS();
				return bfSearchKeys();
			}
			currPosition = queue.get(0);
			currNode = database.getNode(currPosition);
		}
		//Get the pathway to the destination
		ArrayList<Coordinate> destination = new ArrayList<Coordinate>(currNode.pathway);
		//Reset the database for future searches
		database.resetBFS();
		return destination;
	}

	//Check if the node is a particular type
	//Can also be used to check other tiles in the future
	private boolean checkType(String type, Node node) {
		//Check finish
		if (type.equals("FINISH")) {		
			if (node.tileType.getType().equals((MapTile.Type.FINISH))){
				
				return true;
			}
		}
		//Check type of trap
		else if (node.tileType instanceof TrapTile){
			if (((TrapTile)node.tileType).getTrap().equals(type)){
				return true;
			}
			else {
				//Check if avoiding lava
				if (type.equals("no lava")) {
					if (!((TrapTile)node.tileType).getTrap().equals("lava")) {
						
						return true;
					}
				}
			}
		}
		//Check if avoiding lava and tile is not a trap
		else {
			if (type.equals("no lava")) {
				return true;
			}
		}
		return false;
	}
	
	//Breadth first search to find a specific tile type
	private ArrayList<Coordinate> bfSearchTile(String type){
		//Generate the path
		ArrayList<Coordinate> path;
		
		//Create a queue
		ArrayList<Coordinate> queue = new ArrayList<Coordinate>();
		
		//Initialise the first coordinate
		Coordinate currPosition = new Coordinate(getPosition());
		queue.add(currPosition);
		Node currNode = database.getNode(currPosition);
		currNode.visited = true;
		
		//Keep looping until tile is found
		while (!checkType(type, currNode)) {
			
			//Loop through the neighbours, sorted by the car's orientation
			for (Node neighbour: database.sortNeighbours(currNode, car.getOrientation())) {
				//Add the neighbour to the queue is it hasn't been visited
				if (!neighbour.visited) {
					path = new ArrayList<Coordinate>(currNode.pathway);
					path.add(neighbour.coordinate);
					neighbour.pathway = path;
					queue.add(neighbour.coordinate);
					neighbour.visited = true;
					
				}
			}
			
			queue.remove(0);
			//Queue will be empty if the tile does not exist. In which case, the car will stop
			//Current scenario only occurs if health tile or exit has not been found.
			//Boolean values such as exitFound and healthTileFound have been added to guard against this case
			//Will never occur
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
