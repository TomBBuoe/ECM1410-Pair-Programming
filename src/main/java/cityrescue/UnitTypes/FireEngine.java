package cityrescue.UnitTypes;

import cityrescue.enums.*;
import cityrescue.exceptions.*;
import cityrescue.Unit;

public class FireEngine extends Unit {
    //Constructor
    public FireEngine(int unitId, int homeStationId, int unitX, int unitY, int homeStationX, int homeStationY) {
        super(unitId, UnitType.FIRE_ENGINE, homeStationId, unitX, unitY, homeStationX, homeStationY);
    }

    //Can handle override
    @Override
    public boolean canHandle(IncidentType type) {
        return type == IncidentType.FIRE;
    }

    //Ticks taken to resolve incident override
    @Override
    public int getTicksToResolve(int severity) {return 4;}
}