package cityrescue.UnitTypes;

import cityrescue.enums.*;
import cityrescue.exceptions.*;
import cityrescue.Unit;

public class Ambulance extends Unit {
    //Constructor
    public Ambulance(int unitId, int homeStationId, int unitX, int unitY, int homeStationX, int homeStationY) {
        super(unitId, UnitType.AMBULANCE, homeStationId, unitX, unitY, homeStationX, homeStationY);
    }

    //Can handle override
    @Override
    public boolean canHandle(IncidentType type) {
        return type == IncidentType.MEDICAL;
    }

    //Ticks taken to resolve incident override
    @Override
    public int getTicksToResolve(int severity) {return 2;}
}
