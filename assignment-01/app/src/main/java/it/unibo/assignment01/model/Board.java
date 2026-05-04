package it.unibo.assignment01.model;

import java.util.List;

public interface Board { 
    public static double RESTITUTION_FACTOR = 1; 

    public List<Ball> getBalls();
    
    public Ball getPlayerBall();
    
    public  Boundary getBounds();

    public void checkHole(Ball b);

    /**
     * 
     * Resolving collision between 2 balls, updating their getPosition and velocity
     * 
     * @param a
     * @param b
     */
    public static void resolveCollision(Ball a, Ball b) {
        
    	/* check if there is a collision */
    	
    	/* compute dv = b.getPos - a.getPos vector */

    	double dx   = b.getPos().x() - a.getPos().x();
        double dy   = b.getPos().y() - a.getPos().y();
        double dist = Math.hypot(dx, dy);
        double minD = a.getRadius() + b.getRadius();
        
        /* 
         * There is a collision if the distance between the two balls is less than the sum of the radii 
         * 
         */
        if (dist < minD && dist > 1e-6)  {

	        /* 
	         * Collision case - what to do:
	         * 
	         * 1) solve overlaps, moving balls 
	         * 2) update velocities
	         * 
	         */
	        
        	/* dvn = V2d(nx,ny) = dv unit vector */
    
        	double nx = dx / dist;
	        double ny = dy / dist;
	
	        /* 
	         * 
	         * Update getPositions to solve overlaps, moving balls along dvn
	         * - the displacements is proportional to the mass
	         * 
	         */
	        double overlap = minD - dist;
	        double totalM  = a.getMass() + b.getMass();
	
	        double a_factor = overlap * (b.getMass() / totalM);
	        double a_deltax = nx * a_factor; 
	        double a_deltay = ny * a_factor; 
	        
	        a.setPos(new Position(a.getPos().x() - a_deltax, a.getPos().y() - a_deltay));
	        
	        double b_factor = overlap * (a.getMass() / totalM);
	        double b_deltax = nx * b_factor; 
	        double b_deltay = ny * b_factor; 
	
	        b.setPos(new Position(b.getPos().x() + b_deltax, b.getPos().y() + b_deltay));
	
	        /* Update velocities  */
	        
	        /* relative speed along the normal vector*/
	
	        double dvx = b.getVel().x() - a.getVel().x();
	        double dvy = b.getVel().y() - a.getVel().y(); 
	        double dvn = dvx * nx + dvy * ny;
	
	        if (dvn <= 0) { /* if not already separating, update velocities */
	        	
	        	double imp = -(1 + RESTITUTION_FACTOR) * dvn / (1.0/a.getMass() + 1.0/b.getMass());        
	        	a.setVel(new Speed(a.getVel().x() - (imp / a.getMass()) * nx, a.getVel().y() - (imp / a.getMass()) * ny));                
	        	b.setVel(new Speed(b.getVel().x() + (imp / b.getMass()) * nx, b.getVel().y() + (imp / b.getMass()) * ny));
	        }
        }
    }

    
}
