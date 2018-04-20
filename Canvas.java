import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Collin Alpert
 * @version 2.5
 */

public class Canvas extends JPanel implements MouseMotionListener, MouseWheelListener, KeyListener {
    //ArrayList für alle Möbel
    private ArrayList<Moebel> list = new ArrayList<>();

    //ArrayList für Punkte der selbstgezeichneten Figuren
    private ArrayList<Point2D> customShapePoints = new ArrayList<>();

    //ArrayList für selbstgezeichnete Figuren
    private ArrayList<Shape> shapeList = new ArrayList<>();

    //Das aktuelle Möbel Object, damit vergrößern, verkleinern und verschieben korrekt funktioniert
    private Moebel currentObject;
    private Rectangle2D currentRect = null;

    //Das Vorschau-Objekt
    private Moebel preObject = null;

    private int startX = -1, startY = -1, endX = -2, endY = -2;

    //Sagt mir, ob zur Zeit geklickt wird
    private boolean isClicking = false;

    //Zwei Werte, die das Verschieben flüssiger machen
    private double preX, preY, rPreX, rPreY;
    private ArrayList<ArrayList<Double>> preList = new ArrayList<>();


    //Einteilung für das Raster
    private final double divisor = 10;

    private boolean showGrid = false;

    Canvas() {
        //MouseListener für vergrößern, verkleinern und verschieben.
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                //Das aktuelle Möbel Objekt wird gesucht
                currentObject = isTouching(e.getX(), e.getY());
                //Damit das Verschieben nur mit der linken Maustaste funktioniert
                if (!SwingUtilities.isRightMouseButton(e) && currentObject != null) {
                    preX = currentObject.getX() - e.getX();
                    preY = currentObject.getY() - e.getY();
                } else if (!SwingUtilities.isRightMouseButton(e) && currentRect != null && currentObject == null) {
                    rPreX = currentRect.getX() - e.getX();
                    rPreY = currentRect.getY() - e.getY();
                    for (Moebel m : list) {
                        if (currentRect != null) {
                            preList.add(new ArrayList<Double>() {{
                                add(m.getX() - e.getX());
                                add(m.getY() - e.getY());
                            }});
                        }
                    }
                }
                //Bei einem rechten Mausklick wird das Vorschau-Objekt instanziert
                if (SwingUtilities.isRightMouseButton(e) && e.isShiftDown()) cycle(e.getX(), e.getY(), true, false);
                //Wenn Shift gehalten wird während man klickt, wird das Objekt gelöscht.
                if (e.isShiftDown() && currentRect != null && currentObject == null && currentRect.contains(e.getX(), e.getY())) {
                    for (int i = 0; i < list.size(); i++) {
                        if (currentRect.contains(list.get(i).getX(), list.get(i).getY())) {
                            list.set(i, null);
                        }
                    }
                } else if (e.isShiftDown()) list.remove(currentObject);
                list.removeIf(Objects::isNull);
                //Es findet ein Klick statt
                isClicking = true;
                //Die Leinwand bekommt immer wieder Fokus. Dies ist für den KeyListener wichtig
                Canvas.super.requestFocus();
                //Wenn die ctrl Taste während eines Klicks gedrückt wird, wird das aktuelle
                // Objekt ins Raster versetzt. Gibt es kein aktuelles Objekt, werden alle eingerastet.
                if (e.isControlDown()) snapToGrid(currentObject);
                if (e.getClickCount() == 2) preObject = null;

                removeRect(e);
                repaint();
            }

            public void mouseReleased(MouseEvent e) {
                //Kein Klick mehr
                isClicking = false;
                preList.clear();

                if (customShapePoints.size() > 0) {
                    GeneralPath path = new GeneralPath();
                    path.moveTo(customShapePoints.get(0).getX(), customShapePoints.get(0).getY());
                    for (int i = 1; i < customShapePoints.size(); i++)
                        path.lineTo(customShapePoints.get(i).getX(), customShapePoints.get(i).getY());
                    shapeList.add(path);
                    customShapePoints.clear();
                }
                repaint();
            }
        });
        //KeyListener für das Ändern der Farbe
        this.addKeyListener(this);
    }

    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;
        //Die Raster Linien werden gezeichnet
        if (showGrid) {
            g.setColor(Color.lightGray);
            double xStep = this.getSize().getWidth() / divisor;
            double yStep = this.getSize().getHeight() / divisor;
            for (double i = xStep; i < divisor * xStep; i += xStep)
                g.draw(new Line2D.Double(i, 0, i, this.getSize().getHeight()));
            for (double i = yStep; i < divisor * yStep; i += yStep)
                g.draw(new Line2D.Double(0, i, this.getSize().getWidth(), i));
        }
        //Alle Linien sind 2 Pixel dick
        g.setStroke(new BasicStroke(2));
        //Jedes Möbel Objekt aus list wird gezeichnet
        list.forEach(e -> {
            g.setColor(e.getColor());
            g.draw(e.getShape());
        });
        g.setColor(Color.lightGray);
        if (currentRect != null) g.draw(currentRect);
        //Selbstgezeichnete Sachen werden gezeichnet (optional)
        g.setColor(Color.black);
        shapeList.forEach(g::draw);
        //Hier wird das Vorschau Objekt gezeichnet, wenn es existiert.
        g.setColor(new Color(141, 207, 246));
        if (preObject != null) g.draw(preObject.getShape());
    }

    private Moebel isTouching(int x, int y) {
        //Hier wird geguckt, ob die Koordinaten innerhalb eines Möbel Objektes liegen. Wenn nicht, wird es nicht in den Array hinzugefügt
        Moebel[] m = list.stream().filter(e -> (x >= e.getX() && x <= e.getX() + e.getWidth()) && (y >= e.getY() && y <= e.getY() + e.getHeight())).toArray(Moebel[]::new);
        //Sollte nichts gefunden worden sein, wird null zurückgegeben
        return m.length == 0 ? null : m[0];
    }

    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e) && isClicking) {
            customShapePoints.add(e.getPoint());
        } else if (currentObject != null) {
            //Mit einem gedrückt gehaltenem links Klick wird bewegt
            currentObject.setX(preX + e.getX());
            currentObject.setY(preY + e.getY());
            repaint();
        } else if (startX == -1 && startY == -1) {
            //Definiert den Startpunkt des Rechtecks
            startX = e.getX();
            startY = e.getY();
        } else if (currentRect != null && currentRect.contains(e.getX() + 5, e.getY() + 5) && currentObject == null) {
            //Verschiebt das Rechteck
            currentRect.setRect(rPreX + e.getX(), rPreY + e.getY(), currentRect.getWidth(), currentRect.getHeight());
            for (int i = 0; i < list.size(); i++) {
                if (currentRect.contains(list.get(i).getX(), list.get(i).getY())) {
                    list.get(i).setX(preList.get(i).get(0) + e.getX());
                    list.get(i).setY(preList.get(i).get(1) + e.getY());
                }
            }
            repaint();
        } else if (endX != -1 && endY != -1) {
            //Vergrößert oder verkleinert das Rechteck
            if ((currentRect != null && !currentRect.contains(e.getX(), e.getY())) || currentRect == null) {
                endX = e.getX();
                endY = e.getY();
                currentRect = new Rectangle2D.Double(startX, startY, endX - startX, endY - startY);
                repaint();
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        //Hier wird bestimmt, in welche Richtung gescrollt wird
        boolean positive = e.getWheelRotation() > 0;
        //Nochmals wird das aktuelle Objekt bestimmt
        currentObject = isTouching(e.getX(), e.getY());
        //Wenn gleichzeitig geklickt und gescrollt wird, wird das Objekt rotiert. Ausgehend von der Scrollrichtung, natürlich.
        if (e.isShiftDown() && currentRect != null && currentObject == null && currentRect.contains(e.getX(), e.getY())) {
            for (Moebel m : list)
                if (currentRect.contains(m.getX(), m.getY()))
                    if (positive) m.setRotation(m.getRotation() - 10);
                    else m.setRotation(m.getRotation() + 10);
        } else if (currentRect != null && currentObject == null && currentRect.contains(e.getX(), e.getY())) {
            for (Moebel m : list)
                if (currentRect.contains(m.getX(), m.getY()))
                    resize(positive, m);
        } else if (e.isShiftDown() && currentObject != null) {
            if (positive) currentObject.setRotation(currentObject.getRotation() - 10);
            else currentObject.setRotation(currentObject.getRotation() + 10);
            //Bei Scrolling ohne Klick, wird das Objekt vergrößert oder verkleinert
        } else if (currentObject != null) {
            resize(positive, currentObject);
        }
        repaint();
    }

    private void resize(boolean positive, Moebel m) {
        if (positive) {
            m.setWidth(m.getWidth() - 5);
            m.setHeight(m.getHeight() - 5);
        } else {
            m.setWidth(m.getWidth() + 5);
            m.setHeight(m.getHeight() + 5);
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        //Wenn geklickt und gleichzeitig ein Buchstabe getippt wird, ändert sich die Farbe nach der colorCode() Methode
        if (isClicking && currentObject != null) {
            char c = e.getKeyChar();
            //Wenn die Taste "0" während eines Klicks gedrückt wird, wird das aktuelle Objekt in seinen originalzustand versetzt.
            if (c == '0') {
                currentObject.setWidth(currentObject.getDefault()[0]);
                currentObject.setHeight(currentObject.getDefault()[1]);
                currentObject.setRotation(0);
            } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {/*Für Windows Keyboards nötig*/} else
                currentObject.setColor(colorCode(c));
        }
        //Mit den Pfeiltasten wird das Vorschau Objekt gewechselt
        if (e.getKeyCode() == KeyEvent.VK_DOWN && preObject != null)
            cycle(preObject.getX(), preObject.getY(), false, false);
        if (e.getKeyCode() == KeyEvent.VK_UP && preObject != null)
            cycle(preObject.getX(), preObject.getY(), false, true);
        //Mit der Enter Taste übernimmt man das Vorschau Objekt in die reguläre Liste
        if (e.getKeyCode() == KeyEvent.VK_ENTER && preObject != null) {
            list.add(preObject);
            preObject = null;
        }
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    private Color colorCode(char c) {
        HashMap<Character, Color> colorMap = new HashMap<Character, Color>() {{
            put('r', Color.red);
            put('b', Color.blue);
            put('y', Color.yellow);
            put('g', Color.green);
            put('p', Color.magenta);
            put('w', Color.white);
        }};
        if (colorMap.containsKey(c)) return colorMap.get(c);
        return Color.black;
    }

    private void snapToGrid(Moebel object) {
        //Der Rasterabstand wird denfiniert
        double xStep = this.getSize().getWidth() / divisor;
        double yStep = this.getSize().getHeight() / divisor;
        ArrayList<Double> xPoints = new ArrayList<>();
        ArrayList<Double> yPoints = new ArrayList<>();
        //Alle Punkte werden in die ArrayListes übertragen
        for (double i = 0; i <= this.getSize().getWidth(); i += xStep) xPoints.add(i);
        for (double i = 0; i <= this.getSize().getHeight(); i += yStep) yPoints.add(i);
        //Das ganze kommt in eine 2-dimensionale ArrayList
        ArrayList<ArrayList<Double>> points = new ArrayList<>(Arrays.asList(xPoints, yPoints));
        //Hier wird bestimmt, ob ein Objekt eingerastet werden muss, oder alle
        if (object != null) snap(points, object);
        else list.forEach(e -> snap(points, e));
    }

    private void snap(ArrayList<ArrayList<Double>> pos, Moebel m) {
        for (int i = 0; i < pos.get(0).size() - 1; i++) {
            //X-Achse
            //Wenn die Mitte des Objektes zwischen zwei Raster-Punkten ist wird geguckt welcher näher dran ist und das Objekt wird dann dahin geschoben.
            if (m.getMiddle('x') > pos.get(0).get(i) && m.getMiddle('x') < pos.get(0).get(i + 1)) {
                //Damit das Objekt nicht am Rand positioniert wird
                if (i == 0) i = 1;
                //Damit das Objekt nicht am Rand positioniert wird
                if (i == divisor - 1) i = (int) divisor - 2;
                m.setX((m.getMiddle('x') - pos.get(0).get(i) < pos.get(0).get(i + 1) - m.getMiddle('x') ? pos.get(0).get(i) : pos.get(0).get(i + 1)) - m.getWidth() / 2);
            }
            //Y-Achse
            //Wenn die Mitte des Objektes zwischen zwei Raster-Punkten ist wird geguckt welcher näher dran ist und das Objekt wird dann dahin geschoben.
            if (m.getMiddle('y') > pos.get(1).get(i) && m.getMiddle('y') < pos.get(1).get(i + 1)) {
                //Damit das Objekt nicht am Rand positioniert wird
                if (i == 0) i = 1;
                //Damit das Objekt nicht am Rand positioniert wird
                if (i == divisor - 1) i = (int) divisor - 2;
                m.setY((m.getMiddle('y') - pos.get(1).get(i) < pos.get(1).get(i + 1) - m.getMiddle('y') ? pos.get(1).get(i) : pos.get(1).get(i + 1)) - m.getHeight() / 2);
            }
        }
    }

    private void cycle(double x, double y, boolean override, boolean up) {
        //Anfangs-Instanzierung
        if (override) {
            Badewanne pre = new Badewanne();
            pre.setX(x - pre.getWidth() / 2);
            pre.setY(y - pre.getHeight() / 2);
            preObject = pre;
        } else if (preObject != null) {
            //Hier findet der Welchsel statt.
            Moebel[] objects = {new Badewanne(), new Doppelbett(), new Schrank(),
                    new Schrankwand(), new Stuhl(), new Tisch(), new Waschmaschine()};
            int pos = 0;
            //Erst wird geguckt um welches Objekt es sich handelt und an welcher Stelle sich dies befindet
            for (int i = 0; i < objects.length; i++)
                if (objects[i].getClass() == preObject.getClass()) {
                    pos = i;
                    break;
                }
            //2 Ternary Operators, die das richtige Objekt basierend auf den Pfeiltasten zurückliefern
            Moebel z = !up ? pos == objects.length - 1 ? objects[0] : objects[pos + 1] : pos == 0 ? objects[objects.length - 1] : objects[pos - 1];
            //Dann wird das Objekt positioniert
            z.setX(preObject.getMiddle('x') - z.getWidth() / 2);
            z.setY(preObject.getMiddle('y') - z.getHeight() / 2);
            preObject = z;
        }
        repaint();
    }

    public void reset() {
        list.clear();
        preObject = null;
        shapeList.clear();
        repaint();
    }

    private void removeRect(MouseEvent e) {
        if (currentRect != null && !currentRect.contains(e.getX(), e.getY())) {
            startX = -1;
            startY = -1;
            currentRect = null;
            preList.clear();
        }
    }

    public void updateView() {
        repaint();
    }

    public void addItem(Moebel moebel) {
        list.add(moebel);
        repaint();
    }

    public ArrayList<Moebel> getList() {
        return list;
    }

    public void setList(ArrayList<Moebel> list) {
        this.list = list;
    }

    public ArrayList<Shape> getShapeList() {
        return shapeList;
    }

    public void setShapeList(ArrayList<Shape> shapeList) {
        this.shapeList = shapeList;
    }

    public void toggleGrid() {
        showGrid = !showGrid;
        repaint();
    }
}
