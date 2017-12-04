import hlt.*;
import hlt.Ship.DockingStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Rajul Alzayt");
        ArrayList<Move> moveList = new ArrayList<>(); // removed final keyword
        ArrayList<Entity> targetedEntities = new ArrayList<>();
        ArrayList<Planet> targetedPlanets = new ArrayList<>();
        ArrayList<Ship> forceMove = new ArrayList<>();
        ArrayList<Ship> hasMove = new ArrayList<>();
        ArrayList<Ship> priority = new ArrayList<>();
        ArrayList<Planet> safeToDock = new ArrayList<>();
        int myId = gameMap.getMyPlayerId();
        int planets = gameMap.getAllPlanets().size();
        int players = gameMap.getAllPlayers().size();
        boolean bullRushToggle = false;
        Planet earlyGame = null;
        Writer writer = new Writer("testing.txt");
        int i = 0;
        for (;;) {
            moveList.clear();
            targetedEntities.clear();
            forceMove.clear();
            hasMove.clear();
            targetedPlanets.clear();  
            priority.clear();
            safeToDock.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());
            i++;
            populateList(safeToDock,gameMap);
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
				if (forceMove.contains(ship)) {
					continue;
				}
				if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
					
					Ship enemy = beingAttacked(ship, gameMap);
					if (enemy != null) {
						Ship closestBackup = getClosestAlly(enemy, gameMap, forceMove);
						if (closestBackup != null) {
							ThrustMove newThrustMove = new Navigation(closestBackup, enemy).navigateToAttack(gameMap,enemy, Constants.MAX_SPEED);
							if (newThrustMove != null) {
								if (hasMove.contains(closestBackup)) {
									moveList.set(hasMove.indexOf(closestBackup),newThrustMove);
									targetedEntities.add(enemy);
									continue;
								}
								targetedEntities.add(enemy);
								hasMove.add(closestBackup);
								moveList.add(newThrustMove);
								forceMove.add(closestBackup);
								continue;
							}
						}
					}
					continue;
				}
				ThrustMove newThrustMove;

				Map<Double, Entity> everyEntityDistance = gameMap.nearbyEntitiesByDistance(ship);
				Map<Double, Entity> treeMap = new TreeMap<Double, Entity>(everyEntityDistance);
				Set<Double> keys = treeMap.keySet();
				for (Double key : keys) {
					Entity entity = treeMap.get(key);
					if (entity instanceof Ship) {
						Ship target = (Ship) entity;
						if (target.getOwner() != myId) {
							DockingStatus dock = target.getDockingStatus();
							if (dock == DockingStatus.Docked || dock == DockingStatus.Docking) {
								if (ship.getDistanceTo(target) <= 5.9) {
									if (nearbyShipsApproaching(gameMap, ship)) {
										newThrustMove = new Navigation(ship, target).navigateAwayFrom(gameMap,target, Constants.MAX_SPEED, false, 90, Math.PI / 180);
										if (newThrustMove != null) {
											hasMove.add(ship);
											moveList.add(newThrustMove);
											break;
										}
									}

								}
								newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,Constants.MAX_SPEED);
								if (newThrustMove != null) {
									hasMove.add(ship);
									targetedEntities.add(target);
									moveList.add(newThrustMove);
									break;
								}
							}
							if (ship.getDistanceTo(target) <= 9) {
								if ((ship.getHealth() < target.getHealth()) ) {
									newThrustMove = new Navigation(ship, target).navigateTowardsC(gameMap, target,Constants.MAX_SPEED, false, 90, Math.PI / 180);//////
									if (newThrustMove != null) {
										hasMove.add(ship);
										targetedEntities.add(target);
										moveList.add(newThrustMove);
										break;
									}
								}
							}
							if (!targetedEntities.contains(target)) {
							newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,Constants.MAX_SPEED);
							//	newThrustMove = new Navigation(ship,target).navigateToAttackWC(gameMap, target, Constants.MAX_SPEED, true, Constants.MAX_CORRECTIONS, false, Math.PI/180);
								if (newThrustMove != null) {
									hasMove.add(ship);
									targetedEntities.add(target);
									moveList.add(newThrustMove);
									break;
								}
								continue;
							}
							if (allShipsTargeted(gameMap.getAllShips(), targetedEntities))
							{
								newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap,target, Constants.MAX_SPEED);
								if (newThrustMove != null) {
									hasMove.add(ship);
									moveList.add(newThrustMove);
									targetedEntities.add(target);
									break;
									
								}
							}
						}
						continue;
					}

					
					
					if (entity instanceof Planet) {
						if(bullRushToggle)
						{
							continue;
						}
						Planet target = (Planet) entity;
						if (  ship.canDock(target) && !target.isFull() && ( !target.isOwned() || target.getOwner() == myId) )  {
							if(safeToDock.contains(target))
							{
								moveList.add(new DockMove(ship, target));
								targetedPlanets.add(target);
								hasMove.add(ship);
								break;
							}
							continue;
						}
						if (!target.isOwned()) {
								if(i < 10)
								{
									if(players == 2 && enemiesNearby(ship,gameMap,49))
									{
										bullRushToggle = true;
										continue;
									}
									int id = target.getId();
									if ((id == 0 || id == 1 || id == 2 || id == 3)) {
										newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,Constants.MAX_SPEED);
										if (newThrustMove != null) {
											moveList.add(newThrustMove);
											targetedPlanets.add(target);
											hasMove.add(ship);
											break;
										}
									}
									if( target.getDockingSpots() <= 3) 
									{
										if(Collections.frequency(targetedPlanets, target) < target.getDockingSpots())
										{
											newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
										}
										else
										{
											continue;
										}
									}
									else	//size/4 rounded down should be max?
									{
										
										if(earlyGame == null)
										{
											earlyGame = nearbyLargerPlanets(target,gameMap,ship);
										}
										newThrustMove = new Navigation(ship, earlyGame).navigateToDock(gameMap,Constants.MAX_SPEED);
										if (newThrustMove != null) {
											moveList.add(newThrustMove);
											targetedPlanets.add(earlyGame);
											hasMove.add(ship);
											break;
										}
										 
									}
								}
								if (i <75) {
									if (i < 40 && gameMap.returnMyPlanets() < ( (int) planets/4) ) {
										if (enemiesNearby(target, gameMap, 25.0)) {
											continue;
										}
										if (Collections.frequency(targetedPlanets, target) < target
												.getDockingSpots()) {
											newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,
													Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
										} 
									} 
									else
									{
										if (enemiesNearby(target, gameMap, 12.0)) {
											continue;
										}
										if (Collections.frequency(targetedPlanets, target) < target
												.getDockingSpots()) {
											newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,
													Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
										} 
									}
								}
								if(i > 75)
								{
									if(enemiesNearby(target,gameMap,15.0))
									{
										continue;
									}
									if (Collections.frequency(targetedPlanets, target) < target.getDockingSpots()) {
										newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,
												Constants.MAX_SPEED);
										if (newThrustMove != null) {
											moveList.add(newThrustMove);
											targetedPlanets.add(target);
											hasMove.add(ship);
											break;
										}
									} 
								}
							}
						if (target.getOwner() == myId) {
							if(!target.isFull())
							{
								if (!enemiesNearby(target,gameMap,(double) (7+(i/4)) )) {
									if (Collections.frequency(targetedPlanets, target)+ target.getDockedShips().size() < target.getDockingSpots()) //DOES NOT ACCOUNT FOR DOCKING SHIPS
									{
										newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,
												Constants.MAX_SPEED);
										if (newThrustMove != null) {
											moveList.add(newThrustMove);
											targetedPlanets.add(target);
											hasMove.add(ship);
											break;
										}
									} 
								}
								continue;
							}
							continue;
						}
						continue;
					}
				}
			}
				if(i < 2)		
	        	{
	            	moveList = willShipsCollide(moveList);
	        	}	
            	Networking.sendMoves(moveList);
            }
            
        }

	private static void valueNearbyPlanets(Ship ship, GameMap gameMap) {
		Planet planet = null;
		Map<Double,Planet> everyShipByDistance = gameMap.nearbyPlanetsByDistance(ship);
		Map<Double,Planet> treeMap = new TreeMap<Double, Planet>(everyShipByDistance);
    	Set<Double> keys = treeMap.keySet(); 
    	for(Double key: keys){
    		
    	}
	}

	private static void calculateValues(Map<Planet, Integer> planetValues) {
		Set<Planet> keys = planetValues.keySet(); 
    	for(Planet planet: keys){
    		
    	}
		
	}

	private static void populateList(ArrayList<Planet> safeToDock, GameMap gameMap) {
		Map<Integer,Planet> everyShipByDistance = gameMap.getAllPlanets();
    	Set<Integer> keys = everyShipByDistance.keySet();
    	for(Integer key: keys){
    		Planet cycle = everyShipByDistance.get(key);
    		if(!cycle.isOwned() || cycle.getOwner() == gameMap.getMyPlayerId())
    		{
    			if(!enemiesNearby(cycle,gameMap,12.0))
    			{
    				safeToDock.add(cycle);
    			}
    		}
    	}
		
	}

	private static boolean enemiesNearby(Planet ally, GameMap gameMap,Double key2) {
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
	private static boolean enemiesNearby(Ship ship, GameMap gameMap) {
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
	private static boolean enemiesNearby(Ship ship, GameMap gameMap, double x) {
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
	private static Ship closestDockedShip(Planet target, GameMap gameMap, Ship ship) {
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

	private static Ship dockingShipsNearby(Planet target, GameMap gameMap) {
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

	private static Planet nearbyLargerPlanets(Planet target, GameMap gameMap, Ship ship) {
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

	private static Ship getClosestAlly(Ship target, GameMap gameMap, ArrayList<Ship> forceMove) {
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
	private static Ship getClosestAlly(Ship target, GameMap gameMap) {
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

	private static Ship beingAttacked(Ship ship, GameMap gameMap) {
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

	private static ArrayList<Planet> scanTheMap(GameMap gameMap) {
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
	private static Ship amIGettingBamboozled(Ship ship, GameMap gameMap)
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
	private static boolean safeToDock(Planet target, GameMap gameMap) {
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

	private static boolean intersect(Position a, Position b, Position c, Position d)
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
	private static boolean CCW(Position x, Position y, Position z) {
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

	private static ArrayList<Move> willShipsCollide(ArrayList<Move> moveList) {
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

	private static boolean withinRange(int angle, int angle2) {
		int dif = Math.abs(angle - angle2);
		return dif < 6;
	}

	private static boolean allShipsTargeted(List<Ship> allShips, ArrayList<Entity> targetedEntities) {
		for(Ship x : allShips)
		{
			if(!targetedEntities.contains(x))
			{
				return false;
			}
		}
		return true;
	}

	private static Ship closestToTarget(Ship target, Ship ship, GameMap gameMap, ArrayList<Ship> hasMove) {
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

	private static boolean nearbyShipsApproaching(GameMap gameMap,Entity ship) {
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
	
    }

