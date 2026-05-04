package it.unibo.assignment01.model;

public record Position(double x, double y)  {

    public Position sum(Speed v){
        return new Position(x+v.x(),y+v.y());
    }

    public Speed sub(Position v){
        return new Speed(x-v.x(),y-v.y());
    }
    
    public String toString(){
        return "P2d("+x+","+y+")";
    }

    public double x() {
    	return x;
    }

    public double y() {
    	return y;
    }

    public double dist(Position other) {
        return Math.hypot(other.x-x, other.y-y);
    }
}

