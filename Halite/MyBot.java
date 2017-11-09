import hlt.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("The Destroyer");
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
            																				//When enemy ships are next to you and our ship slows down to attack it, the enemy just goes past you, leaving you unable to catch up
            	
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
                			if(entity.getOwner() != gameMap.getMyPlayerId() && !targetedEntities.contains(entity)) //If ship is not mine
                			{
                				if((ship.getDistanceTo(entity) <= 10.0))//ship will slow down when in range of an enemy ship to attack it, instead of ram into it
                				{
                					newThrustMove = new Navigation(ship,entity).navigateTowards(gameMap, entity, 1, true, 90 , Math.PI/180);
                					if (newThrustMove != null)
                					{
                						targetedEntities.add(entity);
                						moveList.add(newThrustMove);
                    					break;
                					}
                				}
                				newThrustMove = new Navigation(ship,entity).navigateTowards(gameMap, entity, Constants.MAX_SPEED, true, 90 , Math.PI/180); // can return null
                				if (newThrustMove != null)
            					{
            						moveList.add(newThrustMove);
            						targetedEntities.add(entity);
                					break;
            					}
                			}	
                			continue;
                		}
                		if(entity instanceof Planet)
                		{
                			if(!((Planet) entity).isOwned()) // If planet is not owned gameMap.getPlanet(entity.getId())
							{
								if((ship.canDock((Planet) entity))) //check if its full and dockable
								{
	                					moveList.add(new DockMove(ship, (Planet) entity));
	                                    break;
								}
								newThrustMove = new Navigation(ship,entity).navigateToDock(gameMap,Constants.MAX_SPEED);
								if (newThrustMove != null)
            					{
            						moveList.add(newThrustMove);
                					break;
            					}
							}
                			if(((Planet) entity).getOwner() == gameMap.getMyPlayerId() && i > 20) // If planet is owned gameMap.getPlanet(entity.getId())
							{
                				if(!((Planet) entity).isFull()) //check if its full
                				{
                					if((ship.canDock((Planet) entity))) //check if its dockable
    								{
    	                					moveList.add(new DockMove(ship, (Planet) entity));
    	                                    break;
    								}
    								newThrustMove = new Navigation(ship,entity).navigateToDock(gameMap,Constants.MAX_SPEED);
    								if (newThrustMove != null)
                					{
                						moveList.add(newThrustMove);
                    					break;
                					}
                				}
								continue;
							}
                			if(((Planet) entity).getOwner() != gameMap.getMyPlayerId()) //If planet is owned by an enemy
                			{
                				if(((Planet) entity).getDockedShips().size() > 0)
                    			{
                					newThrustMove = new Navigation(ship,entity).navigateTowards(gameMap,(gameMap.getShip(((Planet) entity).getOwner(), (((Planet) entity).getDockedShips().get(0)))), Constants.MAX_SPEED, true, 90, Math.PI/180);
                					if (newThrustMove != null)
                					{
                						moveList.add(newThrustMove);
                    					break;
                					}
                    			}
                				else
                				{
                					moveList.add(new DockMove(ship, ((Planet) entity)));
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

