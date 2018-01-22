package hlt;

import java.util.ArrayList;

public class Team{

	private ArrayList<Ship> team;
	public Team() {
		this.team = new ArrayList<Ship>();
	}
	public ArrayList<Ship> getTeam() {
		return team;
	}
	public void setTeam(ArrayList<Ship> team) {
		this.team = team;
	}
	public void addShip(Ship ship)
	{
		this.team.add(ship);
	}
	public void removeShip(Ship ship) {
		this.team.remove(ship);
	}
	public Position avg()
	{
		double x = 0;
		double y = 0;
		for(Ship ship: team)
		{
			x = x +ship.getCurrentPosition().getXPos();
			y = y +ship.getCurrentPosition().getYPos();
		}
		x = x/team.size();
		y = y/team.size();
		
		Position position = new Position(x,y);
		return position;
	}
	
	
	
}