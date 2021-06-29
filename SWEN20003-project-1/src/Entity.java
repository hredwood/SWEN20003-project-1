import bagel.AbstractGame;
import bagel.Image;
import bagel.util.Point;

import java.awt.*;

public class Entity {
    private Point coordinates;
    private Image image;

    public Entity(Point coordinates, Image image){
        this.coordinates = coordinates;
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }
}
