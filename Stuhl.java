import java.awt.*;
import java.awt.geom.*;

public class Stuhl extends Moebel {

    Stuhl() {
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
        GeneralPath stuhl = new GeneralPath();
        stuhl.moveTo(0 , 0);
        stuhl.lineTo(width, 0);
        stuhl.lineTo(width+(width/20+1), height);
        stuhl.lineTo(-(width/20+1), height);
        stuhl.lineTo(0 , 0);
        // Das ist die Umrandung. Das Stuhl bekommt noch eine Lehne:
        stuhl.moveTo(0 , (width/10+1));
        stuhl.lineTo(width, (width/10+1));
        // transformieren:
        AffineTransform t = new AffineTransform();
        t.translate(x, y);
        Rectangle2D umriss = stuhl.getBounds2D();
        t.rotate(Math.toRadians(rotation),umriss.getX()+umriss.getWidth()/2,umriss.getY()+umriss.getHeight()/2);
        return t.createTransformedShape(stuhl);
    }

}
