import java.awt.*;
import java.util.Random;

public abstract class Moebel {
    //Die Werte für alle Möbel Objekte
    double x, y, width, height, rotation;
    Color color;
    //Zufällige Werte, wo die Mögel Objekte erstellt werden
    final double X = new Random().nextInt(Main.width - 200);
    final double Y = new Random().nextInt(Main.height - 100);

    double getX() {
        return x;
    }

    void setX(double x) {
        this.x = x;
    }

    double getY() {
        return y;
    }

    void setY(double y) {
        this.y = y;
    }

    double getWidth() {
        return width;
    }

    void setWidth(double width) {
        this.width = width;
    }

    double getHeight() {
        return height;
    }

    void setHeight(double height) {
        this.height = height;
    }

    double getRotation() {
        return rotation;
    }

    void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    abstract double[] getDefault();

    abstract Shape getShape();

    double getMiddle(char x) {
        return x == 'x' ? getX() + width / 2 : getY() + height / 2;
    }

    public String toString() {
        return getClass() + ": " + x + " " + y + " " + width + " " + height;
    }
}
