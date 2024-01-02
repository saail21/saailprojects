//Saail Imam  9/27/23
//This code is a simulation of a cat and mouse game which uses the random class to move mice randomly around an 8x8 array "board", they move randomly as a cat chases
// the mice through the usage of the findNearestMouse method and the distance formula using both x and y coordinates of the nearest mouse. The goal is for the cat to capture all mice and 
// at the end the program then displays the ammount of iterations it took for the game to complete.
//Program name: CATANDMOUSESIMULATION

package catandmousesimulation;

/**
 *
 * @author saail
 */

import java.util.*;

//classes:

//cat,mouse,board, class responsible for movement,simulation class responsible for running of the game

class Board {
    
    private char[][] board;

    public Board() {
        board = new char[8][8]; //8x8 board to simulate game board
        initializeBoard();
    }

    // Initialize the game board with empty cells
    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = '.';
            }
        }
    }

    // Update the board with cat and mouse positions
    public void updateBoard(Cat cat, Mouse[] mice) {
        initializeBoard();
        board[cat.x][cat.y] = 'C'; // Place the cat on the board
        for (Mouse mouse : mice) {
            if (!mouse.isCaught()) {
                board[mouse.x][mouse.y] = 'M'; // Place the mice on the board
            }
        }
    }

    // Check if a specific cell on the board is occupied by a cat or mouse
    public boolean isOccupied(int x, int y) {
        return board[x][y] != '.';
    }

    // Display the current state of the board
    public void displayBoard() {
        System.out.println("----------------");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j == 0) {
                    System.out.print("| ");
                }

                char symbol = board[i][j];
                if (symbol == '.') {
                    System.out.print("  "); // Empty cell
                } else {
                    System.out.print(symbol + " "); // Cat (C) or mouse (M)
                }

                if (j == 7) {
                    System.out.print("|"); //printing boarder of the game board
                }
            }
            System.out.println();
        }
        System.out.println("----------------");
    }
}

class Movement {
    protected int x, y;

    public Movement(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Check if the creature can move to a new position
    public boolean canMoveTo(int newX, int newY) {
        return newX >= 0 && newX < 8 && newY >= 0 && newY < 8;
    }

    // Move the creature to a random neighboring cell
    public void moveRandomly(Board board) {
        Random rand = new Random();
        int newX, newY;

        do {
            int direction = rand.nextInt(4); // 4 directions of possible movement; 0: up, 1: down, 2: left, 3: right
            switch (direction) {
                case 0:
                    newX = x - 1;
                    newY = y;
                    break;
                case 1:
                    newX = x + 1;
                    newY = y;
                    break;
                case 2:
                    newX = x;
                    newY = y - 1;
                    break;
                default:
                    newX = x;
                    newY = y + 1;
                    break;
            }
        } while (!canMoveTo(newX, newY) || board.isOccupied(newX, newY));

        x = newX;
        y = newY;
    }
}
class Cat extends Movement {
    public Cat(int x, int y) {
        super(x, y);
    }

    // Move the cat towards the nearest mouse
    public void moveTowardsMouse(Mouse[] mice) {
        if (mice.length == 0) {
            return; // no mice to chase
        }

        // find the nearest mouse among the available mice
        Mouse nearestMouse = findNearestMouse(mice);

        int disX = nearestMouse.x - x; // calculate the difference in x-coordinates
        int disY = nearestMouse.y - y; // calculate the difference in y-coordinates

        // calculates the distance between the cat and the nearest mouse using the distance formula:
        double magnitude = Math.sqrt(disX * disX + disY * disY);

        // calculates the x and y for moving towards the mouse
        int moveX = (int) Math.round(disX / magnitude);
        int moveY = (int) Math.round(disY / magnitude);

        if (canMoveTo(x + moveX, y + moveY)) {
            x += moveX;
            y += moveY;
        }
    }

    private Mouse findNearestMouse(Mouse[] mice) {  // finds the nearest mouse among the available mice
        Mouse nearestMouse = mice[0];
        double minDistance = distanceTo(nearestMouse);

        // itirates through the mice to find the lowest distance value from the cat
        for (int i = 1; i < mice.length; i++) {
            double currentDistance = distanceTo(mice[i]);
            if (currentDistance < minDistance) {
                nearestMouse = mice[i];
                minDistance = currentDistance;
            }
        }

        return nearestMouse;
    }

    // calculates the distance between the cat and the nearest mouse using the distance formula
    private double distanceTo(Mouse mouse) {
        int deltaX = mouse.x - x;
        int deltaY = mouse.y - y;

        // distance formula being distance = square root((x)^2 + (y)^2) so...
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
}

class Mouse extends Movement {
    
    private boolean caught;

    public Mouse(int x, int y) {
        super(x, y);
        this.caught = false;
    }

    public boolean isCaught() {
        return caught;
    }

    public void setCaught(boolean caught) {
        this.caught = caught;
    }
}

public class CatAndMouseSimulation {
    
    public static void main(String[] args) {
        Board board = new Board();
        Cat cat = new Cat(0, 0);
        Mouse[] mice = new Mouse[5];

        initializeBoard(board, cat, mice);

        int turns = 0;

        while (!allMiceCaught(mice)) {
            board.updateBoard(cat, mice);

            for (Mouse mouse : mice) {
                if (!mouse.isCaught()) {
                    mouse.moveRandomly(board); //if a mouse is not caught then move randomly on the board
                }
            }

            cat.moveTowardsMouse(mice);

            for (Mouse mouse : mice) {
                if (!mouse.isCaught() && cat.x == mouse.x && cat.y == mouse.y) {
                    mouse.setCaught(true); //if the x and y values of cat = mouse then it is caught
                }
            }

            // remove caught mice from the array
            mice = removeCaughtMice(mice);

            board.displayBoard();
            turns++;
        }

        System.out.println("Number of moves = " + turns);
    }

    private static boolean allMiceCaught(Mouse[] mice) {
        for (Mouse mouse : mice) {
            if (!mouse.isCaught()) {
                return false; 
            }
        }
        return true;
    }

    private static void initializeBoard(Board board, Cat cat, Mouse[] mice) {
        
        Random rand = new Random();

        // initialize the cat position
        int catX, catY;
        do {
            catX = rand.nextInt(8);
            catY = rand.nextInt(8); //random positins on the x and y 
        } while (board.isOccupied(catX, catY));
        cat.x = catX;
        cat.y = catY;

        // initialize the mice positions
        for (int i = 0; i < mice.length; i++) {
            int mouseX, mouseY;
            do {
                mouseX = rand.nextInt(8);
                mouseY = rand.nextInt(8);
            } while (board.isOccupied(mouseX, mouseY));
            mice[i] = new Mouse(mouseX, mouseY);
        }
    }

    private static Mouse[] removeCaughtMice(Mouse[] mice) { 
        // create a new array without the caught mice
        ArrayList<Mouse> remainingMice = new ArrayList<>();
        for (Mouse mouse : mice) {
            if (!mouse.isCaught()) {
                remainingMice.add(mouse); //adds all non caught mice into an arraylist
            }
        }
        return remainingMice.toArray(new Mouse[0]); 
    }
}