import hlt.*;
import hlt.Ship.DockingStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Rajul Alzayt");
        ArrayList<Move> moveList = new ArrayList<>(); 
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
       // Writer writer = new Writer("testing.txt");
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
            tools.populateList(safeToDock,gameMap);
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
				if (forceMove.contains(ship)) {
					continue;
				}
				if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
					
					Ship enemy = tools.beingAttacked(ship, gameMap);
					if (enemy != null) {
						Ship closestBackup = tools.getClosestAlly(enemy, gameMap, forceMove);
						if (closestBackup != null) {
							ThrustMove newThrustMove = new Navigation(closestBackup, enemy).navigateToAttack(gameMap,enemy, Constants.MAX_SPEED);
							if (newThrustMove != null) {
								if (hasMove.contains(closestBackup)) {
									moveList.set(hasMove.indexOf(closestBackup),newThrustMove);
									closestBackup.setTargetPosition(tools.newTarget(newThrustMove));
									targetedEntities.add(enemy);
									continue;
								}
								closestBackup.setTargetPosition(tools.newTarget(newThrustMove));
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
									if (tools.nearbyShipsApproaching(gameMap, ship)) {
										newThrustMove = new Navigation(ship, target).navigateAwayFrom(gameMap,target, Constants.MAX_SPEED, false, 90, Math.PI / 180);
										if (newThrustMove != null) {
											ship.setTargetPosition(tools.newTarget(newThrustMove));
											hasMove.add(ship);
											moveList.add(newThrustMove);
											break;
										}
									}
								}
								newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,Constants.MAX_SPEED);
								if (newThrustMove != null) {
									ship.setTargetPosition(tools.newTarget(newThrustMove));
									hasMove.add(ship);
									targetedEntities.add(target);
									moveList.add(newThrustMove);
									break;
								}
							}
							if (ship.getDistanceTo(target) <= 10) {
								if ((ship.getHealth() < target.getHealth()) ) {
									newThrustMove = new Navigation(ship, target).navigateTowardsE(gameMap, target,Constants.MAX_SPEED, false, 90, Math.PI / 180);//////
									if (newThrustMove != null) {
										ship.setTargetPosition(tools.newTarget(newThrustMove));
										hasMove.add(ship);
										targetedEntities.add(target);
										moveList.add(newThrustMove);
										break;
									}
								}
							}
							if (!targetedEntities.contains(target)) {
							newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,Constants.MAX_SPEED);
								if (newThrustMove != null) {
									ship.setTargetPosition(tools.newTarget(newThrustMove));
									hasMove.add(ship);
									targetedEntities.add(target);
									moveList.add(newThrustMove);
									break;
								}
								continue;
							}
							if (tools.allShipsTargeted(gameMap.getAllShips(), targetedEntities))
							{
								newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap,target, Constants.MAX_SPEED);
								if (newThrustMove != null) {
									ship.setTargetPosition(tools.newTarget(newThrustMove));
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
									if(players == 2)
									{
										if (tools.enemiesNearby(ship,gameMap,49)) {
											bullRushToggle = true;
											continue;
										}
										else
										{
											int id = target.getId();
											if ((id == 0 || id == 1 || id == 2 || id == 3)) {
												newThrustMove = new Navigation(ship, target).navigateToDockEG(gameMap,Constants.MAX_SPEED);
												if (newThrustMove != null) {
													ship.setTargetPosition(ship.getClosestPoint(target));
													moveList.add(newThrustMove);
													targetedPlanets.add(target);
													hasMove.add(ship);
													break;
												}
											}
										}
									}
									if( target.getDockingSpots() <= 3) 
									{
										if(Collections.frequency(targetedPlanets, target) < target.getDockingSpots())
										{
											newThrustMove = new Navigation(ship, target).navigateToDockEG(gameMap,Constants.MAX_SPEED);
											if (newThrustMove != null) {
												ship.setTargetPosition(ship.getClosestPoint(target));
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
											earlyGame = tools.nearbyLargerPlanets(target,gameMap,ship);
										}
										newThrustMove = new Navigation(ship, earlyGame).navigateToDockEG(gameMap,Constants.MAX_SPEED);
										if (newThrustMove != null) {
											ship.setTargetPosition(ship.getClosestPoint(earlyGame));
											moveList.add(newThrustMove);
											targetedPlanets.add(earlyGame);
											hasMove.add(ship);
											break;
										}
										 
									}
								}
								if (i <75) {
									if (i < 40 && gameMap.returnMyPlanets() < ( (int) planets/4) ) {
										if (tools.enemiesNearby(target, gameMap, 25.0)) {
											continue;
										}
										if (Collections.frequency(targetedPlanets, target) < target
												.getDockingSpots()) {
											newThrustMove = new Navigation(ship, target).navigateToDockEG(gameMap,
													Constants.MAX_SPEED);
											if (newThrustMove != null) {
												ship.setTargetPosition(ship.getClosestPoint(target));
												moveList.add(newThrustMove);
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
										} 
									} 
									else
									{
										if (tools.enemiesNearby(target, gameMap, 12.0)) {
											continue;
										}
										if (Collections.frequency(targetedPlanets, target) < target.getDockingSpots()) {
											newThrustMove = new Navigation(ship, target).navigateToDockEG(gameMap,
													Constants.MAX_SPEED);
											if (newThrustMove != null) {
												ship.setTargetPosition(ship.getClosestPoint(target));
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
									if(tools.enemiesNearby(target,gameMap,15.0))
									{
										continue;
									}
									if (Collections.frequency(targetedPlanets, target) < target.getDockingSpots()) {
										newThrustMove = new Navigation(ship, target).navigateToDockEG(gameMap,
												Constants.MAX_SPEED);
										if (newThrustMove != null) {
											ship.setTargetPosition(ship.getClosestPoint(target));
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
								if (!tools.enemiesNearby(target,gameMap,(double) (7+(i/4)) )) {
									if (Collections.frequency(targetedPlanets, target)+ target.getDockedShips().size() < target.getDockingSpots()) //DOES NOT ACCOUNT FOR DOCKING SHIPS
									{
										newThrustMove = new Navigation(ship, target).navigateToDockEG(gameMap,
												Constants.MAX_SPEED);
										if (newThrustMove != null) {
											ship.setTargetPosition(ship.getClosestPoint(target));
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
              Networking.sendMoves(moveList);
           }    
    	}
    }

