import hlt.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TheDestroyer {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Rajul Alzayt");
        final ArrayList<Move> moveList = new ArrayList<>();
        ArrayList<Entity> targetedEntities = new ArrayList<>();
        Writer writer = new Writer("log.txt");
        try {
			writer.writeToFile("hello");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int i = 0;
        for (;;) {
            moveList.clear();
            targetedEntities.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());
            i++;
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {	//TODO- Fix start of the game ship collision leaving only 1 ship
            																				//When enemy ships are next to you and our ship slows down to attack it, the enemy just goes past you, leaving you unable to catch it          	
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
                			if(entity.getOwner() != gameMap.getMyPlayerId() && !targetedEntities.contains(entity)) //If ship is not mine and is not targeted
                			{
                				if((ship.getDistanceTo(entity) <= 10.0))//ship will slow down when in range of an enemy ship to attack it, instead of ram into it
                				{
                					
                					newThrustMove = new Navigation(ship,entity).navigateTowards(gameMap, entity, 1, true, 90 , Math.PI/180);//!!!!
                					if (newThrustMove != null)
                					{
                						targetedEntities.add(entity);
                						moveList.add(newThrustMove);
                    					break;
                					}
                				}
                				else {
                					newThrustMove = new Navigation(ship,entity).navigateAtMaxSpeed(gameMap, entity); // can return null
                					if (newThrustMove != null)
                					{
                						moveList.add(newThrustMove);
                						targetedEntities.add(entity);
                						break;
                					}
                				}
                			}	
                			continue;
                		}
                		if(entity instanceof Planet)
                		{
                			Planet planet = (Planet) entity;
                			if(!planet.isOwned()) // If planet is not owned 
							{
								if(ship.canDock(planet)) //check if its full and dock-able
								{
	                					moveList.add(new DockMove(ship, planet));
	                                    break;
								}
								else
								{
									newThrustMove = new Navigation(ship,planet).navigateToDock(gameMap,Constants.MAX_SPEED);
									if (newThrustMove != null)
									{
										moveList.add(newThrustMove);
										break;
									}
								}
							}
                			if(planet.getOwner() == gameMap.getMyPlayerId() && i > 25) // If planet is owned by me and turn is greater than 20
							{
                				if(!planet.isFull()) //check if its full
                				{
                					if(ship.canDock(planet)) //check if its dock-able
    								{
    	                					moveList.add(new DockMove(ship, planet));
    	                                    break;
    								}
                					else if (Collections.frequency(targetedEntities, planet) < planet.getDockingSpots())
                					{
                						newThrustMove = new Navigation(ship,planet).navigateToDock(gameMap,Constants.MAX_SPEED);
                						if (newThrustMove != null)
                						{
                							targetedEntities.add(planet);
                							moveList.add(newThrustMove);
                							break;
                						}
                					}
                				}
								continue;
							}
                			if(planet.getOwner() != gameMap.getMyPlayerId()) //If planet is owned by an enemy
                			{
                				if(planet.getDockedShips().size() > 0) // If ships are docked on the planet
                    			{
                					Ship dockedShip = (gameMap.getShip(planet.getOwner(), (planet.getDockedShips().get(0)))); //!!!
                					newThrustMove = new Navigation(ship,dockedShip).navigateAtMaxSpeed(gameMap,dockedShip);
                					if (newThrustMove != null)
                					{
                						moveList.add(newThrustMove);
                    					break;
                					}
                    			}
                				else //If no ships are docked on the planet for some reason, take it over
                				{
                					moveList.add(new DockMove(ship, planet));
                                    break;
                				}
                			}
                			
                		}
                	}
                
                }
            	Networking.sendMoves(moveList);
            }
            
        }
    }

