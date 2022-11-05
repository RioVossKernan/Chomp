import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class SerialReader implements Runnable{

    ArrayList<Board> boards;
    private Thread t;

    public SerialReader(){
        boards = new ArrayList<>();
    }

    public ArrayList<Board> getSerializedData(){
        ArrayList<Board> boards = new ArrayList<>();
        try{
            FileInputStream fileIn = new FileInputStream("/Users/RioVK/Desktop/Computer Programing/Chomp/boards.ser");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            boards = (ArrayList<Board>) objectIn.readObject();
            objectIn.close();
            fileIn.close();
        }catch(IOException | ClassNotFoundException i){
            i.printStackTrace();
        }
        return boards;
    }

    @Override
    public void run() {
        boards = getSerializedData();
    }

    public void start(){
        if (t == null) {
            t = new Thread (this, "readingSerial");
            t.start ();
        }
    }
}


