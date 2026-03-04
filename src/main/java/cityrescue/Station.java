package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

/**
 * Station class 
 * 
 * Methods:
 * Station()
 * addUnit()
 * removeUnit()
 * setStationCapacity()
 * stationFull()
 * getStationId()
 * getStationName()
 * getX()
 * getY()
 * getMaxUnits()
 * getUnitCount()
 */

public class Station {
    private int stationId;
    private String stationName;
    private int x;
    private int y;
    private int maxUnits;
    private Unit[] unitsList;
    private int unitCount;

    //Constructor
    public Station(int stationId, String stationName, int x, int y) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.x = x;
        this.y = y;
        this.maxUnits = 50;
        this.unitsList = new Unit[maxUnits];
        this.unitCount = 0;
    }
    
    //Adds unit to stations unit list
    public void addUnit(Unit newUnit) throws CapacityExceededException {
        if (unitCount >= maxUnits) throw new CapacityExceededException("Station is full");
        unitsList[unitCount++] = newUnit;
    }

    //Removes unit from station unit list
    public void removeUnit(Unit oldUnit) {
        int index = -1;
        for (int i = 0; i < unitCount; i++) {
            if (unitsList[i] == oldUnit) {
                index = i;
                break;
            }
        }

        for (int i = index; i < unitCount - 1; i++) {
            unitsList[i] = unitsList[i + 1];
        }
        unitsList[--unitCount] = null;
    }

    //Update/set stations max unit capacity
    public void setStationCapacity(int newMaxUnits) throws InvalidCapacityException {
        this.maxUnits = newMaxUnits;
    }

    //Space left in station?
    public boolean stationFull() {
        return (maxUnits - unitCount) == 0;
    }

    //Info returning methods
    public int getStationId() {return stationId;}
    public String getStationName() {return stationName;}
    public int getX() {return x;}
    public int getY() {return y;}
    public int getMaxUnits() {return maxUnits;}
    public int getUnitCount() {return unitCount;}


}

