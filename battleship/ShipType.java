package battleship;

public enum ShipType {
    AIRCRAFT_CARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

    private final String name;
    private final int length;

    ShipType(String name, int size) {
        this.name = name;
        this.length = size;
    }

    public String getName() {
        return this.name;
    }

    public int getLength() {
        return this.length;
    }
}
