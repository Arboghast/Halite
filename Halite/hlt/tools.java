package hlt;

import java.util.ArrayList;
import java.util.Iterator;
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
		
	
		public static void populateList(ArrayList<Planet> safeToDock, GameMap gameMap,int buffer) {
			Map<Integer,Planet> everyPlanetByDistance = gameMap.getAllPlanets();
	    	Set<Integer> keys = everyPlanetByDistance.keySet();
	    	for(Integer key: keys){
	    		Planet cycle = everyPlanetByDistance.get(key);
	    		if( ( !cycle.isOwned() || cycle.getOwner() == gameMap.getMyPlayerId() ) && !enemiesNearby(cycle,gameMap,buffer))
	    		{
	    				safeToDock.add(cycle);
	    		}
	    	}
			
		}
		
		public static void updateGameState(Map<Planet,Value> planetValues, GameMap gameMap) {
			Map<Integer,Planet> everyPlanetByDistance = gameMap.getAllPlanets();
	    	Set<Integer> keys = everyPlanetByDistance.keySet();
	    	for(Integer key: keys){
	    		Planet planet = everyPlanetByDistance.get(key);
	    		Value planetValue = createValue(planet,gameMap,Constants.MAX_SPEED*8);
	    		planetValues.put(planet,planetValue);
	    	}
			
		}
		
		public static boolean enemiesNearby(Planet ally, GameMap gameMap,int key2) {
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(ally);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship target = treeMap.get(key);
	    		if(target.getOwner() != gameMap.getMyPlayerId() && key < ally.getRadius()+key2)
	    		{
	    				return true;
	    		}
	    	}
			return false;
		}
		public static boolean enemiesNearby(Ship ship, GameMap gameMap,int key2) {
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(ship);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		if(treeMap.get(key).getOwner() != gameMap.getMyPlayerId() && key < key2)
	    		{
	    				return true;
	    		}
	    	}
			return false;
		}
		public static void deploy(ArrayList<Ship> deployables, GameMap gameMap) {
			List<Ship> yes = gameMap.getAllShips();
	    	for(Ship ship : yes){
	    		if(ship.getOwner() == gameMap.getMyPlayerId() && ship.getDockingStatus() == Ship.DockingStatus.Undocked )
	    		{
	    				deployables.add(ship);
	    		}
	    	}
		}
		public static int enemies(Ship ship, GameMap gameMap, int i) {
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(ship);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	int j = 0;
	    	for(Double key: keys){
	    		if(treeMap.get(key).getOwner() != gameMap.getMyPlayerId() && key < i)
	    		{
	    				j++;
	    		}
	    	}
			return j;
		}
		public static Value createValue(Planet planet, GameMap gameMap, int x) {
			Map<Double,Planet> everyPlanetByDistance = gameMap.nearbyPlanetsByDistance(planet);
			Map<Double,Planet> treeMap = new TreeMap<Double, Planet>(everyPlanetByDistance);
	    	Set<Double> keys = treeMap.keySet();
	    	boolean tog = true;
	    	int pl = 0;
	    	int dk = 0;
	    	Planet enemy = null;
	    	for(Double key: keys){
	    		Planet cycle = treeMap.get(key);
	    		if(planet.getDistanceTo(cycle) < x)
	    		{
	    			pl++;
	    			dk = dk + cycle.getDockingSpots();
	    			if(tog && planet.getDistanceTo(cycle) < x-17 &&cycle.isOwned() && cycle.getOwner() != gameMap.getMyPlayerId())
	    			{
	    				enemy = cycle;
	    				tog = false;
	    			}
	    		}
	    	}
	    	Value value = new Value(pl,dk,enemy);
	    	return value;
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
		public static Ship enemyWithin(GameMap gameMap,Ship ship, double key2)
		{
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(ship);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship cycle = treeMap.get(key);
	    		
	    		if (cycle.getOwner() != gameMap.getMyPlayerId() && cycle.getDockingStatus() == Ship.DockingStatus.Undocked) {
					if (key < key2) {
						return cycle;
					} 
					break;
				}
	    	}
	    	return null;
		}
		public static boolean moreAlliesNearby(Planet target, GameMap gameMap) {
			int ally = 0;
			int pats = 0;
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(target);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship cycle = treeMap.get(key);
	    		if(key < 17)
	    		{
	    			if(cycle.getOwner() == gameMap.getMyPlayerId())
	    			{
	    				ally++;
	    			}
	    			else
	    			{
	    				pats++;
	    			}
	    		}
	    	}
	    	return ally > pats+2;
		}
		public static ArrayList<Ship> alliesNearby(Ship ship, GameMap gameMap) {
			ArrayList<Ship> yes = new ArrayList<Ship>();
			Map<Double,Ship> everyShipByDistance = gameMap.nearbyShipsByDistance(ship);
			Map<Double,Ship> treeMap = new TreeMap<Double, Ship>(everyShipByDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship cycle = treeMap.get(key);
	    		if(key < 10 && cycle.getOwner() == gameMap.getMyPlayerId())
	    		{
	    				yes.add(cycle);
	    		}
	    	}
	    	return yes;
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

		public static Planet nearbyLargerPlanets(Planet target, GameMap gameMap, Ship ship, final Map<Planet,Value> planetValues, Position middle) {
			Planet avg1 = target;
			double avg = 0;
			double dis = ship.getDistanceTo(target);
			Value pv = null;
			int doc = 0;
			int num = 0;
			int plannum = 1;
			double dis1 = 0;
			Map<Double,Planet> everyEntityDistance = gameMap.nearbyPlanetsByDistance(ship);
	    	Map<Double,Planet> treeMap = new TreeMap<Double, Planet>(everyEntityDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Planet cycle = treeMap.get(key);
	    		if(key-dis <= 6)
	    		{
	    				pv = planetValues.get(cycle);
	    				doc =  pv.getDockingSpotsNearby();
	    				num = pv.getPlanetsNearby();
	    				if( num != 0 && ( doc/num  )> avg || num > (plannum*1.5) && target.getDistanceTo(middle) > dis1*1.25 )
	    				{
	    					avg1 = cycle;
	    					plannum = num;
	    					avg = (  doc/num  );
	    					dis1 = target.getDistanceTo(middle);
	    				}
	    				
	    		}
	    	}
			return avg1;
		}
		public static boolean approaching(Ship ship, GameMap gameMap,Ship target)
		{
			double angle = Math.atan2(ship.getYPos()-target.getYPos(), ship.getXPos()-target.getXPos());
			return target.getDegree()+250<angle && angle < target.getDegree()+290;
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
		public static double distance(Position endpoint, Position targetCenter)
	    {
	    	return Math.sqrt(Math.pow((endpoint.getXPos()-targetCenter.getXPos()), 2) + Math.pow((endpoint.getYPos()-targetCenter.getYPos()), 2));
	    }
		public static boolean allShipsDocked(GameMap gameMap) {
			int i = 0;
			for(Ship ship : gameMap.getAllShips())
			{
				if(ship.getOwner() == gameMap.getMyPlayerId() && ship.getDockingStatus() != Ship.DockingStatus.Undocked)
				{
					i++;
				}
			}
			return i == 3;
		}

		public static Ship getClosestAlly1(Ship target, GameMap gameMap) {
			Map<Double,Ship> everyEntityDistance = gameMap.nearbyShipsByDistance(target);
	    	Map<Double, Ship> treeMap = new TreeMap<Double, Ship>(everyEntityDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	for(Double key: keys){
	    		Ship ally = treeMap.get(key);
	    		if( ally.getId() == gameMap.getMyPlayerId() && ally.getDockingStatus() == DockingStatus.Undocked/*&& !enemiesNearby(ally,gameMap,key)*/ )
	    		{
	    			return ally;
	    		}
	    		if( key > 33) {
	    			break;
	    		}
	    	}
	    	return null;
		}
		public static Planet closestCenterPlanet(GameMap gameMap, Ship ship) {
			Map<Double,Planet> everyEntityDistance = gameMap.nearbyPlanetsByDistance(ship);
	    	Map<Double,Planet> treeMap = new TreeMap<Double, Planet>(everyEntityDistance);
	    	Set<Double> keys = treeMap.keySet(); 
	    	int i = 0;
	    	for(Double key: keys){
	    		i++;
	    		Planet cycle = treeMap.get(key);
	    		int id = cycle.getId();
				if ((id == 0 || id == 1 || id == 2 || id == 3)  && i <= 3 ) {
					return cycle;
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
	    		Ship enemy = treeMap.get(key);
	    		if(enemy.getId() != gameMap.getMyPlayerId() && key < 25)
	    		{
	    			return enemy;
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
			int counter = 0;
			for(Ship ships : closeShips.values())
			{
				if(ships.getOwner() == ship.getOwner() && ships.getDockingStatus() == Ship.DockingStatus.Undocked && ship.getDistanceTo(ships) < 16) //if ships near target is on the same team as the target and is mobile and is within 15 units of the target
				{
					counter++;
				}
			}
			return !(counter <=1);
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


		public static int playersLeft(GameMap gameMap) {
			List<Player> players = gameMap.getAllPlayers();
			int i = 0;
			for(Player player : players)
			{
				if(player.getShips().size() >2)
				{
					i++;
				}
					
			}
			return i;
		}

		public static double production(GameMap gameMap,int player1,int player2)
		{
			int p1 = gameMap.getAllPlayers().get(player1).getShips().size();
			int p2 = gameMap.getAllPlayers().get(player2).getShips().size();
			double pr1 = 0;
			double pr2 = 0;
			ArrayList<Planet> planets = gameMap.returnArrayOfPlanets();//0 is player 0, 1 is player 1, 2 is player 2, 3 is player 3
			for(Planet planet : planets)
			{
				if (planet.isOwned()) {
					if(planet.getOwner() == player1)
					{
						pr1 = pr1 + planet.getDockedShips().size();
					}
					if(planet.getOwner() == player2)
					{
						pr2 = pr2 + planet.getDockedShips().size();
					}
				}
			}
			pr1 = pr1/12;
			pr2 = pr2/12;
			return player1;
			
		}
		public static int greaterProduction(GameMap gameMap,int turn) {
			List<Player> players = gameMap.getAllPlayers();
			for(Player player : players)
			{
				if(player.getShips().size() >2)
				{
					
				}
					
			}
			return turn;
		}


		public static void createGhosts(GameMap gameMap) {
			int i = 500;
			List<Ship> ships = gameMap.getAllShips();
			for(Iterator<Ship> iterator = ships.iterator(); iterator.hasNext();)
			{
				Ship ship = iterator.next();
				if(ship.getOwner() == gameMap.getMyPlayerId() )
				{
					ArrayList<Ship> allies = alliesNearby(ship,gameMap);
					if(allies.size() >=3)
					{
						double x = 0;
						double y = 0;
						for(Ship shi : allies)
						{
							x = x +shi.getXPos();
							y = y + shi.getYPos();
						}
						x = x/allies.size();
						y = y/allies.size();
						GhostShip ghost = new GhostShip(4,i,x,y,Constants.MAX_SHIP_HEALTH,Constants.SHIP_RADIUS);
						gameMap.addShip(ghost);
						i++;
					}
				}
			}
			
		}
		public static void wipeGhosts(GameMap gameMap)
		{
			List<Ship> ships = gameMap.getAllShips();
			for(Ship ship : ships)
			{
				if(ship instanceof GhostShip)
				{
					GhostShip ss = (GhostShip) ship;
					gameMap.remove(ss);
				}
			}
		}


		


		


		


		
}
