package mycontroller;

import utilities.Coordinate;

public class TileInfo {
    String tileType;
    boolean isChecked;
    Coordinate coordinate;

    public TileInfo(String tileType, boolean isChecked, Coordinate coordinate){
        this.setTileType(tileType);
        this.setIsChecked(isChecked);
        this.setCoordinate(coordinate);
    }

    public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
    public Coordinate getCoordinate() {
    	return this.coordinate;
    }

	public void setTileType(String tileType){
        this.tileType = tileType;
    }
    public String setTileType(){
        return this.tileType;
    }

    public void setIsChecked(boolean checked){
        this.isChecked = checked;
    }
    public boolean getIsChecked(){
        return this.isChecked;
    }



}
