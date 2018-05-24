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

class Database {
	private Coordinate[] keys;
	private ArrayList<Node> graph = new ArrayList<Node>();
	public boolean exitFound;
	public Database(HashMap<Coordinate, MapTile> map, int keys) {
		this.keys = new Coordinate[keys-1];
		exitFound = false;
		int firstX, firstY, secondX, secondY, diffX, diffY, total;
		for (Coordinate place: map.keySet()) {
			MapTile value = map.get(place);
			if (!value.isType(MapTile.Type.WALL)){
				graph.add(new Node(place, value));
			}
		}
		for (Node node1: graph) {
			for (Node node2: graph) {
			    firstX = node1.coordinate.x;
				firstY = node1.coordinate.y;
				secondX = node2.coordinate.x;
				secondY = node2.coordinate.y;
				diffX = Math.abs(firstX - secondX);
				diffY = Math.abs(firstY - secondY);
				total = diffX + diffY;
				if (total == 1) {
					node1.addneighbours(node2);
				}
				
			}
		}
		/*for (Coordinate place: explored.keySet()) {
			boolean value = explored.get(place);
			System.out.print(value);
			System.out.println(place);
		}*/
	}
	public void update(HashMap<Coordinate, MapTile> map) {
		for (Coordinate place: map.keySet()) {
			MapTile value = map.get(place);
			for (Node node : graph) {
				if (node.coordinate.equals(place)) {
					node.explored = true;
					node.tileType = value;
					if (value.isType(MapTile.Type.FINISH)) {
						this.exitFound = true;
					}
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
	public Node getNode(Coordinate coordinate) {
		for (Node node: graph) {
			if (node.coordinate.equals(coordinate)) {
				return node;
			}
		}
		return null;
	}

	public void resetBFS() {
		for (Node node: graph) {
			node.visited = false;
			node.pathway.clear();
			node.pathway.add(node.coordinate);
			node.lavaTiles = 0;
			
		}
	}
	public Coordinate getFirstUnexplored() {
		for (Node node: graph) {
			if (!node.explored) {
				return node.coordinate;
			}
		}
		return null;
	}
	
	public boolean checkKeyFound(int key) {
		if (key == 0) {
			return exitFound;
		}
		if (keys[key-1] == null) {
			return false;
		}
		return true;
	}
	public Coordinate getKeyCoordinates(int key) {
		return keys[key-1];
	}
	/*public void printKeys() {
		boolean hasKeys = false;
		if (exitFound) {
			System.out.println("Exit Found");
		}
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] != null) {
				System.out.println(keys[i]);
				hasKeys = true;
			}
		}
		if (!hasKeys) {
			System.out.println("No keys found");
		}
	}*/
	public void printTile(Coordinate place) {
		for (Node node: graph) {
			if (node.coordinate.equals(place)) {
				System.out.println(node.tileType.getType().equals(MapTile.Type.FINISH));
			}
		}
	}
	
	public ArrayList<Node> sortNeighbours(Node node, WorldSpatial.Direction orientation) {
		int posX;
		int posY;
		if (orientation.equals(WorldSpatial.Direction.NORTH)) {
			posY = 1;
			posX = 0;
		}
		else if (orientation.equals(WorldSpatial.Direction.SOUTH)) {
			posY = -1;
			posX = 0;
		}
		else if (orientation.equals(WorldSpatial.Direction.EAST)) {
			posX = 1;
			posY = 0;
		}
		else {
			posX = -1;
			posY = 0;
		}
		int currX = node.coordinate.x;
		int currY = node.coordinate.y;
		Coordinate straight = new Coordinate(currX+ posX, currY + posY);
		ArrayList<Node> sortedNeighbours = new ArrayList<Node>();
		for (Node neighbour: node.neighbours) {
			if (neighbour.coordinate.equals(straight)) {
				sortedNeighbours.add(0, neighbour);
			}
			else {
				sortedNeighbours.add(neighbour);
			}
		}
		return sortedNeighbours;
	}
	
	
	
	public ArrayList<Node> sortNeighbours(Node node) {

		ArrayList<Node> sortedNeighbours = new ArrayList<Node>();
		for (Node neighbour: node.neighbours) {
			if (neighbour.tileType.isType(MapTile.Type.TRAP)) {
				if (((TrapTile)neighbour.tileType).getTrap().equals("lava")) {
					sortedNeighbours.add(neighbour);
				}
				else {
				sortedNeighbours.add(0, neighbour);
				}
			}
			else {
				sortedNeighbours.add(0, neighbour);
			}
		}
		return sortedNeighbours;
	}
	
	
	
}
