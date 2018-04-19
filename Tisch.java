import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Tisch extends Moebel {

    Tisch() {
        this.x = X;
        this.y = Y;
        this.width = 120;
        this.height = 100;
        this.rotation = 0;
        color = Color.blue;
    }

    public double[] getDefault() {
        return new double[] {120, 100};
    }

    public Shape getShape() {
        Shape tisch = new Ellipse2D.Double(0 , 0, width, height);
        AffineTransform t = new AffineTransform();
        t.translate(x, y);
        Rectangle2D umriss = tisch.getBounds2D();
        t.rotate(Math.toRadians(rotation),umriss.getX()+umriss.getWidth()/2,umriss.getY()+umriss.getHeight()/2);
        return  t.createTransformedShape(tisch);
    }
}
