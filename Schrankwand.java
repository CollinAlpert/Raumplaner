import java.awt.*;
import java.awt.geom.*;

public class Schrankwand extends Moebel {

    Schrankwand() {
        this.x = X;
        this.y = Y;
        this.width = 120;
        this.height = 40;
        this.rotation = 0;
        this.color = Color.blue;
    }

    public double[] getDefault() {
        return new double[] {120, 40};
    }

    public Shape getShape() {
        GeneralPath wand = new GeneralPath();
        wand.append(new Schrank(0, 0).getShape(), false);
        wand.append(new Schrank(40, 0).getShape(), false);
        wand.append(new Schrank(80, 0).getShape(), false);
        // transformieren:
        AffineTransform t = new AffineTransform();
        t.translate(x, y);
        Rectangle2D umriss = wand.getBounds2D();
        t.rotate(Math.toRadians(rotation),umriss.getX()+umriss.getWidth()/2,umriss.getY()+umriss.getHeight()/2);
        return  t.createTransformedShape(wand);
    }
}
