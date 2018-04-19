import java.awt.*;
import java.awt.geom.*;

public class Waschmaschine extends Moebel {

    Waschmaschine() {
        this.x = X;
        this.y = Y;
        this.width = 40;
        this.height = 40;
        this.rotation = 0;
        this.color = Color.blue;
    }

    public double[] getDefault() {
        return new double[] {40, 40};
    }

    public Shape getShape() {
        GeneralPath wasch = new GeneralPath();
        wasch.moveTo(0 , 0);
        wasch.lineTo(width, 0);
        wasch.lineTo(width, height);
        wasch.lineTo(0, height);
        wasch.lineTo(0, 0);
        wasch.append(new Ellipse2D.Double(5, 5, width-10, height-10), false);
        wasch.append(new Ellipse2D.Double(20, 20, width-40, height-40), false);
        //transformieren:
        AffineTransform t = new AffineTransform();
        t.translate(x, y);
        Rectangle2D umriss = wasch.getBounds2D();
        t.rotate(Math.toRadians(rotation), umriss.getX() + umriss.getWidth() / 2, umriss.getY() + umriss.getHeight() / 2);
        return t.createTransformedShape(wasch);
    }
}
