package jpftesting.model;


public class SimpleCollisionDetector implements CollisionDetector {
    private static double RESTITUTION_FACTOR = 1; 

    @Override
    /**
     * 
     * Resolving collision between 2 balls, updating their getPosition and velocity
     * @param collision the pair of balls that are colliding
     */
    public void resolveCollision(Ball a, Ball b) {
    	/* check if there is a collision */
    	
    	/* compute dv = b.getPos - a.getPos vector */

		BallView a_snap = a.getSnapshot();
		BallView b_snap = b.getSnapshot();

    	double dx   = b_snap.getPos().x() - a_snap.getPos().x();
        double dy   = b_snap.getPos().y() - a_snap.getPos().y();
        double dist = Math.hypot(dx, dy);
        double minD = a_snap.getRadius() + b_snap.getRadius();
        
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
	        double totalM  = a_snap.getMass() + b_snap.getMass();
	
	        double a_factor = overlap * (b_snap.getMass() / totalM);
	        double a_deltax = nx * a_factor; 
	        double a_deltay = ny * a_factor; 
	        
	        a.setPos(new Position(a_snap.getPos().x() - a_deltax, a_snap.getPos().y() - a_deltay));
	        
	        double b_factor = overlap * (a_snap.getMass() / totalM);
	        double b_deltax = nx * b_factor; 
	        double b_deltay = ny * b_factor; 
	
	        b.setPos(new Position(b_snap.getPos().x() + b_deltax, b_snap.getPos().y() + b_deltay));
	
	        /* Update velocities  */
	        
	        /* relative speed along the normal vector*/
	
	        double dvx = b_snap.getVel().x() - a_snap.getVel().x();
	        double dvy = b_snap.getVel().y() - a_snap.getVel().y(); 
	        double dvn = dvx * nx + dvy * ny;
	
	        if (dvn <= 0) { /* if not already separating, update velocities */
	        	
	        	double imp = -(1 + RESTITUTION_FACTOR) * dvn / (1.0/a_snap.getMass() + 1.0/b_snap.getMass());        
	        	a.setVel(new Speed(a_snap.getVel().x() - (imp / a_snap.getMass()) * nx, a_snap.getVel().y() - (imp / a_snap.getMass()) * ny));                
	        	b.setVel(new Speed(b_snap.getVel().x() + (imp / b_snap.getMass()) * nx, b_snap.getVel().y() + (imp / b_snap.getMass()) * ny));
	        }
        }
    }
    

}
