package cityrescue;
/**
 * Handles an incident's various states and modifications
 */

import cityrescue.enums.IncidentStatus;
import cityrescue.enums.IncidentType;

public class Incident {
    private int id;
    private int severity;
    private int x;
    private int y;
    private IncidentType type;
    private IncidentStatus status;
    private Unit assignedUnit;

    public Incident(int incidentId, IncidentType incidentType, int incidentSeverity, int xCoord, int yCoord) {
        this.id = incidentId;
        this.type = incidentType;
        this.severity = incidentSeverity;
        this.x = xCoord;
        this.y = yCoord;
        this.status = IncidentStatus.REPORTED; 
    }

    public int getIncidentId() {
        return id;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int incidentSeverity) {
        this.severity = incidentSeverity;
    }

    public int getX() {
        return x;
    }

    public int getY() {
    return y;
    }

    public IncidentType getIncidentType() {
        return type;
    }

    public IncidentStatus getIncidentStatus() {
        return status;
    }

    public void setIncidentStatus(IncidentStatus incidentStatus) {
        status = incidentStatus;
    }

    public Unit getAssignedUnit() {
        return assignedUnit;
    }
    
    public void setAssignedUnit(Unit unit) {
        this.assignedUnit = unit;
    }

}
