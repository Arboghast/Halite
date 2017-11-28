import hlt.*;
import hlt.Ship.DockingStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PlanetTest {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Rajul Alzayt");
        ArrayList<Move> moveList = new ArrayList<>(); // removed final keyword
        ArrayList<Entity> targetedEntities = new ArrayList<>();
        ArrayList<Planet> targetedPlanets = new ArrayList<>();
        ArrayList<Ship> forceMove = new ArrayList<>();
        ArrayList<Ship> hasMove = new ArrayList<>();
        ArrayList<Ship> priority = new ArrayList<>();
        int myId = gameMap.getMyPlayerId();
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
            gameMap.updateMap(Networking.readLineIntoMetadata());
            i++;
            if (i <= 25) {
				for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
					if (forceMove.contains(ship)) {
						continue;
					}
					if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
						if (ship.getHealth() != Constants.MAX_SHIP_HEALTH) {
							Ship enemy = beingAttacked(ship, gameMap);
							if (enemy != null) {
								priority.add(enemy);
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
							if (priority.contains(target) && !targetedEntities.contains(target)) {
								Ship closestBackup = getClosestAlly(target, gameMap, hasMove);
								if (closestBackup != null) {
									newThrustMove = new Navigation(closestBackup, target).navigateToAttack(gameMap,
											target, Constants.MAX_SPEED);
									if (newThrustMove != null) {
										hasMove.add(closestBackup);
										moveList.add(newThrustMove);
										forceMove.add(closestBackup);
										targetedEntities.add(target);
										continue;
									}
								}
							}
							if (target.getOwner() != myId) {
								DockingStatus dock = target.getDockingStatus();
								if (dock == Ship.DockingStatus.Docked || dock == Ship.DockingStatus.Docking) {
									if (ship.getDistanceTo(target) <= 5.9) {
										if (nearbyShipsApproaching(gameMap, ship)) {
											newThrustMove = new Navigation(ship, target).navigateAwayFrom(gameMap,
													target, Constants.MAX_SPEED, false, 90, Math.PI / 180);
											if (newThrustMove != null) {
												hasMove.add(ship);
												moveList.add(newThrustMove);
												break;
											}
										}

									}
									newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,
											Constants.MAX_SPEED);
									if (newThrustMove != null) {
										hasMove.add(ship);
										targetedEntities.add(target);
										moveList.add(newThrustMove);
										break;
									}
								}
								if (ship.getDistanceTo(target) <= 10) {
									newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,
											Constants.MAX_SPEED);
									if (newThrustMove != null) {
										hasMove.add(ship);
										targetedEntities.add(target);
										moveList.add(newThrustMove);
										break;
									}
								}
								if (allShipsTargeted(gameMap.getAllShips(), targetedEntities)) {
									newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,
											Constants.MAX_SPEED);
									Ship cst = closestToTarget(target, ship, gameMap, hasMove);
									if (!cst.equals(ship)) {
										if (!forceMove.contains(cst)) {
											newThrustMove = new Navigation(cst, target).navigateToAttack(gameMap,
													target, Constants.MAX_SPEED);
											if (newThrustMove != null) {
												hasMove.add(cst);
												moveList.add(newThrustMove);
												targetedEntities.add(cst);
												forceMove.add(cst);
												continue;
											}
										}
									}
								}
							}
							continue;
						}

						
						
						if (entity instanceof Planet) {
							Planet target = (Planet) entity;
							if (ship.canDock(target)) {
								moveList.add(new DockMove(ship, target));
								targetedPlanets.add(target);
								hasMove.add(ship);
								break;
							}
							if (!target.isOwned()) {
									if(i < 10)
									{
										if(target.getDockingSpots() <= 3) 
										{
											if(Collections.frequency(targetedPlanets, target) < target.getDockingSpots()-1)
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
										else
										{
											
											if(earlyGame == null)
											{
												earlyGame = nearbyLargerPlanets(target,gameMap);
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
									
									Planet lPlanet = nearbyLargerPlanets(target,gameMap); 
									if(!lPlanet.equals(target))
									{
										if(Collections.frequency(targetedPlanets, lPlanet) < lPlanet.getDockingSpots())
										{
											newThrustMove = new Navigation(ship, lPlanet).navigateToDock(gameMap,Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(lPlanet);
												hasMove.add(ship);
												break;
											}
										}
										else if(Collections.frequency(targetedPlanets, target) < target.getDockingSpots())
										{
											newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
										}
										continue;
									}
									else
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
										continue;
									}
									
								}
							if (target.getOwner() == myId) {
								if(!target.isFull())
								{
									if(Collections.frequency(targetedPlanets, target) + target.getDockedShips().size() < target.getDockingSpots()) //DOES NOT ACCOUNT FOR DOCKING SHIPS
									{
										newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,Constants.MAX_SPEED);
										if (newThrustMove != null) {
											moveList.add(newThrustMove);
											targetedPlanets.add(target);
											hasMove.add(ship);
											break;
										}
									}
									continue;
								}
								continue;
							
							}
							else
							{
								if (target.getDockedShips().size() > 0) {
									Ship enemy = gameMap.getShip(target.getOwner(), (target.getDockedShips().get(0)));
									if (ship.getDistanceTo(target) <= 7.05) {
										if (nearbyShipsApproaching(gameMap, ship)) {
											newThrustMove = new Navigation(ship, enemy).navigateAwayFrom(gameMap, enemy,
													Constants.MAX_SPEED, false, 90, Math.PI / 180);
											if (newThrustMove != null) {
												hasMove.add(ship);
												moveList.add(newThrustMove);
												break;
											}
										}
										newThrustMove = new Navigation(ship, enemy).navigateToAttack(gameMap, enemy,
												Constants.MAX_SPEED);
										if (newThrustMove != null) {
											hasMove.add(ship);
											moveList.add(newThrustMove);
											targetedEntities.add(enemy);
											break;
										}
									}
									newThrustMove = new Navigation(ship, enemy).navigateTowards(gameMap, enemy,
											Constants.MAX_SPEED, true, 90, Math.PI / 180);
									if (newThrustMove != null) {
										moveList.add(newThrustMove);
										targetedEntities.add(enemy);
										hasMove.add(ship);
										break;
									}
								} else {
									moveList.add(new DockMove(ship, target));
									targetedPlanets.add(target);
									hasMove.add(ship);
									break;
								}
						}
					}
				}
			} 
            else
            {
            	for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
					if (forceMove.contains(ship)) {
						continue;
					}
					if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
						if (ship.getHealth() != Constants.MAX_SHIP_HEALTH) {
							Ship enemy = beingAttacked(ship, gameMap);
							if (enemy != null) {
								priority.add(enemy);
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
							if (priority.contains(target) && !targetedEntities.contains(target)) {
								Ship closestBackup = getClosestAlly(target, gameMap, hasMove);
								if (closestBackup != null) {
									newThrustMove = new Navigation(closestBackup, target).navigateToAttack(gameMap,
											target, Constants.MAX_SPEED);
									if (newThrustMove != null) {
										hasMove.add(closestBackup);
										moveList.add(newThrustMove);
										forceMove.add(closestBackup);
										targetedEntities.add(target);
										continue;
									}
								}
							}
							if (target.getOwner() != myId) {
								DockingStatus dock = target.getDockingStatus();
								if (dock == Ship.DockingStatus.Docked || dock == Ship.DockingStatus.Docking) {
									if (ship.getDistanceTo(target) <= 5.9) {
										if (nearbyShipsApproaching(gameMap, ship)) {
											newThrustMove = new Navigation(ship, target).navigateAwayFrom(gameMap,
													target, Constants.MAX_SPEED, false, 90, Math.PI / 180);
											if (newThrustMove != null) {
												hasMove.add(ship);
												moveList.add(newThrustMove);
												break;
											}
										}

									}
									newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,
											Constants.MAX_SPEED);
									if (newThrustMove != null) {
										hasMove.add(ship);
										targetedEntities.add(target);
										moveList.add(newThrustMove);
										break;
									}
								}
								if (ship.getDistanceTo(target) <= 10) {
									newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,
											Constants.MAX_SPEED);
									if (newThrustMove != null) {
										hasMove.add(ship);
										targetedEntities.add(target);
										moveList.add(newThrustMove);
										break;
									}
								}
								if (allShipsTargeted(gameMap.getAllShips(), targetedEntities)) {
									newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,
											Constants.MAX_SPEED);
									Ship cst = closestToTarget(target, ship, gameMap, hasMove);
									if (!cst.equals(ship)) {
										if (!forceMove.contains(cst)) {
											newThrustMove = new Navigation(cst, target).navigateToAttack(gameMap,
													target, Constants.MAX_SPEED);
											if (newThrustMove != null) {
												hasMove.add(cst);
												moveList.add(newThrustMove);
												targetedEntities.add(cst);
												forceMove.add(cst);
												continue;
											}
										}
									}
								}
							}
							/*	if
								{
									
								}
							*/ continue;
						}

						if (entity instanceof Planet) {
							Planet target = (Planet) entity;
							if (i > 25 && targetedPlanets.contains(target)) {
								continue;
							}
							if (!target.isOwned()) {
								if (i < 25 || safeToDock(target, gameMap)) {
									if (i < 25) {
										Ship enemyTroll = amIGettingBamboozled(ship, gameMap);
										if (enemyTroll == null) {
											if (ship.canDock(target)) {
												moveList.add(new DockMove(ship, target));
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
											newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,
													Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
										} else {
											newThrustMove = new Navigation(ship, enemyTroll).navigateToAttack(gameMap,
													enemyTroll, Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedEntities.add(enemyTroll);
												hasMove.add(ship);
												break;
											}
										}
									}
									if (ship.canDock(target)) {
										moveList.add(new DockMove(ship, target));
										targetedPlanets.add(target);
										hasMove.add(ship);
										break;
									}
									newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,
											Constants.MAX_SPEED);
									if (newThrustMove != null) {
										moveList.add(newThrustMove);
										targetedPlanets.add(target);
										hasMove.add(ship);
										break;
									}
								}
								continue;
							}
							if (target.getOwner() == myId) {
								if (i < 5) {
									if (!target.isFull() && targetedPlanets.size() > 1) {
										if (Collections.frequency(targetedEntities,
												target) < (target.getDockingSpots() - target.getDockedShips().size())) {
											if (ship.canDock(target)) {
												moveList.add(new DockMove(ship, target));
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
											newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,
													Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
										}
										continue;
									}
									continue;
								}
								if (i > 5) {
									if (!target.isFull()) {
										if (Collections.frequency(targetedEntities,
												target) < (target.getDockingSpots() - target.getDockedShips().size())) {
											if (ship.canDock(target)) {
												moveList.add(new DockMove(ship, target));
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
											newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,
													Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
										}
										continue;
									}
									continue;
								}

							}
							if (target.getOwner() != gameMap.getMyPlayerId()) {
								if (target.getDockedShips().size() > 0) {
									Ship enemy = gameMap.getShip(target.getOwner(), (target.getDockedShips().get(0)));
									if (ship.getDistanceTo(target) <= 7.05) {
										if (nearbyShipsApproaching(gameMap, ship)) {
											newThrustMove = new Navigation(ship, enemy).navigateAwayFrom(gameMap, enemy,
													Constants.MAX_SPEED, false, 90, Math.PI / 180);
											if (newThrustMove != null) {
												hasMove.add(ship);
												moveList.add(newThrustMove);
												break;
											}
										}
										newThrustMove = new Navigation(ship, enemy).navigateToAttack(gameMap, enemy,
												Constants.MAX_SPEED);
										if (newThrustMove != null) {
											hasMove.add(ship);
											moveList.add(newThrustMove);
											targetedEntities.add(enemy);
											break;
										}
									}
									newThrustMove = new Navigation(ship, enemy).navigateTowards(gameMap, enemy,
											Constants.MAX_SPEED, true, 90, Math.PI / 180);
									if (newThrustMove != null) {
										moveList.add(newThrustMove);
										targetedEntities.add(enemy);
										hasMove.add(ship);
										break;
									}
								} else {
									moveList.add(new DockMove(ship, target));
									targetedPlanets.add(target);
									hasMove.add(ship);
									break;
								}
							}

						}
					}

				} 
            }
				if(i < 3)		
	        	{
	            	moveList = willShipsCollide(moveList);
	        	}	
            	Networking.sendMoves(moveList);
            }
            
        }

	private static Ship getClosestAlly(Ship target, GameMap gameMap, ArrayList<Ship> hasMove) {
		Map<Double,Ship> everyEntityDistance = gameMap.nearbyShipsByDistance(target);
    	Map<Double, Ship> treeMap = new TreeMap<Double, Ship>(everyEntityDistance);
    	Set<Double> keys = treeMap.keySet(); 
    	for(Double key: keys){
    		Ship ally = treeMap.get(key);
    		if(ally.getId() == gameMap.getMyPlayerId() && !hasMove.contains(ally))
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
    		if(key < 10)
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
    		if(hasMove.contains(loop))
    		{
    			continue;
    		}
    		if(loop.equals(ship))
    		{
    			return ship;
    		}
    		return loop;
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

