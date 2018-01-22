package hlt;

public class Value implements Comparable<Object>{

    private int planetsNearby;
    private int dockingSpotsNearby;
    private Planet enemyPlanet;

    public Value(final int planetsNearby,final int dockingSpots,final Planet enemy) {
        this.planetsNearby = planetsNearby;
        this.dockingSpotsNearby = dockingSpots;
        this.enemyPlanet  =enemy;
    }

	public int getPlanetsNearby() {
		return planetsNearby;
	}

	public int getDockingSpotsNearby() {
		return dockingSpotsNearby;
	}
	
	public Planet getEnemyPlanet() {
		return enemyPlanet;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}