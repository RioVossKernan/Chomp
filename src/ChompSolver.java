import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ChompSolver implements Runnable{

    ArrayList<Board> boards;
    private Thread t;
    int scale;
    private final ArrayList<int[]> losses = new ArrayList<>();
    boolean isComplex;

    private int boardsLoaded;
    public double percentLoaded;
    private double logLoad;

    public ChompSolver(int scale, boolean isComplex){
        boards = new ArrayList<>();
        this.isComplex = isComplex;
        this.scale = scale;
    }

    @Override
    public void run() {
        boards = solveChomp(isComplex);
        System.out.println("Chomp Solver Terminated");
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "solvingChomp");
            t.start();
        }
    }

//***********************************************************************************
    public ArrayList<Board> solveChomp(boolean getLossMovesToo){
        if(getLossMovesToo){
            return findBestLosingMoves(findLossesAndWins(generateBoards(false),false),false);
        }else{
            return findLossesAndWins(generateBoards(false),false);
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


    public void updateLoadingScreen(){
        percentLoaded = (boardsLoaded/184755d) * 100;
        System.out.println(percentLoaded);
    }









}


