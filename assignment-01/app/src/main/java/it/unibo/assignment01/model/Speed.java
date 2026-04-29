package it.unibo.assignment01.model;

public record Speed(double x, double y)  {

    public Speed sum(Speed v){
        return new Speed(x+v.x,y+v.y);
    }

    public double abs(){
        return (double)Math.sqrt(x*x+y*y);
    }

    public Speed getNormalized(){
        double module=(double)Math.sqrt(x*x+y*y);
        return new Speed(x/module,y/module);
    }

    public Speed mul(double fact){
        return new Speed(x*fact,y*fact);
    }

    public Speed getSwappedX() {
    	return new Speed(-x, y);
    }

    public Speed getSwappedY() {
    	return new Speed(x, -y);
    }

    public String toString(){
        return "V2d("+x+","+y+")";
    }
    
    
}
