import hlt.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("The Destroyer");
        final ArrayList<Move> moveList = new ArrayList<>();
        int i = 0;
        for (;;) {
            moveList.clear();
            gameMap.updateMap(Networking.readLineIntoMetadata());
            i++;
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }
                			//i 0-10 will be the start game code, separate from mid-game code
                	final ThrustMove newThrustMove;
                	Map<Double, Entity> everyEntityDistance = gameMap.nearbyEntitiesByDistance(ship);
                	Map<Double, Entity> treeMap = new TreeMap<Double, Entity>(everyEntityDistance);
                	Set<Double> keys = treeMap.keySet();
                	for(Double key: keys){
                		if(treeMap.get(key).getClass().equals(Ship.class))
                		{
                			if(treeMap.get(key).getOwner() != gameMap.getMyPlayerId())
                			{
                				if((ship.getDistanceTo(treeMap.get(key)) <= 10.0))
                				{
                					newThrustMove = new Navigation(ship,treeMap.get(key)).navigateTowards(gameMap, treeMap.get(key), 1, true, 90 , Math.PI/180);
                					moveList.add(newThrustMove);
                					break;
                				}
                				newThrustMove = new Navigation(ship,treeMap.get(key)).navigateTowards(gameMap, treeMap.get(key), Constants.MAX_SPEED, true, 90 , Math.PI/180);
            					moveList.add(newThrustMove);
            					break;
                			}	
                			continue;
                		}
                		if(treeMap.get(key).getClass().equals(Planet.class))
                		{
                			if(!gameMap.getPlanet(treeMap.get(key).getId()).isOwned())
							{
								if((ship.canDock(gameMap.getPlanet(treeMap.get(key).getId()))))
								{
	                					moveList.add(new DockMove(ship, gameMap.getPlanet(treeMap.get(key).getId())));
	                                    break;
								}
								newThrustMove = new Navigation(ship,treeMap.get(key)).navigateToDock(gameMap,Constants.MAX_SPEED);
								moveList.add(newThrustMove);
								break;
							}
                			if(gameMap.getPlanet(treeMap.get(key).getId()).getOwner() != gameMap.getMyPlayerId())
                			{
                				if(gameMap.getPlanet(treeMap.get(key).getId()).getDockedShips() != null)
                    			{
                					newThrustMove = new Navigation(ship,ship).navigateTowards(gameMap,(gameMap.getShip(gameMap.getPlanet(treeMap.get(key).getId()).getOwner(), (gameMap.getPlanet(treeMap.get(key).getId()).getDockedShips().get(0)))), Constants.MAX_SPEED, true, 90, Math.PI/180);
                    				moveList.add(newThrustMove);
                            		break;
                    			}
                				else
                				{
                					moveList.add(new DockMove(ship, gameMap.getPlanet(treeMap.get(key).getId())));
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

