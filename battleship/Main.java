package battleship;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Board board = new Board();

        board.print();
        for (ShipType shipType : ShipType.values()) {
            System.out.println();
            System.out.printf(
                    "Enter the coordinates of the %s (%d cells):%n",
                    shipType.getName(),
                    shipType.getLength()
            );
            Ship ship = null;
            do {
                String input = scanner.nextLine();
                try {
                    ship = new Ship(input, shipType);
                    try {
                        board.placeShip(ship);
                    } catch (ShipSurroundingsNotEmptyException e) {
                        ship = null;
                        System.out.println("Error! You placed it too close to another one. Try again:");
                    }
                } catch (InvalidShipCoordinatesException e) {
                    System.out.println("Error! Wrong ship location! Try again:");
                } catch (InvalidShipLengthException e) {
                    System.out.println("Error! Wrong length of the Submarine! Try again:");
                }
            } while (ship == null);

            board.print();
        }
    }
}

class UnreachableCodeRuntimeException extends RuntimeException {
    public UnreachableCodeRuntimeException(String message) {
        super(message);
    }
}