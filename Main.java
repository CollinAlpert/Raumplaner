import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main extends JFrame {

    private Canvas canvas;

    private JPanel templatePanel = new JPanel();
    private JCheckBox useGrid = new JCheckBox("Raster anzeigen");
    final static int width = 800;
    final static int height = 800;
    //Map für eigene Templates
    private Map<String, ArrayList<Moebel>> templateList = new HashMap<>();
    private ArrayList<Moebel> tempList = new ArrayList<>();
    private ArrayList<Shape> tempShapeList = new ArrayList<>();
    private JTextField field = new JTextField();
    private JPanel p = getPanel();

    private Main() {
        super("Leinwand");
        setLayout(new BorderLayout());
        canvas = new Canvas();
        add(canvas, BorderLayout.CENTER);
        add(p, BorderLayout.WEST);

        JButton button = new JButton("Speichern");
        field.setPreferredSize(new Dimension(150, 20));
        button.addActionListener(e -> {
            templateList.put(field.getText(), new ArrayList<>(canvas.getList()));
            p.removeAll();
            p.add(getPanel());
            canvas.reset();
            canvas.setList(new ArrayList<>(tempList));
            canvas.setShapeList(new ArrayList<>(tempShapeList));
            tempList.clear();
            tempShapeList.clear();
            templatePanel.setVisible(false);
        });
        templatePanel.add(field);
        templatePanel.add(button);
        templatePanel.setVisible(false);
        add(templatePanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(button);
        setResizable(false);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }

    private JPanel getPanel() {
        JPanel pan = new JPanel(new GridBagLayout());
        ArrayList<JButton> buttons = new ArrayList<>(Arrays.asList(new JButton("Badewanne"), new JButton("Doppelbett"),
                new JButton("Schrank"), new JButton("Schrankwand"), new JButton("Stuhl"),
                new JButton("Tisch"), new JButton("Waschmaschine"), new JButton("Reset"), new JButton("Neue Gruppe")));
        templateList.forEach((key, value) -> {
            JButton b = new JButton(key);
            b.addActionListener(e -> value.forEach(canvas::addItem));
            buttons.add(b);
        });
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.insets = new Insets(5, 0, 5, 0);
        for (int i = 0; i < buttons.size(); i++) {
            c.gridy = i;
            pan.add(buttons.get(i), c);
        }
        c.gridy = buttons.size();
        pan.add(useGrid, c);
        pan.setBackground(Color.red);

        //ActionListener für das Erstellen der Möbel Objekte
        buttons.get(0).addActionListener(e -> canvas.addItem(new Badewanne()));
        buttons.get(1).addActionListener(e -> canvas.addItem(new Doppelbett()));
        buttons.get(2).addActionListener(e -> canvas.addItem(new Schrank()));
        buttons.get(3).addActionListener(e -> canvas.addItem(new Schrankwand()));
        buttons.get(4).addActionListener(e -> canvas.addItem(new Stuhl()));
        buttons.get(5).addActionListener(e -> canvas.addItem(new Tisch()));
        buttons.get(6).addActionListener(e -> canvas.addItem(new Waschmaschine()));
        buttons.get(7).addActionListener(e -> canvas.reset());
        buttons.get(8).addActionListener(e -> {
            tempList = new ArrayList<>(canvas.getList());
            tempShapeList = new ArrayList<>(canvas.getShapeList());
            canvas.reset();
            field.grabFocus();
            field.requestFocus();
            templatePanel.setVisible(true);
        });
        useGrid.addActionListener(e -> canvas.toggleGrid());
        return pan;
    }
}
