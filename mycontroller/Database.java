/** 
 * Project by:
 * Haobei Ma     837734
 * David Pham	 756598
 * Shaobin Zhao  776298
 */


package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;


//A database class used to store all the information needed for the traversal of the map
class Database {
	//An array for the keys required to find
	private Coordinate[] keys;
	
	//A graph to show the relationships between each node (Nodes are each point on the map)
	private ArrayList<Node> graph = new ArrayList<Node>();
	
	//Boolean variable that checks if the exit has been found
	private boolean exitFound;
	
	//Constructor
	public Database(HashMap<Coordinate, MapTile> map, int keys) {
		
		//Initialise the array of keys
		this.keys = new Coordinate[keys-1];
		//Exit not found yet
		this.exitFound = false;
		
		//Add all the traversable tiles into the graph
		int firstX, firstY, secondX, secondY, diffX, diffY, total;
		for (Coordinate place: map.keySet()) {
			MapTile value = map.get(place);
			if (!value.isType(MapTile.Type.WALL)){
				graph.add(new Node(place, value));
			}
		}
		//Create neighbouring nodes
		for (Node node1: graph) {
			for (Node node2: graph) {
				//Check if node2 is right next to node 1
			    firstX = node1.coordinate.x;
				firstY = node1.coordinate.y;
				secondX = node2.coordinate.x;
				secondY = node2.coordinate.y;
				diffX = Math.abs(firstX - secondX);
				diffY = Math.abs(firstY - secondY);
				total = diffX + diffY;
				//If it is, add it to list of neighbours
				if (total == 1) {
					node1.addneighbours(node2);
				}
				
			}
		}
	}
	
	//Update the graph according to the view generated from the car.
	public void update(HashMap<Coordinate, MapTile> map) {
		
		//Iterate through the view
		for (Coordinate place: map.keySet()) {
			MapTile value = map.get(place);
			
			//Iterate through the graph
			for (Node node : graph) {
				
				//Check if the node corresponds with the tile
				if (node.coordinate.equals(place)) {
					//Update the node
					node.explored = true;
					node.tileType = value;
					
					//Check if exit is found
					if (value.isType(MapTile.Type.FINISH)) {
						exitFound = true;
					}
					
					//Check if the tile contains a key
					if (value instanceof TrapTile) {
						if (((TrapTile)value).getTrap().equals("lava")) {
							int keyCheck = ((LavaTrap)value).getKey();
							if (keyCheck != 0){
								keys[keyCheck-1] = place;
							}
						}
					}
				}
			}
		}
	}
	
	//Get the node from a coordinate
	public Node getNode(Coordinate coordinate) {
		for (Node node: graph) {
			if (node.coordinate.equals(coordinate)) {
				return node;
			}
		}
		return null;
	}

	//Reset the information of the nodes after doing a breadth first search
	public void resetBFS() {
		for (Node node: graph) {
			node.visited = false;
			node.pathway.clear();
			node.pathway.add(node.coordinate);
			
		}
	}
	
	//Loop through the graph and get the first tile that hasn't been explored
	public Coordinate getFirstUnexplored() {
		for (Node node: graph) {
			if (!node.explored) {
				return node.coordinate;
			}
		}
		return null;
	}
	
	//Check if the key has been found
	//Called in MyAIController
	public boolean checkKeyFound(int key) {
		if (key == 0) {
			return isExitFound();
		}
		if (keys[key-1] == null) {
			return false;
		}
		return true;
	}
	
	
	//Get the coordinate of the specific key
	public Coordinate getKeyCoordinates(int key) {
		return keys[key-1];
	}
	

	
	//Getter for exitFound
	public boolean isExitFound() {
		return exitFound;
	}
	
}
