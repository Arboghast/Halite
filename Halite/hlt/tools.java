package hlt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import hlt.Ship.DockingStatus;

public class tools {
		public static Position newTarget(ThrustMove ntm)
	    {
	    	double newTargetDx = Math.cos(ntm.getAngle()) *ntm.getThrust();
			double newTargetDy = Math.sin(ntm.getAngle()) * ntm.getThrust();
			Position newTarget = new Position(ntm.getShip().getXPos() + newTargetDx, ntm.getShip().getYPos() + newTargetDy);
			return newTarget;
	    }
		
	
		public static void populateList(ArrayList<Planet> safeToDock, GameMap gameMap) {
			Map<Integer,Planet> everyPlanetByDistance = gameMap.getAllPlanets();
	    	Set<Integer> keys = everyPlanetByDistance.keySet();
	    	for(Integer key: keys){
	    		Planet cycle = everyPlanetByDistance.get(key);
	    		if( (!cycle.isOwned()) || (cycle.getOwner() == gameMap.getMyPlayerId()) )
	    		{
	    			if(!enemiesNearby(cycle,gameMap,12.0))
	    			{
	    				safeToDock.add(cycle);
	    			}
	    		}
	    	}
			
		}

		public static boolean enemiesNearby(Planet ally, GameMap gameMap,Double key2) {
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(ally);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		if(treeMap.get(key).getOwner() != gameMap.getMyPlayerId())
	    		{
	    			if(key < ally.getRadius()+key2)
	    			{
	    				return true;
	    			}
	    		}
	    	}
			return false;
		}
		public static boolean enemiesNearby(Ship ship, GameMap gameMap) {
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(ship);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		if(treeMap.get(key).getOwner() != gameMap.getMyPlayerId())
	    		{
	    			if(key < 15)
	    			{
	    				return true;
	    			}
	    		}
	    	}
			return false;
		}
		public static boolean enemiesNearby(Ship ship, GameMap gameMap, double x) {
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(ship);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		if(treeMap.get(key).getOwner() != gameMap.getMyPlayerId())
	    		{
	    			if(key < x)
	    			{
	    				return true;
	    			}
	    		}
	    	}
			return false;
		}
		public static Ship closestDockedShip(Planet target, GameMap gameMap, Ship ship) {
			List<Integer> docked = target.getDockedShips();
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(ship);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship cycle = treeMap.get(key);
	    		if(docked.contains(cycle.getId()))
	    		{
	    			return cycle;
	    		}
	    	}
	    	return gameMap.getShip(target.getId(), docked.get(0));
		}

		public static Ship dockingShipsNearby(Planet target, GameMap gameMap) {
			double dis = target.getRadius() + 5;
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(target);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship cycle = treeMap.get(key);
	    		if(key <= dis )
	    		{
	    			if(cycle.getDockingStatus() == DockingStatus.Docking)
	    			{
	    				return cycle;
	    			}
	    			continue;
	    		}
	    		break;
	    		
	    	}
			return null;
		}

		public static Planet nearbyLargerPlanets(Planet target, GameMap gameMap, Ship ship) {
			Planet newTarget = target;
			double dis = ship.getDistanceTo(target);
			int dock = target.getDockingSpots();
			Map<Double,Planet> everyEntityDistance = gameMap.nearbyPlanetsByDistance(ship);
	    	Map<Double,Planet> treeMap = new TreeMap<Double, Planet>(everyEntityDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Planet cycle = treeMap.get(key);
	    		if(cycle.equals(target))
	    		{
	    			continue;
	    		}
	    		if(key < dis+7)
	    		{
	    			if(cycle.getDockingSpots() > dock)
	    			{
	    				newTarget = cycle;
	    				continue;
	    			}
	    		}
	    		break;
	    	}
			return newTarget;
		}

		public static Ship getClosestAlly(Ship target, GameMap gameMap, ArrayList<Ship> forceMove) {
			Map<Double,Ship> everyEntityDistance = gameMap.nearbyShipsByDistance(target);
	    	Map<Double, Ship> treeMap = new TreeMap<Double, Ship>(everyEntityDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship ally = treeMap.get(key);
	    		if( ally.getId() == gameMap.getMyPlayerId() && ally.getDockingStatus() == DockingStatus.Undocked/*&& !enemiesNearby(ally,gameMap,key)*/ )
	    		{
	    			return ally;
	    		}
	    		if( key > 48.0) {
	    			break;
	    		}
	    	}
	    	return null;
		}
		public static Ship getClosestAlly(Ship target, GameMap gameMap) {
			Map<Double,Ship> everyEntityDistance = gameMap.nearbyShipsByDistance(target);
	    	Map<Double, Ship> treeMap = new TreeMap<Double, Ship>(everyEntityDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship ally = treeMap.get(key);
	    		if(ally.getId() == gameMap.getMyPlayerId())
	    		{
	    			return ally;
	    		}
	    	}
	    	return null;
		}

		public static Ship beingAttacked(Ship ship, GameMap gameMap) {
			Map<Double,Ship> everyEntityDistance = gameMap.nearbyShipsByDistance(ship);
	    	Map<Double, Ship> treeMap = new TreeMap<Double, Ship>(everyEntityDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship enemy =treeMap.get(key);
	    		if(enemy.getId() != gameMap.getMyPlayerId() && key < 7)
	    		{
	    			return treeMap.get(key);
	    		}
	    		else
	    		{
	    			break;
	    		}
	    	}
	    	return null;
		}

		public static ArrayList<Planet> scanTheMap(GameMap gameMap) {
			ArrayList<Planet> no = new ArrayList<>();
			ArrayList<Planet> yes = gameMap.returnArrayOfPlanets();
			for(Planet planet : yes)
			{
				if(safeToDock(planet,gameMap))
				{
					no.add(planet);
				}
			}
			return no;
		}
		public static Ship amIGettingBamboozled(Ship ship, GameMap gameMap)
		{
			Map<Double,Ship> everyEntityDistance = gameMap.nearbyShipsByDistance(ship);
	    	Map<Double, Ship> treeMap = new TreeMap<Double, Ship>(everyEntityDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship entity = treeMap.get(key);
	    		if(key > 70)
	    		{
	    			break;
	    		}
	    		if(key <= 70)
	    		{
	    			if (entity.getId() != gameMap.getMyPlayerId()) {
						if (entity.orientTowardsInDeg(ship) > 350 || entity.orientTowardsInDeg(ship) < 5) {
							return entity;
						} 
					}
	    		}
	    	}
	    	return null;
		}
		public static boolean safeToDock(Planet target, GameMap gameMap) {
			double radius = target.getRadius() + 12;
			int mine = 1;
			int theirs = 0;
			Map<Double,Ship> everyEntityDistance = gameMap.nearbyShipsByDistance(target);
	    	Map<Double, Ship> treeMap = new TreeMap<Double, Ship>(everyEntityDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship entity = treeMap.get(key);
	    		if(key > radius)
	    		{
	    			break;
	    		}
	    		else
	    		{
	    				if(entity.getId() != gameMap.getMyPlayerId())
	    				{
	    					mine++;
	    				}
	    				else
	    				{
	    					theirs++;
	    				}
	    		}
	    	}
	    	return mine > theirs; 
		}
		
		public static ArrayList<Move> CollisionCheck(ArrayList<Move> moveList)
		{
				for (int i = 0; i < 2; i++) {
					Ship ship = moveList.get(i).getShip();
					Ship otherShip = moveList.get(i+1).getShip();
					if (intersect(ship.getCurrentPosition(), ship.getTargetPosition(), otherShip.getCurrentPosition(),
							otherShip.getTargetPosition())) {
						int x = ship.orientTowardsInDeg(ship.getTargetPosition());
						if (x < 180) {
							moveList.set(i, new ThrustMove(ship, ((ThrustMove) moveList.get(i)).getAngle() + 200,
									((ThrustMove) moveList.get(i)).getThrust()));
					//		ship.setTargetPosition(x); reset target position for collision
						} else {
							moveList.set(i, new ThrustMove(ship, ((ThrustMove) moveList.get(i)).getAngle() - 200,
									((ThrustMove) moveList.get(i)).getThrust()));
						}
					} 
				}
				
				Ship ship = moveList.get(0).getShip();
				Ship otherShip = moveList.get(2).getShip();
				if (intersect(ship.getCurrentPosition(), ship.getTargetPosition(), otherShip.getCurrentPosition(),
						otherShip.getTargetPosition())) {
					int x = ship.orientTowardsInDeg(ship.getTargetPosition());
					if (x < 180) {
						moveList.set(2, new ThrustMove(ship, ((ThrustMove) moveList.get(2)).getAngle() + 200,
								((ThrustMove) moveList.get(2)).getThrust()));
					} else {
						moveList.set(2, new ThrustMove(ship, ((ThrustMove) moveList.get(2)).getAngle() - 200,
								((ThrustMove) moveList.get(2)).getThrust()));
					}
				} 
			return moveList;
		}
		public static boolean intersect(Position a, Position b, Position c, Position d)
		{
			if(CCW(a,c,d) == CCW(b,c,d))
			{
				return false;
			}
			else if(CCW(a,b,c) == CCW(a,b,d))
			{
				return false;
			}
			return true;
		}
		public static boolean CCW(Position x, Position y, Position z) {
			double xX = x.getXPos();
			double xY = x.getYPos();
			double yX = y.getXPos();
			double yY = y.getYPos();
			double zX = z.getXPos();
			double zY = z.getYPos();
			double sum = ((yX-xX)*(yY+xY)) + ((zX-yX)*(zY+yY)) + ((xX-zX)*(xY+zY)); //checks for counterclockwise points
			if(sum > 0)
			{
				return true;
			}
			return false;
		}

		public static ArrayList<Move> willShipsCollide(ArrayList<Move> moveList) {
			ThrustMove x = (ThrustMove) moveList.get(0);
			ThrustMove y = (ThrustMove) moveList.get(1);
			ThrustMove z = (ThrustMove) moveList.get(2);
			if(withinRange(x.getAngle(),y.getAngle()))
			{
				if(x.getAngle() >= y.getAngle())
				{
					moveList.set(0, new ThrustMove(x.getShip(),x.getAngle()+4,x.getThrust()));
				}
				else
				{
					moveList.set(0, new ThrustMove(x.getShip(),x.getAngle()-4,x.getThrust()));
				}
			}
			if(withinRange(y.getAngle(),z.getAngle()))
			{
				if(y.getAngle() >= z.getAngle())
				{
					moveList.set(1, new ThrustMove(y.getShip(),y.getAngle()+4,y.getThrust()));
				}
				else
				{
					moveList.set(1, new ThrustMove(y.getShip(),y.getAngle()-4,y.getThrust()));
				}
			}
			if(withinRange(z.getAngle(),x.getAngle()))
			{
				if(z.getAngle() >= x.getAngle())
				{
					moveList.set(2, new ThrustMove(z.getShip(),z.getAngle()+4,z.getThrust()));
				}
				else
				{
					moveList.set(2, new ThrustMove(z.getShip(),z.getAngle()-4,z.getThrust()));
				}
			}
			return moveList;
		}

		public static boolean withinRange(int angle, int angle2) {
			int dif = Math.abs(angle - angle2);
			return dif < 6;
		}

		public static boolean allShipsTargeted(List<Ship> allShips, ArrayList<Entity> targetedEntities) {
			for(Ship x : allShips)
			{
				if(!targetedEntities.contains(x))
				{
					return false;
				}
			}
			return true;
		}

		public static Ship closestToTarget(Ship target, Ship ship, GameMap gameMap, ArrayList<Ship> hasMove) {
			Map<Double, Ship> everyEntityDistance = gameMap.nearbyShipsByDistance(target);
	    	Map<Double, Ship> treeMap = new TreeMap<Double, Ship>(everyEntityDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship loop = treeMap.get(key);
	    		if (loop.getOwner() == gameMap.getMyPlayerId()) {
					if (hasMove.contains(loop)) {
						continue;
					}
					if (loop.equals(ship)) {
						return ship;
					}
					return loop;
				}
	    	}
	    	return null;
		}

		public static boolean nearbyShipsApproaching(GameMap gameMap,Entity ship) {
			Map<Double,Ship> closeShips = gameMap.nearbyShipsByDistance(ship);
			for(Ship ships : closeShips.values())
			{
				double deg = ships.orientTowardsInDeg(ship);
				double dis = ships.getDistanceTo(ship);
				if (dis < 8) {
					if (deg < 5 && deg > 355) { //95 and 85
						return true;
					} 
				}
			}
			return false;
		}
		
		public static boolean biggerPlanetsNearby(Planet planet, Map<Double, Planet> nearbyPlanetsByDistance, Ship ship) {
			double distance = ship.getDistanceTo(planet);
			Map<Double,Planet> planets = new TreeMap<Double,Planet>(nearbyPlanetsByDistance);
			Set<Double> keys = planets.keySet();
			for(double key : keys)
			{
				Planet target = planets.get(key);
				if(Math.abs( ship.getDistanceTo(target) - distance) <= 1.5)
				{
					if(planet.getRadius() < target.getRadius())
					{
						return true;
					}
					continue;
				}
				if(Math.abs( ship.getDistanceTo(target) - distance) < 3.5)
				{
					if(planet.getRadius() < target.getRadius())
					{
						return true;
					}
					continue;
				}
			}
			return false;
		}

		public static boolean untargettedEntitiesNearby(Planet entity, Map<Double, Ship> map, ArrayList<Entity> targetedEntities, int i) {
			boolean answer = true;
			double rad = entity.getRadius()*1.15;
			Set<Double> keys = map.keySet(); 
			for(double key : keys)
			{
				if(key < rad)
				{
					if(!targetedEntities.contains(map.get(key)) && map.get(key).getOwner() != i)
					{
						answer = false;
					}
				}
				
			}
			return answer;
		}
		public static boolean isLeftOfLine(Ship ship, Position targetPos, ArrayList<Entity> planetsBetween) {
			double yB = 0;
			double xB = 0;
			double xC = ship.getXPos();//x2
			double yC = ship.getYPos();//y2
			double xP = targetPos.getXPos();//x1
			double yP = targetPos.getYPos();//y1
			if(!planetsBetween.isEmpty())
			{
			xB = planetsBetween.get(0).getXPos();
			yB = planetsBetween.get(0).getYPos();
			}
			
			//y = m(x - x1)+y1
			double yPos =  ((( (yC-yP)/(xC-xP) ) * (xB-xP) ) + yP);
			return yPos  <= yB;
		}
}
