package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;
import cityrescue.UnitTypes.*;

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
        if (stationId < 1 || stationId > MAX_STATIONS || stations[stationId - 1] == null) throw new IDNotRecognisedException("Station ID not recognised");
        Station stationObj = stations[stationId - 1];
        if (stationObj.stationFull()) throw new IllegalStateException("Station is full");
        if (type == null) throw new InvalidUnitException("Unit type invalid");
        if (nextFreeUnitIndex >= MAX_UNITS) throw new CapacityExceededException("Unit capacity exceeded");
        Unit unit;
        switch (type) {
            case AMBULANCE:
                unit = new Ambulance(nextFreeUnitIndex + 1,  stationId, stationObj.getX(), stationObj.getY(), stationObj.getX(), stationObj.getY());
                break;
            case FIRE_ENGINE:
                unit = new FireEngine(nextFreeUnitIndex + 1,  stationId, stationObj.getX(), stationObj.getY(), stationObj.getX(), stationObj.getY());
                break;
            case POLICE_CAR:
                unit = new PoliceCar(nextFreeUnitIndex + 1,  stationId, stationObj.getX(), stationObj.getY(), stationObj.getX(), stationObj.getY());
                break;
            default:
                throw new InvalidUnitException("Unit type invalid");
        }
        units[nextFreeUnitIndex] = unit;
        stationObj.addUnit(unit);
        return ++nextFreeUnitIndex;
    }

    // Archie
    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException {
        if (unitId < 1 || unitId > MAX_UNITS || units[unitId - 1] == null) throw new IDNotRecognisedException("Unit ID not recognised");
        Unit unit = units[unitId - 1];
        if (unit.getStatus() == UnitStatus.EN_ROUTE || unit.getStatus() == UnitStatus.AT_SCENE) throw new IllegalStateException("Unit is in illegal state for action");
        Station station = stations[unit.getHomeStationId() - 1];
        station.removeUnit(unit);
        units[unitId - 1] = null;
    }

    // Archie
    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException {
        if (units[unitId - 1] == null) throw new IDNotRecognisedException("Unit ID not recognised");
        if (stations[newStationId - 1] == null) throw new IDNotRecognisedException("Station ID not recognised");
        Unit unit = units[unitId - 1];
        Station station = stations[newStationId - 1];
        Station oldStation = stations[unit.getHomeStationId() - 1];
        if (station.stationFull()) throw new IllegalStateException("Station full");
        if (unit.getStatus() != UnitStatus.IDLE) throw new IllegalStateException("Unit is not idle");
        oldStation.removeUnit(unit);
        station.addUnit(unit);
        unit.newStation(newStationId, station.getX(), station.getY());
        unit.setPosition(station.getX(), station.getY());
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
        int[] incidentIds = getIncidentIds();
        int[] unitIds = getUnitIds();

        for (int incidentId : incidentIds) {
            Incident incident = incidents[incidentId - 1];
            if (incident.getIncidentStatus() == IncidentStatus.REPORTED) {
                for (int unitId : unitIds) {
                    Unit unit = units[unitId];
                    if (unit.getStatus() == UnitStatus.IDLE && unit.canHandle(incident.getIncidentType())) {

                    }
                }
            }
        }


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
