import hlt.*;
import hlt.Ship.DockingStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class theDestroyer {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Rajul Alzayt");
        final ArrayList<Move> moveList = new ArrayList<>();
        ArrayList<Entity> targetedEntities = new ArrayList<>();
        ArrayList<Planet> targetedPlanets = new ArrayList<>();
        ArrayList<Ship> forceMove = new ArrayList<>();
        ArrayList<Ship> hasMove = new ArrayList<>();
        int myId = gameMap.getMyPlayerId();
        Writer writer = new Writer("testing.txt");
        int i = 0;
        for (;;) {
            moveList.clear();
            targetedEntities.clear();
            forceMove.clear();
            hasMove.clear();
            targetedPlanets.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());
            i++;
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
            	if(forceMove.contains(ship))
            	{
            		continue;
            	}
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }
                ThrustMove newThrustMove;
                	
                	Map<Double, Entity> everyEntityDistance = gameMap.nearbyEntitiesByDistance(ship);
                	Map<Double, Entity> treeMap = new TreeMap<Double, Entity>(everyEntityDistance);
                	Set<Double> keys = treeMap.keySet(); 
                	for(Double key: keys){
                		Entity entity = treeMap.get(key);
                		if(entity instanceof Ship)
                		{
                			Ship target = (Ship) entity;	
                			if (target.getOwner() != myId) {
								DockingStatus dock = target.getDockingStatus();
								if (dock == Ship.DockingStatus.Docked || dock == Ship.DockingStatus.Docking) {
									if (ship.getDistanceTo(target) <= 5.0) {
										if(nearbyShipsApproaching(gameMap,ship))
										{
											newThrustMove = new Navigation(ship, target).navigateAwayFrom(gameMap, target,
													Constants.MAX_SPEED, false, 90, Math.PI / 180);
											if (newThrustMove != null) {
												hasMove.add(ship);
												moveList.add(newThrustMove);
												break;
											}
										}
										newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,0);
										if (newThrustMove != null) {
											hasMove.add(ship);
											targetedEntities.add(target);
											moveList.add(newThrustMove);
											break;
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
								if (ship.getDistanceTo(target) <= 5.0) {
									newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,
											Constants.MAX_SPEED);
									if (newThrustMove != null) {
										hasMove.add(ship);
										targetedEntities.add(target);
										moveList.add(newThrustMove);
										break;
									}
								}
								if (allShipsTargeted(gameMap.getAllShips(),targetedEntities)) {
									newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,
											Constants.MAX_SPEED);
									Ship cst = closestToTarget(target,ship,gameMap,hasMove);
									if(!cst.equals(ship))
									{
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
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		
                		if(entity instanceof Planet) ///REWORK PLANET 
                		{
                			Planet target = (Planet) entity;
                			if(targetedPlanets.contains(target))
                			{
                				continue;
                			}
                			if(!target.isOwned())
							{
								if (true/*safeToDock(target,gameMap,hasMove)*/) {
									if (ship.canDock(target))
									{
										moveList.add(new DockMove(ship, target));
										targetedPlanets.add(target);
										break;
									}
									newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,
											Constants.MAX_SPEED);
									if (newThrustMove != null) {
										moveList.add(newThrustMove);
										break;
									} 
								}
								continue;
							}
                			if(target.getOwner() == myId && i > 25)
							{
                				if(!target.isFull()) 
                				{
                					if (Collections.frequency(targetedEntities, target) < (target.getDockingSpots() - target.getDockedShips().size())) {
										if (ship.canDock(target)) {
											moveList.add(new DockMove(ship, target));
											targetedPlanets.add(target);
											break;
										}
										newThrustMove = new Navigation(ship, target).navigateToDock(gameMap,
												Constants.MAX_SPEED);
										if (newThrustMove != null) {
											moveList.add(newThrustMove);
											targetedPlanets.add(target);
											break;
										} 
									}
                				}
								continue;
							}
                			if(target.getOwner() != gameMap.getMyPlayerId()) //If planet is owned by an enemy
                			{
                				if(target.getDockedShips().size() > 0)
                    			{
                					newThrustMove = new Navigation(ship,entity).navigateTowards(gameMap,(gameMap.getShip(target.getOwner(), (target.getDockedShips().get(0)))), Constants.MAX_SPEED, true, 90, Math.PI/180);
                					if (newThrustMove != null)
                					{
                						moveList.add(newThrustMove);
                    					break;
                					}
                    			}
                				else
                				{
                					moveList.add(new DockMove(ship, target));
                                    break;
                				}
                			}
                			
                		}
                	}
                
                }
	    /*        if(i == 1 && willShipsColide(moveList))		/WIP  Turn one check if collide at planet
	        	{
	            		
	        	}	*/
            	Networking.sendMoves(moveList);
            }
            
        }

	private static boolean safeToDock(Planet target, GameMap gameMap, ArrayList<Ship> hasMove) {
		double radius = target.getRadius() + 12;
		int i = 0;
		int j = 0;
		Map<Double,Entity> everyEntityDistance = gameMap.nearbyEntitiesByDistance(target);
    	Map<Double, Entity> treeMap = new TreeMap<Double, Entity>(everyEntityDistance);
    	Set<Double> keys = treeMap.keySet(); 
    	for(Double key: keys){
    		Entity entity = treeMap.get(key);
    		if(key > radius)
    		{
    			break;
    		}
    		else
    		{
    			if(entity instanceof Ship)
    			{
    				if(entity.getId() != gameMap.getMyPlayerId())
    				{
    					i++;
    				}
    				else
    				{
    					j++;
    				}
    			}
    		}
    	}
    	return j >= i;
	}

	private static boolean willShipsColide(ArrayList<Move> moveList) {
		for(Move x: moveList)
		{
			if(!(x.getType() == Move.MoveType.Thrust))
			{
				return false;
			}
		}
		for(Move x : moveList)
		{
			ThrustMove y = (ThrustMove) x;
			
		}
		return null != null;
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
			if(deg < 95 && deg > 85)
			{
				return true;
			}
		}
		return false;
	}

	private static int getFrequency(ArrayList<Entity> targetedEntities, Ship target) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static int nearbyAllies(Ship target) {
		// TODO Auto-generated method stub
		return 0;
	}
    }

