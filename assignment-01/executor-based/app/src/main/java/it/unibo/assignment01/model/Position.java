package it.unibo.assignment01.model;

public class Position {
    private final double x;
    private final double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Speed sub(Position v){
        return new Speed(x-v.x(),y-v.y());
    }

    public Position sum(Speed v){
        return new Position(x+v.x(),y+v.y());
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
        return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
    }
}

