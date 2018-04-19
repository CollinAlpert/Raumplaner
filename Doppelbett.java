import java.awt.*;
import java.awt.geom.*;

public class Doppelbett extends Moebel {

    Doppelbett() {
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
        GeneralPath dbett = new GeneralPath();
        dbett.moveTo(0 , 0);
        dbett.lineTo(width, 0);
        dbett.lineTo(width, height);
        dbett.lineTo(0, height);
        dbett.lineTo(0, 0);
        dbett.moveTo(width, height/2);
        dbett.lineTo(0, height/2);
        dbett.lineTo(width, 0);
        dbett.moveTo(width, height);
        dbett.lineTo(0, height/2);
        // transformieren:
        AffineTransform t = new AffineTransform();
        t.translate(x, y);
        Rectangle2D umriss = dbett.getBounds2D();
        t.rotate(Math.toRadians(rotation),umriss.getX()+umriss.getWidth()/2,umriss.getY()+umriss.getHeight()/2);
        return t.createTransformedShape(dbett);
    }
}
