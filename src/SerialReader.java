import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

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








}


