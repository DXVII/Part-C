package mycontroller;

import java.util.ArrayList;

import tiles.MapTile;
import utilities.Coordinate;


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
}