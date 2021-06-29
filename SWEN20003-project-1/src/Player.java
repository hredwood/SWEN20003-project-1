import bagel.Image;
import bagel.util.Point;

public class Player extends Entity{
    private int energy;
    public Player(Point coordinates, Image image, int energy){
        super(coordinates, image);
        this.energy= energy;
    }

    /**
     * getter and setter for energy of player
    */
    public int getEnergy() {
        return energy;
    }
    public void setEnergy(int energy) {
        this.energy = energy;
    }

}
