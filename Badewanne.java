import java.awt.*;
import java.awt.geom.*;

public class Badewanne extends Moebel {

    Badewanne() {
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
        GeneralPath badewanne = new GeneralPath();
        Rectangle2D umriss = new Rectangle2D.Double(0 , 0, width, height); //können wir gut gebrauchen!
        badewanne.append(umriss, false);

        Line2D obererRand = new Line2D.Double(0.1*height, 0.1*height, width - 0.5*height, 0.1*height);
        Line2D linkerRand = new Line2D.Double(0.1*height, 0.1*height, 0.1*height, 0.9*height);
        Line2D untererRand = new Line2D.Double(0.1*height, 0.9*height, width - 0.5*height, 0.9*height);
        Arc2D bogen = new Arc2D.Double(width - 0.9*height, 0.1*height,  0.8*height, 0.8*height, 270, 180, Arc2D.OPEN);
        badewanne.append(obererRand, false);
        badewanne.append(linkerRand, false);
        badewanne.append(untererRand, false);
        badewanne.append(bogen, false);

        Ellipse2D ablauf = new Ellipse2D.Double(0.1*width, 0.5*height-2, width/10, height/10);
        badewanne.append(ablauf, false);

        // transformieren
        AffineTransform t = new AffineTransform();
        t.translate(x, y);
        t.rotate(Math.toRadians(rotation),umriss.getX()+umriss.getWidth()/2,umriss.getY()+umriss.getHeight()/2);
        return  t.createTransformedShape(badewanne);
    }
}
