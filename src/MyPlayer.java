//Rio Voss-Kernan
//October 2022
//Algorithm for creating a perfect chomp player with integration into the game itself

import java.awt.*;
import java.util.*;

public class MyPlayer {
    final int scale = 10;
    boolean isWinning = false;

    public MyPlayer() {
        //writeSerialized(boards);
        //writeBoardsToFile(boards);
    }

    //THIS IS CALLED WHEN YOU CLICK COMPUTER BUTTON
    //PBoard is board on screen
    //boards is arrayList of boards with stored bestMoves
    public Point move(Chip[][] pBoard, ArrayList<Board> boards) {
        Board board = chipArrayToIntArray(pBoard, false);
        return getMove(board,boards);
    }

    //Convert Chomp Board to Rio Board
    public Board chipArrayToIntArray(Chip[][] pBoard, boolean isPrint){
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

    //Parse for move through given ArrayList
    public Point getMove(Board board, ArrayList<Board> pBoards){
        Point move = new Point();
        isWinning = (board.isWin == -1);
        for(Board b: pBoards){
            if(Arrays.equals(b.columns,board.columns)){
                move = b.bestMove;
            }
        }
        move = new Point(move.y,move.x);
        return move;
    }
}
