package hlt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Util {

    public static int angleRadToDegClipped(final double angleRad) {
        final int degUnclipped = (int)(Math.toDegrees(angleRad) + 0.5);
        // Make sure return value is in [0, 360) as required by game engine.
        return ((degUnclipped % 360) + 360) % 360;
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
/*	public static Position[] futurePosition(double startX,double startY,double speed)
	{
		
	}	*/
}
