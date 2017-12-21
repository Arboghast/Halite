package hlt;

public class graveyard {
	/* public ThrustMove navigateTowardsEG(final GameMap gameMap, final Position targetPos, final int maxThrust,
	            final boolean avoidObstacles, final int maxCorrections, final double angularStepRad) {
	if (maxCorrections <= 0) {
	return null;
	}
	
	final double distance = ship.getDistanceTo(targetPos);
	final double angleRad = ship.orientTowardsInRad(targetPos);
	
	if (avoidObstacles && !gameMap.objectsBetweenEG(ship, targetPos).isEmpty()) {
		final double newTargetDx = Math.cos(angleRad + angularStepRad) * distance;
		final double newTargetDy = Math.sin(angleRad + angularStepRad) * distance;
		Position newTarget = new Position(ship.getXPos() + newTargetDx, ship.getYPos() + newTargetDy);
		return navigateTowards(gameMap, newTarget, maxThrust, true, (maxCorrections-1), angularStepRad);
	}
	
	final int thrust;
	if (distance < maxThrust) {
	// Do not round up, since overshooting might cause collision.
	thrust = (int) distance;
	}
	else {
	thrust = maxThrust;
	}
	
	final int angleDeg = Util.angleRadToDegClipped(angleRad);
	
	return new ThrustMove(ship, angleDeg, thrust);
}
public ThrustMove navigateTowardsC(final GameMap gameMap, final Position targetPos, final int maxThrust,
		            final boolean avoidObstacles, final int maxCorrections, final double angularStepRad) {
		
		 double distance = ship.getDistanceTo(targetPos);
		 double angleRad = ship.orientTowardsInRad(targetPos);
		
		if (avoidObstacles && !gameMap.objectsBetween(ship, targetPos).isEmpty()) {
			double newTargetDx;
			double newTargetDy;
			Position newTarget = targetPos;
			boolean toggle = true;
			int left = 1;
			int right = 1;
			while(true)
			{
				
				if(gameMap.objectsBetween(ship,newTarget).isEmpty())
				{
					break;
				}
				if(left+right > 180)
				{
					return null;
				}
				if (toggle) {
					newTargetDx = Math.cos(angleRad - (angularStepRad*left)) * distance;
					newTargetDy = Math.sin(angleRad - (angularStepRad*left)) * distance;
					left++;
				}
				else
				{
					newTargetDx = Math.cos(angleRad + (angularStepRad*right)) * distance;
					newTargetDy = Math.sin(angleRad + (angularStepRad*right)) * distance;
					right++;
				}
				toggle = !toggle;
				newTarget = new Position(ship.getXPos() + newTargetDx, ship.getYPos() + newTargetDy);
				//distance = ship.getDistanceTo(newTarget);
				angleRad = ship.orientTowardsInRad(newTarget);
			}
		}
		
		final int thrust;
		if (distance < maxThrust) {
		// Do not round up, since overshooting might cause collision.
		thrust = (int) distance;
		}
		else {
		thrust = maxThrust;
		}
		
		final int angleDeg = Util.angleRadToDegClipped(angleRad);
		
		return new ThrustMove(ship, angleDeg, thrust);
		}
public ThrustMove navigateTowardsD(final GameMap gameMap, final Position targetPos, final int maxThrust,
		            final boolean avoidObstacles, final int maxCorrections, final double angularStepRad) {
		
		 double distance = ship.getDistanceTo(targetPos);
		 double angleRad = ship.orientTowardsInRad(targetPos);
		
		if (avoidObstacles && !gameMap.objectsBetween(ship, targetPos).isEmpty()) {
			double newTargetDx;
			double newTargetDy;
			Position newTarget = targetPos;
			int incr = 1;
			while(incr < 180)
			{
				if(incr > 179 )
				{
					newTarget = null;
					break;
				}
				
					newTargetDx = Math.cos(angleRad - (angularStepRad*incr)) * distance;
					newTargetDy = Math.sin(angleRad - (angularStepRad*incr)) * distance;
					newTarget = new Position(ship.getXPos() + newTargetDx, ship.getYPos() + newTargetDy);
					if(gameMap.objectsBetween(ship,newTarget).isEmpty())
					{
						break;
					}
					
					newTargetDx = Math.cos(angleRad + (angularStepRad*incr)) * distance;
					newTargetDy = Math.sin(angleRad + (angularStepRad*incr)) * distance;
					newTarget = new Position(ship.getXPos() + newTargetDx, ship.getYPos() + newTargetDy);
					if(gameMap.objectsBetween(ship,newTarget).isEmpty())
					{
						break;
					}
					incr++;
				
				//distance = ship.getDistanceTo(newTarget);
				//angleRad = ship.orientTowardsInRad(newTarget);
			}
		}
		
		final int thrust;
		if (distance < maxThrust) {
		// Do not round up, since overshooting might cause collision.
		thrust = (int) distance;
		}
		else {
		thrust = maxThrust;
		}
		
		final int angleDeg = Util.angleRadToDegClipped(angleRad);
		
		return new ThrustMove(ship, angleDeg, thrust);
		}
		public ThrustMove navigateAtMaxSpeed(final GameMap gameMap, final Position targetPos)
    {
    	return navigateTowards(gameMap, targetPos, Constants.MAX_SPEED, true, 90 , Math.PI/180);
    }
     public ThrustMove navigateToAttackWC(final GameMap gameMap, final Position targetPos, final int maxThrust,
		            final boolean avoidObstacles, final int maxCorrections,final boolean leftOrRight ,final double angularStepRad) {
		if (maxCorrections <= 0) {
		return null;
		}
		
	    double distance = ship.getDistanceTo(targetPos);
		double angleRad = ship.orientTowardsInRad(targetPos);
		
		if (avoidObstacles && !gameMap.objectsBetween(ship, targetPos).isEmpty()) {
		double newTargetDx;
		double newTargetDy;
		Position newTarget = targetPos;
		boolean toggle = leftOrRight;
		int left = 1;
		int right = 1;
		while(left+right < 180)
		{
			
			distance = ship.getDistanceTo(newTarget);
			angleRad = ship.orientTowardsInRad(targetPos);
			if (toggle) {
				newTargetDx = Math.cos(angleRad - (angularStepRad*left)) * distance;
				newTargetDy = Math.sin(angleRad - (angularStepRad*left)) * distance;
				left++;
			}
			else
			{
				newTargetDx = Math.cos(angleRad + (angularStepRad*right)) * distance;
				newTargetDy = Math.sin(angleRad + (angularStepRad*right)) * distance;
				right++;
			}
			toggle = !toggle;
			newTarget = new Position(ship.getXPos() + newTargetDx, ship.getYPos() + newTargetDy);
			if(gameMap.objectsBetween(ship, newTarget).isEmpty())
			{
				break;
			}
		}
		boolean toggle = leftOrRight;
		if (leftOrRight) {
			newTargetDx = Math.cos(angleRad - angularStepRad) * distance;
			newTargetDy = Math.sin(angleRad - angularStepRad) * distance;
		}
		else
		{
			newTargetDx = Math.cos(angleRad + angularStepRad) * distance;
			newTargetDy = Math.sin(angleRad + angularStepRad) * distance;
		}
		toggle = !toggle;
		Position newTarget = new Position(ship.getXPos() + newTargetDx, ship.getYPos() + newTargetDy);
		return navigateTowardsWithCollisionRecursion(gameMap, newTarget, maxThrust, true, (maxCorrections-1),toggle, angularStepRad,angleRad,1);
		}}
		
		final int thrust;
		if (distance < maxThrust) {
		// Do not round up, since overshooting might cause collision.
		thrust = (int) distance;
		}
		else {
		thrust = maxThrust;
		}
		
		final int angleDeg = Util.angleRadToDegClipped(angleRad);
		
		return new ThrustMove(ship, angleDeg, thrust);
		}
		
		 public ArrayList<Entity> objectsBetweenEG(Position start, Position target) {
        final ArrayList<Entity> entitiesFound = new ArrayList<>();
       
        addEntitiesBetweenEG(entitiesFound, start, target, planets.values());
        addEntitiesBetweenEG(entitiesFound, start, target, allShips);
        return entitiesFound;
    }
    private static void addEntitiesBetweenEG(final List<Entity> entitiesFound,final Position start, final Position target,final Collection<? extends Entity> entitiesToCheck) {

    	for (final Entity entity : entitiesToCheck) {
            if (entity.equals(start) || entity.equals(target)) {
                continue;
            }
            if (Collision.segmentCircleIntersectEarlyGame(start, target, entity, Constants.FORECAST_FUDGE_FACTOR)) {
                entitiesFound.add(entity);
            }
        }
    }
    
     public static boolean segmentCircleIntersect(final Position start, final Position end, final Entity circle, final double fudge) {
        // Parameterize the segment as start + t * (end - start),
        // and substitute into the equation of a circle
        // Solve for t
    	
    	//add code so that i
        final double circleRadius = circle.getRadius(); //.7 to get rid of early game ship collision
        final double startX = start.getXPos();
        final double startY = start.getYPos();
        final double endX = end.getXPos();
        final double endY = end.getYPos();
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
    public static boolean segmentCircleIntersectEarlyGame(final Position start, final Position end, final Entity circle, final double fudge) {
        // Parameterize the segment as start + t * (end - start),
        // and substitute into the equation of a circle
        // Solve for t
        final double circleRadius = circle.getRadius()+.7; //.7 to get rid of early game ship collision
        final double startX = start.getXPos();
        final double startY = start.getYPos();
        final double endX = end.getXPos();
        final double endY = end.getYPos();
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
    
     public static boolean segmentIntersect(final Position start, final Position end, final Entity enemy,final double fudge) {
    	double xX = start.getXPos();
    	double xY = start.getYPos();
    	double yX = end.getXPos();
    	double yY = end.getYPos();
    	double slope = (yY-xY)/(yX-xX);
    	return Math.abs( (  (slope*(enemy.getXPos()  -  xX) ) + xY)- enemy.getYPos())     <= enemy.getRadius()+.55; // radius of 1
    }
*/}
