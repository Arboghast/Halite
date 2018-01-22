package hlt;

import java.util.ArrayList;

public class Group 
{
	private static ArrayList<Ship> ships;
	private static Position target;
	
	public Group()
	{
		Group.ships = new ArrayList<Ship>();
		Group.target = null;
	}
	
	public static void move(GameMap gameMap,Entity target, ArrayList<Move> moveList, ArrayList<Entity> targetedEntities, ArrayList<Ship> hasMove)
	{
		for(Ship ship: ships)
		{
			ThrustMove newThrustMove = new Navigation(ship, target).navigateToAttack(gameMap, target,Constants.MAX_SPEED);
			if (newThrustMove != null) {
				hasMove.add(ship);
				targetedEntities.add(target);
				moveList.add(newThrustMove);
			} 
		}
	}
	public static ArrayList<Ship> getships()
	{
		return ships;
	}
	public static void addShips(Ship x)
	{
		ships.add(x);
	}
	public static void removeShips(Ship x)
	{
		ships.remove(x);
	}
	public static Position getTarget()
	{
		return target;
	}
	public static void setTarget(Position x)
	{
		target = x;
	}
}