package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;
import cityrescue.UnitTypes.*;


public class CityRescueImpl implements CityRescue {

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

    private int tick = 0;

    /**
     * Creates simulation grid
     * 
     * @param width grid width
     * @param width height
     */
    @Override
    public void initialise(int width, int height) throws InvalidGridException {
        if (width > 0 && height > 0) {
            this.cityMap = new CityMap(width, height);
            stations = new Station[MAX_STATIONS];
            nextFreeStationIndex = 0;
            units = new Unit[MAX_UNITS];
            nextFreeUnitIndex = 0;
            incidents = new Incident[MAX_INCIDENTS];
            nextFreeIncidentIndex = 0;
            tick = 0;
        }
        else {
            throw new InvalidGridException("Input grid dimensions invalid");
        }
    }

    /** 
     * Returns grid dimensions
     * 
     * @return grid dimensions
     */
    @Override
    public int[] getGridSize() {
        return new int[]{cityMap.getWidth(), cityMap.getHeight()};
    }

    /**
     * Creates obstacle in specified location
     * 
     * @param x Obstacle x coordinate
     * @param y Obstacle y coordinate
     */
    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException {
        if (cityMap.isValidLocation(x, y)) {
            cityMap.blockCell(x, y);
        }
        else {
            throw new InvalidLocationException("Invalid location input");
        }
    }

    /**
     * Removes obstacle from grid
     * 
     * @param x Obstacle x coordinate
     * @param y Obstacle y coordinate
     */
    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException {
        if (cityMap.isValidLocation(x, y)) {
            cityMap.unblockCell(x, y);
        }
        else {
            throw new InvalidLocationException("Invalid location input");
        }
    }

    /**
     * Creates station of specified name and location
     * 
     * @param name Station name
     * @param x Station x coordinate
     * @param y Station y coordinate
     * @return nextFreeStationIndex Incremented record of next free station index
     */
    @Override
    public int addStation(String name, int x, int y) throws InvalidNameException, InvalidLocationException {
        if (name == null || name.trim().isEmpty()) {
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

    /**
     * Removes station
     * Station must have no units to be eligible
     * 
     * @param stationId station ID
     */
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

    /**
     * Updates stations unit capacity
     * 
     * @param stationId Station ID
     * @param maxUnits Stations new unit capacity
     */
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

    /**
     * Returns list of station IDs (ascending order)
     * 
     * @return foundStationIds List of station IDs
     */
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

    /**
    * Creates a new unit and assigns it to a station
    * 
    * @param stationId Station ID
    * @param type Unit type
    */
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

    /**
     * Removes unit
     * 
     * @param unitId unit ID
     */
    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException {
        if (unitId < 1 || unitId > MAX_UNITS || units[unitId - 1] == null) throw new IDNotRecognisedException("Unit ID not recognised");
        Unit unit = units[unitId - 1];
        if (unit.getStatus() == UnitStatus.EN_ROUTE || unit.getStatus() == UnitStatus.AT_SCENE) throw new IllegalStateException("Unit is in illegal state for action");
        Station station = stations[unit.getHomeStationId() - 1];
        station.removeUnit(unit);
        units[unitId - 1] = null;
    }

    /**
     * Updates units home station, sets unit location to home station
     * 
     * @param unitId Unit ID
     * @param newStationId Updated home station ID
     */
    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException {
        if (unitId < 1 || unitId > MAX_UNITS || units[unitId - 1] == null) throw new IDNotRecognisedException("Unit ID not recognised");
        if (newStationId < 1 || newStationId > MAX_STATIONS || stations[newStationId - 1] == null) throw new IDNotRecognisedException("Station ID not recognised");
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

    /**
     * Sets unit status to OUT_OF_SERVICE or IDLE
     * 
     * @param unitId unit ID
     * @param outOfService boolean value, dictates which status unit is set to
     */
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

    /**
     * Returns list of unit IDs in ascending order
     * 
     * @return foundUnitIds List of unit ids
     */
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

    /**
     * Produces unit information
     * 
     * @param unitId units ID
     * @return String holding unit information
     */
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
        if (unit.getTicksAtScene() == 0) {
            return new String ("U#" + unitId + " TYPE=" + type + " HOME=" + home + " LOC=(" + xLoc + "," + yLoc + ") STATUS=" + status + " INCIDENT=" + incidentId);
        }
        else {
            return new String ("U#" + unitId + " TYPE=" + type + " HOME=" + home + " LOC=(" + xLoc + "," + yLoc + ") STATUS=" + status + " INCIDENT=" + incidentId + " WORK=" + workTicks);
        }
    }

    /**
     * Creates new incident of specified type, severity and location
     * 
     * @param type Incident type
     * @param severity Incident severity
     * @param x Incident x coordinate
     * @param y Incident y coordinate
     * @return nextFreeIncidentIndex incremented record of next free incident index
     */
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

    /**
     * Cancels an incident
     * 
     * @param incidentId incident ID
     */
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

    /**
     * Escalates incidents severity
     * 
     * @param incidentId incident ID
     * @param newSeverity new severity level
     */
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

    /**
     * Returns list of incident IDs in ascending order
     * 
     * @return foundIncidentIds List of incident ids
     */
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

    /**
     * Produces incident information
     * 
     * @param incidentId incident ID
     * @return String holding incident information
     */
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

    /**
     * Assigns best eligible unit to reported incidents
     * 
     * Uses the following tie-breakers in this order to choose optimal unit:
     * Shortest Manhattan distance
     * If last tied: lowest unitId
     * If last tied: lowest homeStationId 
     */
    @Override
    public void dispatch() {
        int[] incidentIds = getIncidentIds();

        for (int incidentId : incidentIds) {
            Incident incident = incidents[incidentId - 1];
            if (incident.getIncidentStatus() == IncidentStatus.REPORTED) {
                int smallestDistance = 999999; // Use arbitrary large number to compare to
                Unit bestUnit = null;
                for (Unit unit : units) {
                    if (unit == null) continue;
                    if (unit.getStatus() == UnitStatus.IDLE && unit.canHandle(incident.getIncidentType())) {
                        int distanceToIncident = Math.abs(incident.getX() - unit.getUnitX()) + Math.abs(incident.getY() - unit.getUnitY());
                        if (distanceToIncident < smallestDistance) {
                            smallestDistance = distanceToIncident;
                            bestUnit = unit;
                        }
                    }
                }
                if (bestUnit != null) { // Can use best unit as the lowest ID unit will be the first to be found, and so automatically the tie break will be implemented
                    incident.setIncidentStatus(IncidentStatus.DISPATCHED);
                    incident.setAssignedUnit(bestUnit);
                    bestUnit.assignIncident(incident);
                }
            }
        }
    }

    /**
     * Advances the simulation forward a tick
     * 
     * Manages the following in this order:
     * Moves units (In ascending unitId)
     * Marks arrivals
     * processes on-scene work
     * resolves completed incidents (In ascending incidentId)
     */
    @Override
    public void tick() {
        //tick++
        tick++;
        //move enroute units (start lowest unitid and ascend) + mark arrivals
        for (int i = 0; i < MAX_UNITS; i++) {
            Unit unit = units[i];
            if (unit == null) continue; //In case of null value
            if (unit.getStatus() == UnitStatus.EN_ROUTE) {
                unit.move(cityMap);
                if (unit.hasArrived()) {
                    Incident incident = unit.getAssignedIncident();
                    incident.setIncidentStatus(IncidentStatus.IN_PROGRESS);
                    unit.startWork();
                }
            }
        }



        //process on scene work (aslong as they havent just got there or else they will progress 2 tick at once) + resolve completed incidents
        for (int i = 0; i < MAX_INCIDENTS; i++) {
            Incident incident = incidents[i];
            if (incident == null) continue;
            if (incident.getIncidentStatus() == IncidentStatus.IN_PROGRESS) {
                Unit unit = incident.getAssignedUnit();
                boolean finishedWork = unit.workATick();
                if (finishedWork) {
                    incident.setIncidentStatus(IncidentStatus.RESOLVED);
                    unit.release();
                }
            }
        }   
    }

    /** 
     * Returns a formatted report on the current state of the program
     * 
     * The report includes:
     * Current tick 
     * tally of stations, units, incidents and obstacles
     * All current incidents details
     * All current units details
     * 
     * @return String variable holding current state of program
    */
    @Override
    public String getStatus() {
        int stationCount = 0;
        int unitCount = 0;
        int incidentCount = 0;
        int obstacleCount = 0;
        String report = "";
        for (int i = 0; i < MAX_STATIONS; i++) {
            Station station = stations[i];
            if (station != null) {stationCount += 1;}
        }
        for (int i = 0; i < MAX_UNITS; i++) {
            Unit unit = units[i];
            if (unit != null) {unitCount += 1;}
        }
        for (int i = 0; i < MAX_INCIDENTS; i++) {
            Incident incident = incidents[i];
            if (incident != null) {incidentCount += 1;}
        }
        for (int x = 0; x < cityMap.getWidth(); x++) {
            for (int y = 0; y < cityMap.getHeight(); y++) {
                if (cityMap.isBlocked(x, y)) {
                    obstacleCount += 1;
                }
            }
        }
        report = report + ("TICK=" + tick + "\n" + "STATIONS=" + stationCount + " UNITS=" + unitCount + " INCIDENTS=" + incidentCount + " OBSTACLES=" + obstacleCount + "\n" + "INCIDENTS");
        for (int i = 0; i < MAX_INCIDENTS; i++) {
            if (incidents[i] != null) {
                try {
                    report = report + ("\n" + viewIncident((incidents[i]).getIncidentId()));
                }
                catch (IDNotRecognisedException exception) {}
            }
        }
        report = report + ("\n" + "UNITS");
        for (int i = 0; i < MAX_UNITS; i++) {
            if (units[i] != null) {
                try {
                    report = report + ("\n" + viewUnit((units[i]).getUnitId()));
                }
                catch (IDNotRecognisedException exception) {}
            }
        }
        return report;
    }
}
