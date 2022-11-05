import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SerialReader implements Runnable{

    ArrayList<Board> boards;
    private Thread t;
    public double percentLoaded;

    public SerialReader(){
        boards = new ArrayList<>();
    }

    @Override
    public void run() {
        boards = getSerializedData();
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "readingSerial");
            t.start();
        }
    }

//***********************************************************************************
    //read serial file
//    public ArrayList<Board> getSerializedData(){
//        ArrayList<Board> boards = new ArrayList<>();
//        percentLoaded = 25;
//        try{
//            FileInputStream fileIn = new FileInputStream("/Users/RioVK/Desktop/Computer Programing/Chomp/boards.ser");
//            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
//            percentLoaded = 50;
//            boards = (ArrayList<Board>) objectIn.readObject();
//            percentLoaded = 75;
//            objectIn.close();
//            fileIn.close();
//        }catch(IOException | ClassNotFoundException i){
//            i.printStackTrace();
//        }
//        percentLoaded = 100;
//        System.out.println("Serial Reading Complete");
//        System.out.println(percentLoaded);
//        return boards;
//    }

    public ArrayList<Board> getSerializedData(){
        ArrayList<Board> boards = new ArrayList<>();
        percentLoaded = 25;
        try{
            FileInputStream fileIn = new FileInputStream("/Users/RioVK/Desktop/Computer Programing/Chomp/boards.ser");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            percentLoaded = 50;
            boards = (ArrayList<Board>) objectIn.readObject();
            percentLoaded = 75;
            objectIn.close();
            fileIn.close();
        }catch(IOException | ClassNotFoundException i){
            i.printStackTrace();
        }
        percentLoaded = 100;
        System.out.println("Serial Reading Complete");
        System.out.println(percentLoaded);
        return boards;
    }


    //read text file
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

            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return move;
    }
}


