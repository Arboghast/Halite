package hlt;

public class Collision {
    /**
     * @param start  The start of the segment.
     * @param end    The end of the segment.
     * @param fudge  An additional safety zone to leave when looking for collisions. (Probably set it to ship radius 0.5)
     * @return true if the segment intersects, false otherwise
     */
   
    public static boolean vectorIntersect(final Position start, final Position end, final Entity circle, final double fudge) {
        // Parameterize the segment as start + t * (end - start),
        // and substitute into the equation of a circle
        // Solve for t
    	final double endX;
        final double endY;
        final double circleRadius = circle.getRadius();
        final double startX = start.getXPos();
        final double startY = start.getYPos();
        
        if (circle instanceof Ship && (((Ship) circle).getTargetPosition() != null) ) { //IF INSTANCEOFSHIP AND HAS TARGETPOS
    		//IF CIRCLE INSTANCE OF SHIP TAKE THE TARGETPOS AND INVERT IT AND ADD IT TO END
    		Ship ship2 = (Ship) circle;
			Position startS = start;
			Position endS = end;
			Position startE = ship2.getCurrentPosition();
			Position endE = ship2.getTargetPosition();
			double X = (endS.getXPos() - startS.getXPos()) - (endE.getXPos() - startE.getXPos());
			double Y = (endS.getYPos() - startS.getYPos()) - (endE.getYPos() - startE.getYPos());
			endX = start.getXPos()+X;
			endY = start.getYPos()+Y;
		}
        else
        {
        	endX = end.getXPos();
            endY = end.getYPos();
        }
        final double centerX = circle.getXPos();
        final double centerY = circle.getYPos();
        final double dx = endX - startX;
        final double dy = endY - startY;

        final double a = square(dx) + square(dy);

        final double b = -2 * (square(startX) - (startX * endX)
                            - (startX * centerX) + (endX * centerX)
                            + square(startY) - (startY * endY)
                            - (startY * centerY) + (endY * centerY));

        if (a == 0.0) {
            // Start and end are the same point
            return start.getDistanceTo(circle) <= circleRadius + fudge;
        }

        // Time along segment when closest to the circle (vertex of the quadratic)
        final double t = Math.min(-b / (2 * a), 1.0);
        if (t < 0) {
            return false;
        }

        final double closestX = startX + dx * t;
        final double closestY = startY + dy * t;
        final double closestDistance = new Position(closestX, closestY).getDistanceTo(circle);

        return closestDistance <= circleRadius + fudge;
    }

    public static double square(final double num) {
        return num * num;
    }
   
    public static boolean vectorCollision(final Position start, final Position target,final Entity ship, final double fudge)
    {
    	if (ship instanceof Ship ) {
    		Ship ship2 = (Ship) ship;
			Position startS = start;
			Position endS = target;
			Position startE = ship2.getCurrentPosition();
			Position endE = ship2.getTargetPosition();
			double xPos = (endS.getXPos() - startS.getXPos()) - (endE.getXPos() - startE.getXPos());
			double yPos = (endS.getYPos() - startS.getYPos()) - (endE.getYPos() - startE.getYPos());
			Position newTarget = new Position(start.getXPos()+xPos, start.getYPos()+yPos);
			return vectorIntersect(start, newTarget, ship, fudge);
			//double slope = (startS.getYPos() - newTarget.getYPos()) / (startS.getXPos() - newTarget.getXPos());
			//return Math.abs(slope * (startE.getXPos() - startS.getXPos()) + startS.getYPos() - newTarget.getYPos()) < fudge;
		}
    	else
    	{
    		return vectorIntersect(start, target, ship, fudge);
    	}
 
    }
}
