package jpftesting.model;

public class Speed {
    private double x;
    private double y;

    public Speed(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public Speed sum(Speed v){
        return new Speed(x+v.x,y+v.y);
    }

    public Speed ceilSum(Speed v, double max) {
        return new Speed(
            Math.max(-max, Math.min(max, x + v.x)),
            Math.max(-max, Math.min(max, y + v.y))
        );
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
