import hlt.*;
import hlt.Ship.DockingStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
        ArrayList<Ship> hasMove = new ArrayList<>();
        ArrayList<Planet> safeToDock = new ArrayList<>();
        ArrayList<Ship> rogue = new ArrayList<>();
        Map<Planet,Value> planetValues = new HashMap<>();//number of planets nearby, number of docking spots for the nearby planets total, if there is an enemy planet directly/closeby
        int myId = gameMap.getMyPlayerId();  //if enemyplanet nearby, check enemies production level, if ours is grater, go to cap it ,else focus on another planet
        int players = gameMap.getAllPlayers().size();
        boolean bullRushToggle = false;
        Planet earlyGame = null;
        boolean allShipsDocked = true;
        int safetyZone;
        Position middle = new Position(gameMap.getWidth()/2,gameMap.getHeight()/2);
        Position one = new Position(gameMap.getWidth(),gameMap.getHeight());
        Position two = new Position(gameMap.getWidth(),0);
        Position three = new Position(0,0);
        Position four = new Position(0,gameMap.getHeight());
        
        Writer writer = new Writer("testing.txt");
        int i = 0;
        for (;;) {
            moveList.clear();
            targetedEntities.clear();
            hasMove.clear();
            targetedPlanets.clear();  
            safeToDock.clear();
            planetValues.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());
            i++;
            players = tools.playersLeft(gameMap);
            if(i > 150)
            {
            	safetyZone = 22;
            }
            else
            {
            	safetyZone = 5+  (7*  (  ((int)(i/60)) + 1)   );
            }
            tools.populateList(safeToDock,gameMap, safetyZone ); //45 is a test number, can change
            tools.updateGameState(planetValues,gameMap);
			for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
				if (hasMove.contains(ship)) {
					continue;
				}
				
				if(i>45 && gameMap.getMyPlayer().getShips().size() < 10 && gameMap.returnMyPlanets() <=2)
				{
					if(ship.getDockingStatus() != Ship.DockingStatus.Undocked)
					{
						moveList.add(new UndockMove(ship));
					}
					else
					{
						int sw= tools.nearWall(gameMap,ship); //1 = x 0 , 2 == x max, 3 = y 0, 4 = y max
						if(sw != 0)
						{
							double y = ship.getYPos();
							double x = ship.getXPos();
							int a = gameMap.getHeight()/2;
							int b = gameMap.getWidth()/2;
							ThrustMove newThrustMove = null;
							if(sw == 1 && y >a || sw == 4 && x <= b)
							{
								newThrustMove = new Navigation(ship,ship).navigateToAttack(gameMap,four, Constants.MAX_SPEED);
							}
							if(sw == 2 && y > a || sw == 4 && x > b)
							{
								newThrustMove = new Navigation(ship,ship).navigateToAttack(gameMap,one, Constants.MAX_SPEED);
							}
							if(sw == 3 && x >b|| sw == 2 && y <= a)
							{
								newThrustMove = new Navigation(ship,ship).navigateToAttack(gameMap,two, Constants.MAX_SPEED);
							}
							if(sw == 1 && y <= a || sw == 3 && x <= b)
							{
								newThrustMove = new Navigation(ship,ship).navigateToAttack(gameMap,three, Constants.MAX_SPEED);
							}
							
							
							if (newThrustMove != null) {
								hasMove.add(ship);
								moveList.add(newThrustMove);
								break;
							}
						}
						else
						{
							Position position = tools.closestWall(gameMap,ship);
							ThrustMove newThrustMove = new Navigation(ship,ship).navigateToAttack(gameMap,position, Constants.MAX_SPEED);
							if (newThrustMove != null) {
								hasMove.add(ship);
								moveList.add(newThrustMove);
								break;
							}
						}
					}
				}
				if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
					Ship enemy = tools.beingAttacked(ship, gameMap);
					if (enemy != null) {
						Ship closestBackup = tools.getClosestAlly1(enemy, gameMap);
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
							if (dock != DockingStatus.Undocked ) {
									if (!tools.nearbyShipsApproaching(gameMap, target)) {
										newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,
												Constants.MAX_SPEED);
										if (newThrustMove != null) {
											hasMove.add(ship);
											targetedEntities.add(target);
											moveList.add(newThrustMove);
											break;
										} 
									}
									continue;
							}
							
							
							//health of ship and target aka all this code below should be used when making an attack command.
							if (ship.getDistanceTo(target)<=11) { //within range to collide in 1 turn assuming
								double health = ship.getHealth()/target.getHealth();
								if(health < .52)// or .76 for 3/4 health left, .52 is 2/4 health remaining
								{
									newThrustMove = new Navigation(ship, target).navigateTowardsE(gameMap, target,Constants.MAX_SPEED, true, Constants.MAX_CORRECTIONS, Math.PI / 180);//////
									if (newThrustMove != null) {
										hasMove.add(ship);
										targetedEntities.add(target);
										moveList.add(newThrustMove);
										break;
									}
								}
								else
								{
									if (!tools.isAttackingDockedAlly(gameMap, target)) {
										Ship alli = tools.getClosestAlly(ship, gameMap);
										if (alli != null) {
											newThrustMove = new Navigation(ship, alli).navigateAwayFrom(gameMap, alli,Constants.MAX_SPEED, true, Constants.MAX_CORRECTIONS,Math.PI / 180);
											if (newThrustMove != null) {
												hasMove.add(ship);
												targetedEntities.add(target);
												moveList.add(newThrustMove);
												break;
											}
										} 
									}
								}
							}
							
							
							
							
							if (ship.getDistanceTo(target) < 100) { //makes ships go only for ships, not unowned plannets
								if (!targetedEntities.contains(target)) {
									newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,Constants.MAX_SPEED);
									if (newThrustMove != null) {
										hasMove.add(ship);
										targetedEntities.add(target);
										moveList.add(newThrustMove);
										break;
									}
								}
								continue;
							}
							else
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
						
						
				
					    
					}
					
					
					
					
						
					
					
					if (entity instanceof Planet) {
						
						
						if(bullRushToggle)
						{
							continue;
						}
						
						
						
						Planet target = (Planet) entity;
						if (ship.canDock(target) && !target.isFull() && ( !target.isOwned() || target.getOwner() == myId) )  {
							if(safeToDock.contains(target))
							{
								ship.setTargetPosition(ship.getClosestPoint(target));
								moveList.add(new DockMove(ship, target));
								targetedPlanets.add(target);
								hasMove.add(ship);
								break;
							}
							continue;
						}
						
						
						
						if (!target.isOwned()) {
							
							
							
								if(allShipsDocked && i < 10)
								{
									
									if(tools.allShipsDocked(gameMap))
									{
										allShipsDocked = false;
									}
									
									if(players == 2) //Section untested because only testable in 1v1's
									{
										if (tools.enemiesNearby(ship,gameMap,54)) {
											bullRushToggle = true;
											continue;
										}
										else
										{
											Planet center = tools.nearbyLargerPlanets(target, gameMap, ship);
											if(center != null)
											{
												newThrustMove = new Navigation(ship, center).navigateToDockEG(gameMap,Constants.MAX_SPEED);
												if (newThrustMove != null) {
													moveList.add(newThrustMove);
													targetedPlanets.add(center);
													hasMove.add(ship);
													break;
												}
											}
											continue;
										}
									}
									
									
									
									
									if(earlyGame == null)
									{
										//earlyGame = tools.nearbyLargerPlanets(target,gameMap,ship,planetValues,middle);
										earlyGame = tools.nearbyLargerPlanets(target, gameMap, ship);
									}
									if( earlyGame.getDockingSpots() <= 3) 
									{
										if(Collections.frequency(targetedPlanets, earlyGame) < earlyGame.getDockingSpots())
										{
											newThrustMove = new Navigation(ship, earlyGame).navigateToDockEG(gameMap,Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(earlyGame);
												hasMove.add(ship);
												break;
											}
										}
										else
										{
											newThrustMove = new Navigation(ship, target).navigateToDockEG(gameMap,Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(target);
												hasMove.add(ship);
												break;
											}
										}
									}
									newThrustMove = new Navigation(ship, earlyGame).navigateToDockEG(gameMap,Constants.MAX_SPEED);
									if (newThrustMove != null) {
										moveList.add(newThrustMove);
										targetedPlanets.add(earlyGame);
										hasMove.add(ship);
										break;
									}
									
									
									
								}
								
								
								
								
								
								if (i < 50) { 
									ArrayList<Integer> cents = new ArrayList<Integer>();
									cents.add(0);
									cents.add(1);
									cents.add(2);
									cents.add(3);
									if(players == 2)
									{
										Planet planet = tools.closestCenterPlanet(gameMap, ship);
										if(Collections.frequency(targetedPlanets, planet) <=1)
										{
											newThrustMove = new Navigation(ship, planet).navigateToDockEG(gameMap,Constants.MAX_SPEED);
											if (newThrustMove != null) {
												moveList.add(newThrustMove);
												targetedPlanets.add(planet);
												hasMove.add(ship);
												break;
											}
										}
									}
									if (tools.enemiesNearby(target, gameMap, safetyZone + 11) || cents.contains(target.getId())) { //adjust +14 also if needed
										continue;
									}
									if (Collections.frequency(targetedPlanets, target) < target.getDockingSpots()) {
										newThrustMove = new Navigation(ship, target).navigateToDockEG(gameMap,Constants.MAX_SPEED);
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
									if (tools.enemiesNearby(target, gameMap, safetyZone)) {
										continue;
									}
									if (Collections.frequency(targetedPlanets, target) < target.getDockingSpots()) {
										newThrustMove = new Navigation(ship, target).navigateToDockEG(gameMap,Constants.MAX_SPEED);
										if (newThrustMove != null) {
											moveList.add(newThrustMove);
											targetedPlanets.add(target);
											hasMove.add(ship);
											break;
										}
									} 
								}
								
								
								
								
								
								
								
							}
						
						
						
						
						
							if (target.getOwner() == myId) {  //this is or my own planets
								if(!target.isFull() && !tools.enemiesNearby(target,gameMap,safetyZone))
								{
										if (Collections.frequency(targetedPlanets, target)+ target.getDockedShips().size() < target.getDockingSpots()) //DOES NOT ACCOUNT FOR DOCKING SHIPS
										{
											newThrustMove = new Navigation(ship, target).navigateToDockEG(gameMap,Constants.MAX_SPEED);
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
							else //this is for enemy planets
							{
								//code for planet suicide?
								
							}
								
								
								
									
					        continue;
					        
					        
					  } 
				   }
            	}
             Networking.sendMoves(moveList);
           }    
    	}
     
    }


