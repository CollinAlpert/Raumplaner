import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public class Schrank extends Moebel {

    Schrank() {
        this.x = X;
        this.y = Y;
        this.width = 40;
        this.height = 40;
        this.rotation = 0;
        this.color = Color.blue;
    }

    //Dies ist die einzige Klasse, die zwei Constructor benötigt. Dies liegt an der Schrankwand.
    Schrank(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 40;
        this.rotation = 0;
        this.color = Color.blue;
    }

    public double[] getDefault() {
        return new double[] {40, 40};
    }

    public Shape getShape() {
        GeneralPath schrank = new GeneralPath();
        schrank.append(new Rectangle2D.Double(0, 0, width, height), false);
        schrank.moveTo(0 , 0);
        schrank.lineTo(width, height);
        schrank.moveTo(0, height);
        schrank.lineTo(width, 0);
        // transform:

        AffineTransform t = new AffineTransform();
        t.translate(x, y);
        Rectangle2D umriss = schrank.getBounds2D();
        t.rotate(Math.toRadians(rotation),umriss.getX()+umriss.getWidth()/2,umriss.getY()+umriss.getHeight()/2);
        return  t.createTransformedShape(schrank);
    }
}
