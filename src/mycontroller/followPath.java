// asssume array has tile by tile coordinates of path car needs to follow
public void followPath(ArrayList<Coordinate> coordinate, Car car){
    for(Coordinate coord: coordinate){

        while(!(car.getCoordinate()).equals(coord)){
            // determine path from car
            // get curr car tile
            location = (car.getCurrentTile().)getCoordinate(); //don't know if this exists
            currX = location.getX();
            currY = location.getY();
            moveX = coord.getX() - currX;
            moveY = coord.getY() - currY;

            //Up Down Movements
            if(moveY > 0) {
                //go up
            }
            if(moveY < 0) {
                // go backwards?
                // turn around?
            }

            //Horizontal Movements
            if(moveX > 0){
                //turning right
            }
            if(moveX < 0 ){
                //turn left
            }
        }

    }

}
