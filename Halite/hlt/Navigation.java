package hlt;

public class Navigation {

    private Ship ship;
    private Entity target;

    public Navigation(final Ship ship, final Entity target) {
        this.ship = ship;
        this.target = target;
    }

    public ThrustMove navigateToDock(final GameMap gameMap, final int maxThrust) {
        final int maxCorrections = Constants.MAX_CORRECTIONS;
        final boolean avoidObstacles = true;
        final double angularStepRad = Math.PI/180;
        final Position targetPos = ship.getClosestPoint(target);

        return navigateTowards(gameMap, targetPos, maxThrust, avoidObstacles, maxCorrections, angularStepRad);
    }
    public ThrustMove navigateToDockEG(final GameMap gameMap, final int maxThrust) {
        final int maxCorrections = Constants.MAX_CORRECTIONS;
        final boolean avoidObstacles = true;
        final double angularStepRad = Math.PI/180;
        final Position targetPos = ship.getClosestPoint(target);

        return navigateTowardsE(gameMap, targetPos, maxThrust, avoidObstacles, maxCorrections, angularStepRad);
    }

    public ThrustMove navigateTowards(final GameMap gameMap, final Position targetPos, final int maxThrust,
                                      final boolean avoidObstacles, final int maxCorrections, final double angularStepRad) {
        if (maxCorrections <= 0) {
            return null;
        }

        final double distance = ship.getDistanceTo(targetPos);
        final double angleRad = ship.orientTowardsInRad(targetPos);

        if (avoidObstacles && !gameMap.objectsBetween(ship, targetPos).isEmpty()) {
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
    public ThrustMove navigateTowardsEG(final GameMap gameMap, final Position targetPos, final int maxThrust,
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
    public ThrustMove navigateTowardsE(final GameMap gameMap, final Position targetPos, final int maxThrust,
		            final boolean avoidObstacles, final int maxCorrections, final double angularStepRad) {
		
		 double distancel = ship.getDistanceTo(targetPos);
		 double angleRadl = ship.orientTowardsInRad(targetPos);
		 double distancer = ship.getDistanceTo(targetPos);
		 double angleRadr = ship.orientTowardsInRad(targetPos);
		 double newTargetDxl;
		 double newTargetDyl;
		 double newTargetDxr;
		 double newTargetDyr;
		 Position newTarget = targetPos;
		 int incr = 0;
		 boolean togg = false;
		 
		while(avoidObstacles && !gameMap.objectsBetween(ship, targetPos).isEmpty()) {
				if(incr > 179 )
				{
					return null;
				}
				
					newTargetDxl = Math.cos(angleRadl - angularStepRad) * distancel;
					newTargetDyl = Math.sin(angleRadl - angularStepRad) * distancel;
					newTarget = new Position(ship.getXPos() + newTargetDxl, ship.getYPos() + newTargetDyl);
					if(gameMap.objectsBetween(ship,newTarget).isEmpty())
					{
						togg = true;
						break;
					}
					distancel = ship.getDistanceTo(newTarget);
					angleRadl = ship.orientTowardsInRad(newTarget);
					
					
					newTargetDxr = Math.cos(angleRadr + angularStepRad) * distancer;
					newTargetDyr = Math.sin(angleRadr + angularStepRad) * distancer;
					newTarget = new Position(ship.getXPos() + newTargetDxr, ship.getYPos() + newTargetDyr);
					if(gameMap.objectsBetween(ship,newTarget).isEmpty())
					{
						togg = false;
						break;
					}
					distancer = ship.getDistanceTo(newTarget);
					angleRadr = ship.orientTowardsInRad(newTarget);
					incr++;
				
				//distance = ship.getDistanceTo(newTarget);
				//angleRad = ship.orientTowardsInRad(newTarget);
		}
		
		final int thrust;
		if (togg) {
			if (distancel < maxThrust) {
				// Do not round up, since overshooting might cause collision.
				thrust = (int) distancel;
			} else {
				thrust = maxThrust;
			}
			final int angleDeg = Util.angleRadToDegClipped(angleRadl);
			return new ThrustMove(ship, angleDeg, thrust);
		}
		else
		{
			if (distancer < maxThrust) {
				// Do not round up, since overshooting might cause collision.
				thrust = (int) distancer;
			} else {
				thrust = maxThrust;
			}
			final int angleDeg = Util.angleRadToDegClipped(angleRadr);
			return new ThrustMove(ship, angleDeg, thrust);
		}
	}
    public ThrustMove navigateAtMaxSpeed(final GameMap gameMap, final Position targetPos)
    {
    	return navigateTowards(gameMap, targetPos, Constants.MAX_SPEED, true, 90 , Math.PI/180);
    }
    public ThrustMove navigateAwayFrom(final GameMap gameMap, final Position targetPos, final int maxThrust,
            final boolean avoidObstacles, final int maxCorrections, final double angularStepRad) {
			if (maxCorrections <= 0) {
			return null;
			}
			
			final double distance = ship.getDistanceTo(targetPos);
			final double angleRad = ship.orientTowardsInRad(targetPos);
			
			if (avoidObstacles && !gameMap.objectsBetween(ship, targetPos).isEmpty()) {
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
			
			int angleDeg = Util.angleRadToDegClipped(angleRad);
			angleDeg = Math.abs(angleDeg - 180);
			
			return new ThrustMove(ship, angleDeg, thrust);
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
	/*	boolean toggle = leftOrRight;
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
		}*/}
		
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
    

	public ThrustMove navigateToAttack(final GameMap gameMap, final Position targetPos, final int speed) {
		return navigateTowardsE(gameMap, ship.getClosestPoint(target), speed, true, 90 , Math.PI/180);   //////
	}
}
