package mycontroller;
import java.util.HashMap;
import utilities.Coordinate;

public class Database {

    private HashMap<Coordinate, TileInfo> carMap = new HashMap<Coordinate, TileInfo>();

	public Database() {
		this.initialiseCarMap();
	}

	private void initialiseCarMap() {
		// TODO Auto-generated method stub
		
	}

	public HashMap<Coordinate, TileInfo> getCarMap() {
		return carMap;
	}

	public void setCarMap(HashMap<Coordinate, TileInfo> carMap) {
		this.carMap = carMap;
	}
	

}
