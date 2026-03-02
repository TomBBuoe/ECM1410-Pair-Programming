package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

/**
 * CityRescueImpl (Starter)
 *
 * Your task is to implement the full specification.
 * You may add additional classes in any package(s) you like.
 */
public class CityRescueImpl implements CityRescue {

    // TODO: add fields (map, arrays for stations/units/incidents, counters, tick, etc.)

    final int MAX_STATIONS = 20;
    final int MAX_UNITS = 50;
    final int MAX_INCIDENTS = 200;

    private Station[] stations = new Station[MAX_STATIONS];
    private int nextFreeStationIndex = 0;
    private Unit[] units = new Unit[MAX_UNITS];
    private int nextFreeUnitIndex = 0;
    private Incident[] incidents = new Incident[MAX_INCIDENTS];
    private int nextFreeIncidentIndex = 0;

    private CityMap cityMap;

    @Override
    public void initialise(int width, int height) throws InvalidGridException {
        if (width > 0 && height > 0) {
            this.cityMap = new CityMap(width, height);
        }
        else {
            throw new InvalidGridException("Input grid dimensions invalid");
        }
    }

    @Override
    public int[] getGridSize() {
        return new int[]{cityMap.getWidth(), cityMap.getHeight()};
    }

    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException {
        if (cityMap.isValidLocation(x, y)) {
            cityMap.blockCell(x, y);
        }
        else {
            throw new InvalidLocationException("Invalid location input");
        }
    }

    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException {
        if (cityMap.isValidLocation(x, y)) {
            cityMap.unblockCell(x, y);
        }
        else {
            throw new InvalidLocationException("Invalid location input");
        }
    }

    // Tom
    @Override
    public int addStation(String name, int x, int y) throws InvalidNameException, InvalidLocationException {
        if (name == null || name.trim() == null) {
            throw new InvalidNameException("Station name cannot be null");
        }
        else if (!cityMap.isLegalMove(x, y)) {
            throw new InvalidLocationException("Location of station invalid");
        }
        else if (nextFreeStationIndex >= MAX_STATIONS) {
            throw new CapacityExceededException("Station capacity exceeded");
        }
        else {
            stations[nextFreeStationIndex] = new Station(nextFreeStationIndex + 1, name, x, y);
            return ++nextFreeStationIndex;
        }
    }

    // Tom
    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException {
        if (stationId < 1 || stationId > MAX_STATIONS || stations[stationId - 1] == null) {
            throw new IDNotRecognisedException("Station ID not recognised");
        }
        else if (stations[stationId - 1].getUnitCount() != 0) {
            throw new IllegalStateException("Station cannot be removed as it still owns units");
        }
        else {
            stations[stationId - 1] = null;
        }
    }

    // Tom
    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException {
        if (stationId < 1 || stationId > MAX_STATIONS || stations[stationId - 1] == null) {
            throw new IDNotRecognisedException("Station ID not recognised");
        }

        int unitCount = stations[stationId - 1].getUnitCount();
        if (maxUnits < unitCount || maxUnits <= 0) {
            throw new InvalidCapacityException("New unit max is less then current unit number");
        }
        else {
            stations[stationId - 1].setStationCapacity(maxUnits);
        }
    }

    // Tom
    @Override
    public int[] getStationIds() {
        int totalStations = 0;
        for (Station station : stations) {
            if (station != null) {
                ++totalStations;
            } 
        }

        int[] foundStationIds = new int[totalStations];
        int counter = 0; 
        for (Station station : stations) {
            if (station != null) {
                foundStationIds[counter++] = station.getStationId();
            }
        }
        return foundStationIds;
    }

    // Archie
    @Override
    public int addUnit(int stationId, UnitType type) throws IDNotRecognisedException, InvalidUnitException, IllegalStateException {
        // TODO: implement
    }

    // Archie
    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Archie
    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Archie
    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException {
        if (unitId < 1 || unitId > MAX_UNITS || units[unitId - 1] == null) {
            throw new IDNotRecognisedException("Unit ID not recognised");
        }

        Unit unit = units[unitId - 1];
        if (outOfService){
            if (unit.getStatus() == UnitStatus.IDLE) {
                unit.outOfService(outOfService);
            }
            else {
                throw new IllegalStateException("Illegal unit state: must be IDLE");
            }
        }
        else {
            if (unit.getStatus() == UnitStatus.OUT_OF_SERVICE) {
                unit.outOfService(outOfService);
            }
            else {
                throw new IllegalStateException("Illegal unit state: must be OUT_OF_SERVICE");
            }
        }
    }

    // Archie
    @Override
    public int[] getUnitIds() {
        int totalUnits = 0;
        for (Unit unit : units) {
            if (unit != null) {
                ++totalUnits;
            } 
        }

        int[] foundUnitIds = new int[totalUnits];
        int counter = 0; 
        for (Unit unit : units) {
            if (unit != null) {
                foundUnitIds[counter++] = unit.getUnitId();
            }
        }
        return foundUnitIds;
    }

    // Archie
    @Override
    public String viewUnit(int unitId) throws IDNotRecognisedException {
        if (unitId < 1 || unitId > MAX_UNITS || units[unitId - 1] == null) {
            throw new IDNotRecognisedException("Unit ID not recognised");
        }
        Unit unit = units[unitId - 1]; 
        String type = unit.getUnitType().name();
        String home = Integer.toString(unit.getHomeStationId());
        String xLoc = Integer.toString(unit.getUnitX());
        String yLoc = Integer.toString(unit.getUnitY());
        String status = unit.getStatus().name();
        Incident incident = unit.getAssignedIncident();
        String incidentId = "-";
        if (incident != null) {
            incidentId = Integer.toString(incident.getIncidentId());
        }
        String workTicks = Integer.toString(unit.getTicksAtScene());

        return new String ("U#" + unitId + " TYPE=" + type + " HOME=" + home + " LOC=(" + xLoc + "," + yLoc + ") STATUS=" + status + " INCIDENT=" + incidentId + " WORK=" + workTicks);
    }

    @Override
    public int reportIncident(IncidentType type, int severity, int x, int y) throws InvalidSeverityException, InvalidLocationException {

        if (type == null) {
            throw new IllegalArgumentException("Invalid type input");
        }
        else if (1 > severity || severity > 5) {
            throw new InvalidSeverityException("Invalid severity input");
        }
        else if (!cityMap.isLegalMove(x, y)) {
            throw new InvalidLocationException("Invalid location input");
        }
        else if (nextFreeIncidentIndex >= MAX_INCIDENTS) {
            throw new CapacityExceededException("Incident capacity exceeded");
        }
        else {
        incidents[nextFreeIncidentIndex] = new Incident(nextFreeIncidentIndex + 1, type, severity, x, y);
        return ++nextFreeIncidentIndex;
        }
    }

    
    @Override
    public void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException {
        if (incidentId < 1 || incidentId > MAX_INCIDENTS || incidents[incidentId - 1] == null) {
            throw new IDNotRecognisedException("Incident ID not recognised");
        }

        Incident incident = incidents[incidentId - 1];
        IncidentStatus status = incident.getIncidentStatus();

        if (status == IncidentStatus.DISPATCHED) {
            incident.getAssignedUnit().release();
            incident.setAssignedUnit(null);
            incident.setIncidentStatus(IncidentStatus.CANCELLED);
        }
        else if (status == IncidentStatus.REPORTED) {
            incident.setIncidentStatus(IncidentStatus.CANCELLED);
        }
        else {
            throw new IllegalStateException("Incident state is illegal");
        }
    }

    
    @Override
    public void escalateIncident(int incidentId, int newSeverity) throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException {
        if (incidentId < 1 || incidentId > MAX_INCIDENTS || incidents[incidentId - 1] == null) {
            throw new IDNotRecognisedException("Incident ID not recognised");
        }

        Incident incident = incidents[incidentId - 1];
        IncidentStatus status = incident.getIncidentStatus();

        if (newSeverity < 1 || 5 < newSeverity) {
            throw new InvalidSeverityException("Invalid new serverity input");
        }
        else if (status == IncidentStatus.RESOLVED || status == IncidentStatus.CANCELLED) {
            throw new IllegalStateException("Invalid incident state");
        }
        else {
            incident.setSeverity(newSeverity);
        }
    }

    
    @Override
    public int[] getIncidentIds() {
        int totalIncidents = 0;
        for (Incident incident : incidents) {
            if (incident != null) {
                ++totalIncidents;
            } 
        }

        int[] foundIncidentIds = new int[totalIncidents];
        int counter = 0; 
        for (Incident incident : incidents) {
            if (incident != null) {
                foundIncidentIds[counter++] = incident.getIncidentId();
            }
        }
        return foundIncidentIds;
    }

    
    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException {
        if (incidentId < 1 || incidentId > MAX_INCIDENTS || incidents[incidentId - 1] == null) {
            throw new IDNotRecognisedException("Incident ID not recognised");
        }
        Incident incident = incidents[incidentId - 1]; 
        String type = incident.getIncidentType().name();
        String severity = Integer.toString(incident.getSeverity());
        String xLoc = Integer.toString(incident.getX());
        String yLoc = Integer.toString(incident.getY());
        String status = incident.getIncidentStatus().name();
        Unit unit = incident.getAssignedUnit();
        String unitId = "-";
        if (unit != null) {
            unitId = Integer.toString(unit.getUnitId());
        }

        return new String ("I#" + incidentId + " TYPE=" + type + " SEV=" + severity + " LOC=(" + xLoc + "," + yLoc + ") STATUS=" + status + " UNIT=" + unitId);

    }

    // Both
    @Override
    public void dispatch() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Both
    @Override
    public void tick() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Both
    @Override
    public String getStatus() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
