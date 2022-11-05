//Rio Voss-Kernan
//October 2022
//Algorithm for creating a perfect chomp player with integration into the game itself

import java.awt.*;
import java.util.*;
import java.io.*;

public class MyPlayer {
    final int scale = 10;
    boolean isWinning = false;

    private int boardsLoaded;
    public double percentLoaded;
    private double logLoad;
    public ArrayList<Board> boards;
    private final ArrayList<int[]> losses = new ArrayList<>();
    private boolean isSerial = false;

    private SerialReader serialReader;

    public MyPlayer() {
        //boards = findBestLosingMoves(findLossesAndWins(generateBoards(false), false), true);
        //boards = findLossesAndWins(generateBoards(false), false);

        //writeSerialized(boards);
        //writeBoardsToFile(boards);

        if(isSerial){
            serialReader.start();
        }
    }

    //add your code to return the row and the column of the chip you want to take.
    //you'll be returning a data type called Point which consists of two integers.
    public Point move(Chip[][] pBoard) {
        Point move;
        Board board = chipArrayToIntArray(pBoard, false);
        move = readFileForMove(board);

        if(isSerial){
            move = getMove(board,serialReader.boards);
        }
        return move;
    }

//****************************************************************************************************************
    public void updateLoadingScreen(){
        if(Math.log(boardsLoaded) - logLoad >= 0.25){
            logLoad = Math.log(boardsLoaded);
            percentLoaded = (logLoad/12.126) * 100;
            System.out.println(percentLoaded);
        }
    }

    //Data generation methods
    public ArrayList<Board> generateBoards(boolean isPrint) {
        //A board is a array in integers. Each integer represents a amount of dots in a column (ex.  :: = 22)
        //boards is a arraylist of every possible board
        ArrayList<Board> boards = new ArrayList<>();
        Board board = new Board(scale);

        //Loop until the highest index reaches the highest possible value
        while (board.columns[board.columns.length - 1] != board.columns.length) {

            //find the index with the smallest number
            int lowestIndex = 0;
            for (int i = 0; i < board.columns.length; i++) {
                if (board.columns[i] < board.columns[lowestIndex]) {
                    lowestIndex = i;
                }
            }

            //increase the smallest index by one
            board.columns[lowestIndex]++;

            //all indexes to the right of the increased index, become zero
            for (int x = lowestIndex + 1; x < board.columns.length; x++) {
                board.columns[x] = 0;
            }

            //add current board to the list of boards
            Board buffer = new Board(board.columns.length);
            buffer.columns = board.columns.clone();
            boards.add(buffer);
        }

        //Print
        if (isPrint) {
            for (Board i : boards) {
                i.printArray();
                System.out.println();
            }
            System.out.println("Number of Boards = " + boards.size());
        }

        System.out.println("BOARDS GENERATED");
        return boards;
    }
    public ArrayList<Board> possibleMoves(Board pBoard, boolean isPrint) {
        ArrayList<Board> moves = new ArrayList<>();
        //for loop through indexes (column of chomp)
        for (int x = pBoard.columns.length-1; x >= 0 ; x--) {
            //loop through values for that column
            for (int y = pBoard.columns[x]-1; y >= 0 ; y--) {
                //declare board as equal to parameter board
                Board board = new Board(pBoard.columns.length);
                board.columns = pBoard.columns.clone();
                board.columns[x] = y;

                //all digits to the right of the changed digit, become changed digit
                for (int i = x + 1; i < board.columns.length; i++) {
                    if (board.columns[i] > y) {
                        board.columns[i] = y;
                    }
                }

                //if the move isn't clicking on blue chip, add to list of moves
                int[] blueChip = new int[pBoard.columns.length];
                for (int i = 1; i < pBoard.columns.length; i++) {
                    blueChip[i] = 0;
                }
                if (!Arrays.equals(board.columns, blueChip)) {
                    moves.add(board);
                }
            }
        }


        //Print
        if (isPrint) {
            for (Board i : moves) {
                i.printArray();
                System.out.println();
            }
            System.out.println("Number of Moves = " + moves.size());
        }

        return moves;
    }
    public Board canIMakeALosingBoard(Board pBoard) {
        Board lossBoard = new Board(scale);
        boolean boardFound = false;
        Board board = new Board(pBoard.columns.length);
        //for loop through indexes (column of chomp)
        for (int x = 0; x < pBoard.columns.length; x++) {
            //loop through values for that column
            for (int y = 0; y < pBoard.columns[x]; y++) {
                //declare board as equal to parameter board
                board.columns = pBoard.columns.clone();
                board.columns[x] = y;

                //all digits to the right of the changed digit, become changed digit
                for (int i = x + 1; i < board.columns.length; i++) {
                    if (board.columns[i] > y) {
                        board.columns[i] = y;
                    }
                }
                for(int[] i: losses){
                    if(Arrays.equals(i,board.columns)){
                        lossBoard = board;
                        boardFound = true;
                        break;
                    }
                }
                if(boardFound){ break; }
            }
            if(boardFound){ break; }
        }
        return lossBoard;
    }
    public ArrayList<Board> findLossesAndWins(ArrayList<Board> boards, boolean isPrint) {
        Board noLossBuffer = new Board(scale);

        //loop every board. starting simple adding complexity
        for (Board b : boards) {

            b.bestMoveBoard = canIMakeALosingBoard(b);
            b.bestMove = makeAMove(b, b.bestMoveBoard);

            //if this board can make a loss its a winner
            if (!Arrays.equals(b.bestMoveBoard.columns, noLossBuffer.columns)) {
                b.isWin = 1;
            } else {
                // if it cannot make a loss for opponent, that means every move makes a winning board for the opponent,
                // therefore this is now a losing board
                b.isWin = -1;
                losses.add(b.columns.clone());
            }

            boardsLoaded++;
            updateLoadingScreen();
        }

        //Print
        if (isPrint) {
            System.out.println("Winning Boards: " + (boards.size() - losses.size()));
            for (Board i : boards) {
                if (i.isWin == 1) {
                    i.printArray();
                    System.out.print("   ");
                    System.out.print("Chip: (" + i.bestMove.x + "," + i.bestMove.y + ")");
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println("Losing Boards:" + losses.size());
            for (Board i : boards) {
                if (i.isWin == -1) {
                    i.printArray();
                    System.out.println();
                }
            }
        }

        return boards;
    }
    public ArrayList<Board> findBestLosingMoves(ArrayList<Board> boards, boolean isPrint) {
        //find best moves for situations where you should lose no matter what
        //loop through losses
        ArrayList<Board> wins = new ArrayList<>();
        ArrayList<Board> losses = new ArrayList<>();
        for(Board i: boards){
            if(i.isWin == 1){
                wins.add(i);
            }else{
                losses.add(i);
            }
        }

        for (Board b : losses) {
            ArrayList<Board> moves = possibleMoves(b, false);
            int largestBoard = 0;
            b.bestMove = new Point(0, 0);

            //loop through moves.
            // find the move that forced your opponent to make the most moves to kill you. leaving more room for mistakes.
            for (Board m : wins) {
                for (Board x : moves) {
                    //this double loop thing is because we need the moves,
                    // but they need to have the bestMove stored, so we use the boards (which store bestMove) that match the moves arraylist
                    if (Arrays.equals(x.columns, m.columns)) {
                        int boardSize = 0;

                        for (int c : m.bestMoveBoard.columns) {
                            boardSize += c;
                        }
                        if (boardSize > largestBoard) {
                            largestBoard = boardSize;
                            b.bestMoveBoard = m;
                            b.bestMove = makeAMove(b, m);
                        }
                    }
                }
            }
        }


        //PRINT
        //Get losses so I can printArray out the number
        int numOfLosses = 0;
        for (Board i : boards) {
            if (i.isWin == -1) {
                numOfLosses++;
            }
        }
        if (isPrint) {
            System.out.println("LIST OF BOARDS");
            System.out.print("Winning Boards: " + (boards.size() - numOfLosses));
            System.out.println("  Losing Boards:" + numOfLosses);
            for (Board i : boards) {
                i.printArray();
                System.out.print("   ");
                System.out.print("Chip: (" + i.bestMove.x + "," + i.bestMove.y + ")");
                System.out.print("   ");
                if (i.isWin == -1) {
                    System.out.print("Loss");
                }else if(i.isWin == 1){
                    System.out.print("Win");
                }
                System.out.println();
            }
        }
        System.out.println("DONE");
        System.out.println();

        return boards;
    }
    public Point makeAMove(Board boardI, Board boardF) {
        //find the x value
        //moving from left to right, when does boardI become different from boardF
        //the column where is changes must be the column that was clicked
        int indexOfChange = boardI.columns.length;
        for (int i = 0; i < boardI.columns.length; i++) {
            if (boardI.columns[i] != boardF.columns[i]) {
                indexOfChange = i;
                break;
            }
        }
        //how high up did it change
        //the height it changes at must be the height that was clicked
        int heightOfChange;
        try { //try because if you input a impossible move it will crash
            heightOfChange = boardF.columns[indexOfChange];
        } catch (Exception e) {
            System.out.println("****************** Impossible Move Attempted *****************");
            return new Point(0, 0);
        }

        //the place to click is (whats the first column that was changed,whats the first row that was changed)
        return new Point(indexOfChange, heightOfChange);
    }

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
