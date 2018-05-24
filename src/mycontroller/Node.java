package mycontroller;

import java.util.ArrayList;

import tiles.MapTile;
import utilities.Coordinate;

public class Node
{
	Coordinate coordinate;
	MapTile tileType;
	boolean visited;
	boolean explored;
	ArrayList<Node> neighbours;
	ArrayList<Coordinate> pathway = new ArrayList<Coordinate>();

	Node(Coordinate coordinate, MapTile tile)
	{
		this.coordinate = coordinate;
		this.neighbours=new ArrayList<>();
		this.visited = false;
		this.explored = false;		
		this.tileType = tile;
        this.pathway.add(this.coordinate);
	}
	public void addneighbours(Node neighbourNode) 
	{
		this.neighbours.add(neighbourNode);
	}
	public ArrayList<Node> getNeighbours() {
		return neighbours;
	}
	public void setNeighbours(ArrayList<Node> neighbours) {
		this.neighbours = neighbours;
	}
}