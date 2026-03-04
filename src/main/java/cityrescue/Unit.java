package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

public abstract class Unit {
    private int unitId;
    private UnitType unitType;
    private int ticksAtScene;
    private int unitX;
    private int unitY;
    private int homeStationId;
    private int homeStationX;
    private int homeStationY;
    private UnitStatus status;
    private Incident assignedIncident;
    private int workRemanining;

    //Constructor
    public Unit(int unitId, UnitType unitType, int homeStationId, int unitX, int unitY, int homeStationX, int homeStationY) {
        this.unitId = unitId;
        this.unitType = unitType;
        this.homeStationId = homeStationId;
        this.unitX = unitX;
        this.unitY = unitY;
        this.homeStationX = homeStationX;
        this.homeStationY = homeStationY;

        this.status = UnitStatus.IDLE;
        this.assignedIncident = null;
        this.workRemanining = 0;
    }

    //Abstract methods to be overidden
    //Can this unit handle this incident
    public abstract boolean canHandle(IncidentType incidentType);
    //Ticks unit type takes to resolve incident
    public abstract int getTicksToResolve(int severity);

    //Assign incident to unit
    public void assignIncident(Incident incident) {
        this.assignedIncident = incident;
        this.status = UnitStatus.EN_ROUTE;
    }

    //Release unit from incident
    public void release() {
        this.assignedIncident = null;
        this.status = UnitStatus.IDLE;
        this.workRemanining = 0;
        this.ticksAtScene = 0;
    }

    //Distance to goal from current point
    public int manhattan(int endX, int endY) {
        return Math.abs(this.unitX - endX) + Math.abs(this.unitY - endY);
    }

    //Move the unit
    public void move(CityMap map) {
        if (status != UnitStatus.EN_ROUTE || assignedIncident == null) {
            return;
        }

        int endX = assignedIncident.getX();
        int endY = assignedIncident.getY();

        int[][] directions = {{0, -1},{1, 0},{0, 1},{-1, 0}}; //N? E S? W

        int distanceTo = manhattan(endX, endY);

        //Trying to take first legal move that reduces distance
        for (int[] d : directions) {
            int newX = this.unitX + d[0];
            int newY = this.unitY + d[1];
            if (map.isLegalMove(newX, newY)) {
                int newDistanceTo = manhattan(newX, newY);
                if (newDistanceTo < distanceTo) {
                    this.unitX = newX;
                    this.unitY = newY;
                    return;
                }

            }
        }
        //Trying to take first legal move
        for (int[] d : directions) {
            int newX = this.unitX + d[0];
            int newY = this.unitY + d[1];
            if (map.isLegalMove(newX, newY)) {
                this.unitX = newX;
                this.unitY = newY;
                return;
            }
        }

    }

    //Track arrival to goal
    public boolean hasArrived() {
        if (assignedIncident == null) return false;
        return ((this.unitX == assignedIncident.getX()) && (this.unitY == assignedIncident.getY()));
    }

    //Start the unit working on the incident
    public void startWork() {
        if (assignedIncident == null) return;
        this.status = UnitStatus.AT_SCENE;
        this.workRemanining = getTicksToResolve(assignedIncident.getSeverity());
    }

    //Removes a tick every call untill 0 ticks remain and it returns true
    public boolean workATick() {
        if (status != UnitStatus.AT_SCENE) return false;
        workRemanining--;
        if (workRemanining == 0) {
            ticksAtScene = 0;
            return true;
        }
        else {
            ticksAtScene++;
            return false;
        }
    }

    //Put a unit out of service
    public void outOfService(boolean outOf) {
        if (outOf) {
            this.status = UnitStatus.OUT_OF_SERVICE;
        } else {
            this.status = UnitStatus.IDLE;
        }
    }

    //Update/set new positon
    public void setPosition(int newX, int newY) {
        this.unitX = newX;
        this.unitY = newY;
    }

    //set new home station
    public void newStation(int stationId, int stationX, int stationY) {
        this.homeStationId = stationId;
        this.homeStationX =  stationX;
        this.homeStationY =  stationY;
    }

    //Getting methods
    public int getUnitId() {return unitId;}
    public UnitType getUnitType() {return unitType;}
    public int getTicksAtScene() {return ticksAtScene;}
    public int getUnitX() {return unitX;}
    public int getUnitY() {return unitY;}
    public int getHomeStationId() {return homeStationId;}
    public int getHomeStationX() {return homeStationX;}
    public int getHomeStationY() {return homeStationY;}
    public UnitStatus getStatus() {return status;}
    public Incident getAssignedIncident() {return assignedIncident;}
    public int getWorkRemaining() {return workRemanining;}


}



