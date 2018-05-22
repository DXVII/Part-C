package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

class Database {
	ArrayList<Node> graph = new ArrayList<Node>();
	public Database(HashMap<Coordinate, MapTile> map) {
		int firstX, firstY, secondX, secondY, diffX, diffY, total;
		for (Coordinate place: map.keySet()) {
			MapTile value = map.get(place);
			if (value.isType(MapTile.Type.ROAD)){
				graph.add(new Node(place));
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
	public boolean noNewNeighbours(Node node) {
		for (Node neighbours: node.neighbours) {
			if (!neighbours.visited) {
				return false;
			}
		}
		return true;
	}

}
