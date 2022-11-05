//Rio Voss-Kernan
//October 2022
//Algorithm for creating a perfect chomp player with integration into the game itself

import java.awt.*;
import java.util.*;
import java.io.*;

public class MyPlayer {
    final int scale = 10;
    boolean isWinning = false;

    public ArrayList<Board> boards = new ArrayList<>();
    private boolean isSerial = false;
    //private SerialReader serialReader = new SerialReader();
    //private ChompSolver chompSolver = new ChompSolver(scale,false);


    public MyPlayer() {
        //boards = findBestLosingMoves(findLossesAndWins(generateBoards(false), false), true);
        //boards = findLossesAndWins(generateBoards(false), false);

        //writeSerialized(boards);
        //writeBoardsToFile(boards);

        if(isSerial){
            //serialReader.start();
        }else{
            //chompSolver.start();
        }
    }

    //add your code to return the row and the column of the chip you want to take.
    //you'll be returning a data type called Point which consists of two integers.
    public Point move(Chip[][] pBoard, ArrayList<Board> boards) {

        Board board = chipArrayToIntArray(pBoard, false);
        Point move = getMove(board,boards);

        return move;
    }

//****************************************************************************************************************

    //Convert Chomp Board to Rio Board
    public  Board chipArrayToIntArray(Chip[][] pBoard, boolean isPrint){
        int[] board = new int[pBoard.length];

        //get array
        for (int c = 0; c < pBoard.length; c++) { // loop through columns
            for (Chip[] chips : pBoard) { // loop through rows
                if (chips[c].isAlive) {
                    board[c]++;
                }
            }
        }

        //shrinkArray
        int[] shrunkBoard = new int[scale];
        System.arraycopy(board, 0, shrunkBoard, 0, shrunkBoard.length);

        //Print
        if(isPrint) {
            System.out.println("Board: " + Arrays.toString(shrunkBoard));
        }

        Board boardF = new Board(scale);
        boardF.columns = shrunkBoard;
        return boardF;
    }

    //text Reading
    public Point readFileForMove(Board pBoard){
        Point move = new Point();

        //try-catch so it doesn't crash
        try {
            //Build File Reader
            File myObj = new File("boards.txt");
            Scanner myReader = new Scanner(myObj);

            //loop until we found the desired board in the file
            boolean foundBoard = false;
            while (!foundBoard) {
                String boardString = myReader.next(); //get board from file
                StringBuilder pBoardString = new StringBuilder(); //turn pBoard into string so we can compare them
                for(int i: pBoard.columns){
                    pBoardString.append(i);
                }

                if(boardString.equals(pBoardString.toString())){ //if fileBoard is the same as pBoard, finish the loop, its been found
                    foundBoard = true;
                    //System.out.println(boardString);
                }else {
                    myReader.nextLine();
                }
            }

            //the moves = the next integers after that board, because that's how I have the file written, EX. "Board xMove yMove"
            move.y = myReader.nextInt();
            move.x = myReader.nextInt();

            String win = myReader.next();
            isWinning = win.equals("W");

            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return move;
    }
    public void writeBoardsToFile(ArrayList<Board> boards){
        //create file
        try {
            File myObj = new File("boards.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //write to file
        try {
            FileWriter myWriter = new FileWriter("boards.txt");

            for (Board i : boards) {
                for(int w: i.columns) {
                    myWriter.write(w + "");
                }
                myWriter.write(" " + i.bestMove.x + " " + i.bestMove.y);
                if(i.isWin == -1){
                    myWriter.write(" L" + "\n");
                }else{
                    myWriter.write(" W" + "\n");
                }
            }

            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    //Serialization
    public void writeSerialized(ArrayList<Board> boards){
        try {
            FileOutputStream fileStream = new FileOutputStream("/Users/RioVK/Desktop/Computer Programing/Chomp 2.1 Beta/boards.ser");
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            objectStream.writeObject(boards);
            objectStream.close();
            fileStream.close();
        }
        catch(IOException i){
            i.printStackTrace();
        }
    }
    public Point getMove(Board board, ArrayList<Board> pBoards){
        Point move = new Point();
        if(board.isWin == -1){
            isWinning = true;
        }else{
            isWinning = false;
        }
        for(Board b: pBoards){
            if(Arrays.equals(b.columns,board.columns)){
                move = b.bestMove;
            }
        }
        move = new Point(move.y,move.x);
        return move;
    }
}
