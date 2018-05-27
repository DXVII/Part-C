/** 
 * Project by:
 * Haobei Ma     837734
 * David Pham	 756598
 * Shaobin Zhao  776298
 */

package mycontroller;

import java.util.ArrayList;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;


//A class used to contain all the information for a tile at a single coordinate
public class Node{
	
	//Coordinate of the node
	Coordinate coordinate;
	
	//Tile type of the node
	MapTile tileType;
	
	//Used in search algorithm. Checks if the node has already been checked 
	boolean visited;
	
	//Whether the car has seen the tile
	boolean explored;
	
	//A list of the neighbours connecting to the point
	ArrayList<Node> neighbours;
	
	//Pathway used to get from the car's position to current position
	//Used in breadth first search.
	ArrayList<Coordinate> pathway = new ArrayList<Coordinate>();

	
	//Constructor
	Node(Coordinate coordinate, MapTile tile){
		this.coordinate = coordinate;
		this.neighbours=new ArrayList<>();
		this.visited = false;
		this.explored = false;		
		this.tileType = tile;
        this.pathway.add(this.coordinate);
	}
	
	//Setter to update neighbours
	public void addneighbours(Node neighbourNode) 
	{
		this.neighbours.add(neighbourNode);
	}
	
	//Getter for neighbours
	public ArrayList<Node> getNeighbours() {
		return neighbours;
	}
	
	//Setter for neighbours
	public void setNeighbours(ArrayList<Node> neighbours) {
		this.neighbours = neighbours;
	}
	
	//Sort the neighbours by the orientation of the car
	public ArrayList<Node> sortNeighbours(WorldSpatial.Direction orientation) {
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
		
		int currX = this.coordinate.x;
		int currY = this.coordinate.y;
		Coordinate straight = new Coordinate(currX+ posX, currY + posY);
		ArrayList<Node> sortedNeighbours = new ArrayList<Node>();
		for (Node neighbour: this.neighbours) {
			if (neighbour.coordinate.equals(straight)) {
				//Add the neighbour to the front if it is directly ahead 
				sortedNeighbours.add(0, neighbour);
			}
			else {
				sortedNeighbours.add(neighbour);
			}
		}
		return sortedNeighbours;
	}
	
	
	//Sort neighbours by lava tile. 
	public ArrayList<Node> sortNeighbours() {

		ArrayList<Node> sortedNeighbours = new ArrayList<Node>();
		for (Node neighbour: this.neighbours) {
			
			//If the tile is lava, add it to the end of the arrayList
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