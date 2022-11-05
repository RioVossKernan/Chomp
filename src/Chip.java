import java.awt.*;

public class Chip {
    //variables
    public int xPos;
    public int yPos;
    public int width;
    public int height;
    public boolean isAlive;
    public Rectangle rec;

    public Chip(int row, int col, int xOff, int yOff, int size){

        isAlive = true;
        xPos = col*size+xOff;
        yPos = (9-row)*size+yOff;
        rec = new Rectangle(xPos, yPos, size, size);
    }

}
