package hlt;

public class GhostShip extends Ship {

	public GhostShip(int owner, int id, double xPos, double yPos, int health, double radius) {
		super(owner,id, xPos, yPos, health, Ship.DockingStatus.Undocked, health, health, health);
		
	}
}