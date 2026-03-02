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

    @Override
    public int addStation(String name, int x, int y) throws InvalidNameException, InvalidLocationException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int[] getStationIds() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int addUnit(int stationId, UnitType type) throws IDNotRecognisedException, InvalidUnitException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int[] getUnitIds() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String viewUnit(int unitId) throws IDNotRecognisedException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
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
        else if (nextFreeIncidentIndex < MAX_INCIDENTS) {
            throw new CapacityExceededException("Incident capacity exceeded");
        }
        else {
        incidents[nextFreeIncidentIndex] = new Incident(nextFreeIncidentIndex + 1, type, severity, x, y);
        return ++nextFreeIncidentIndex;
        }
    }

    @Override
    public void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException {
        Incident incident = incidents[incidentId - 1];
        if (incident == null) {
            throw new IDNotRecognisedException("Incident ID not recognised");
        }
        else if (incident.getIncidentStatus() != IncidentStatus.REPORTED && incident.getIncidentStatus() != IncidentStatus.DISPATCHED) {
            throw new IllegalStateException("Incident state is illegal");
        }
        else {
            // TODO: 
        }
    }

    @Override
    public void escalateIncident(int incidentId, int newSeverity) throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int[] getIncidentIds() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void dispatch() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void tick() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String getStatus() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
