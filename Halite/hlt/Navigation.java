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
   
    public ThrustMove navigateTowardsE(final GameMap gameMap, final Position targetPos, final int maxThrust,
		            final boolean avoidObstacles, final int maxCorrections, final double angularStepRad) {
		
		 double distancel = ship.getDistanceTo(targetPos);
		 double angleRadl = ship.orientTowardsInRad(targetPos);
		 double distancer = ship.getDistanceTo(targetPos);
		 double angleRadr = ship.orientTowardsInRad(targetPos);
		 double newTargetDxl = 0;
		 double newTargetDyl = 0;
		 double newTargetDxr = 0;
		 double newTargetDyr = 0;
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
					distancel = ship.getDistanceTo(newTarget);
					angleRadl = ship.orientTowardsInRad(newTarget);
					if(gameMap.objectsBetween(ship,newTarget).isEmpty())
					{
						togg = true;
						break;
					}
					
					newTargetDxr = Math.cos(angleRadr + angularStepRad) * distancer;
					newTargetDyr = Math.sin(angleRadr + angularStepRad) * distancer;
					newTarget = new Position(ship.getXPos() + newTargetDxr, ship.getYPos() + newTargetDyr);
					distancer = ship.getDistanceTo(newTarget);
					angleRadr = ship.orientTowardsInRad(newTarget);
					if(gameMap.objectsBetween(ship,newTarget).isEmpty())
					{
						togg = false;
						break;
					}
					incr++;
		}
		
		final int thrust;
		ship.setTargetPosition(newTarget);
		if (togg) {
			if (distancel < maxThrust) {
				// Do not round up, since overshooting might cause collision.
				thrust = (int) distancel;
			} else {
				thrust = maxThrust;
			}
			
			final int angleDeg = Util.angleRadToDegClipped(angleRadl);
			ship.setDegree(angleDeg);
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
			ship.setDegree(angleDeg);
			return new ThrustMove(ship, angleDeg, thrust);
		}
	}
   
    public ThrustMove navigateAwayFrom(final GameMap gameMap, final Position targetPos, final int maxThrust,
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
					distancel = ship.getDistanceTo(newTarget);
					angleRadl = ship.orientTowardsInRad(newTarget);
					if(gameMap.objectsBetween(ship,newTarget).isEmpty())
					{
						togg = true;
						break;
					}
					
					newTargetDxr = Math.cos(angleRadr + angularStepRad) * distancer;
					newTargetDyr = Math.sin(angleRadr + angularStepRad) * distancer;
					newTarget = new Position(ship.getXPos() + newTargetDxr, ship.getYPos() + newTargetDyr);
					distancer = ship.getDistanceTo(newTarget);
					angleRadr = ship.orientTowardsInRad(newTarget);
					if(gameMap.objectsBetween(ship,newTarget).isEmpty())
					{
						togg = false;
						break;
					}
					incr++;
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
			ThrustMove yes = new ThrustMove(ship, angleDeg-180, thrust);
			ship.setTargetPosition(tools.newTarget(yes));
			return yes;
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
			ThrustMove yes = new ThrustMove(ship, angleDeg-180, thrust);
			ship.setTargetPosition(tools.newTarget(yes));
			return yes;
		}
    }
	public ThrustMove navigateToAttack(final GameMap gameMap, final Position targetPos, final int speed) {
		return navigateTowardsE(gameMap, ship.getClosestPoint(target), speed, true, Constants.MAX_CORRECTIONS , Math.PI/180);   //////
	}
}
