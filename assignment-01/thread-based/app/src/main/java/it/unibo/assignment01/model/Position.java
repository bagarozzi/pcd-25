package it.unibo.assignment01.model;

public class Position {
    double x;
    double y;

    public Position(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Position sum(Speed v){
        return new Position(this.x+v.x(), this.y+v.y());
    }

    public Speed sub(Position v){
        return new Speed(this.x-v.x(), this.y-v.y());
    }
    
    public String toString(){
        return "P2d(" + this.x + "," + this.y +")";
    }

    public double x() {
    	return this.x;
    }

    public double y() {
    	return this.y;
    }

    public double dist(Position other) {
        return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
    }
}

