package battleship;

import java.util.Arrays;

public class Board {
    CellType[][] grid;
    public static final int HEIGHT = 10;
    public static final int WIDTH = 10;
    public static final String ROW_LABELS = "ABCDEFGHIJ";

    Board() {
        this.grid = new CellType[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            Arrays.fill(this.grid[i], CellType.FOG_OF_WAR);
        }
    }

    public void print() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < HEIGHT; i++) {
            StringBuilder row = new StringBuilder();
            row.append(ROW_LABELS.charAt(i));
            for (int j = 0; j < WIDTH; j++) {
                row.append(" ");
                row.append(grid[i][j].getSymbol());
            }
            System.out.println(row);
        }
    }

    public void placeShip(Ship ship) throws ShipSurroundingsNotEmptyException {
        Coordinates shipStart = ship.getCoordinatesPair().getStart();
        Coordinates shipEnd = ship.getCoordinatesPair().getEnd();
        Coordinates[] shipCells = ship.getCoordinatesPair().getSteps();

        // Check if the area is empty
        int minX = Math.min(shipStart.getRow(), shipEnd.getRow()) - 1;
        int maxX = Math.max(shipStart.getRow(), shipEnd.getRow()) + 1;
        int minY = Math.min(shipStart.getColumn(), shipEnd.getColumn()) - 1;
        int maxY = Math.max(shipStart.getColumn(), shipEnd.getColumn()) + 1;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // skip coordinates that are out of bounds
                try {
                    new Coordinates(x, y);
                } catch (InvalidCoordinatesException e) {
                    continue;
                }
                if (grid[x][y] != CellType.FOG_OF_WAR) {
                    throw new ShipSurroundingsNotEmptyException("Cell [" + x + "][" + y + "] not empty");
                }
            }
        }

        // Place the ship on the board
        for (Coordinates coordinates : shipCells) {
            this.grid[coordinates.getRow()][coordinates.getColumn()] = CellType.YOUR_SHIP;
        }
    }

    private enum CellType {
        FOG_OF_WAR('~'),
        YOUR_SHIP('O'),
        HIT_SHIP('X'),
        MISS('M');

        private final char symbol;

        CellType(char symbol) {
            this.symbol = symbol;
        }

        char getSymbol() {
            return this.symbol;
        }
    }
}

class ShipSurroundingsNotEmptyException extends Exception {
    public ShipSurroundingsNotEmptyException(String message) {
        super(message);
    }
}