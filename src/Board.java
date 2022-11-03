import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.*;

public class Board implements Serializable {
    int[] columns;
    int isWin; // -1 = lose; 1 = win
    Point bestMove;
    Board bestMoveBoard;
    ArrayList<Board> moves;
    int indexInBoards = 0;

    public Board(int scale){
        columns = new int[scale];
        isWin = 0;
    }
    public Board(int x1, int x2, int x3){
        columns = new int[]{x1,x2,x3};
        isWin = 0;
    }
    public Board(int x1, int x2){
        columns = new int[]{x1,x2};
        isWin = 0;
    }
    public Board(int x1, int x2, int x3,int x4,int x5,int x6,int x7,int x8,int x9,int x10){
        columns = new int[]{x1,x2,x3,x4,x5,x6,x7,x8,x9,x10};
        isWin = 0;
    }


    public void printArray(){
        System.out.print(Arrays.toString(columns));
    }

    public Board clone(){
        Board board = new Board(columns.length);
        board.isWin = isWin;
        board.columns = columns;
        return board;
    }
}
