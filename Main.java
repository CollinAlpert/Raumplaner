import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main extends JFrame {

    private Canvas canvas;

    private JPanel panel = new JPanel();
    private JPanel p = getPanel();
    static JCheckBox useGrid = new JCheckBox("Raster anzeigen");
    static final int width = 800;
    static final int height = 800;
    //Map für eigene Templates
    private static Map<String, ArrayList<Moebel>> templateList = new HashMap<>();
    private ArrayList<ArrayList> editList = new ArrayList<>();
    private JTextField field = new JTextField();

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
            canvas.setList(editList.get(0));
            canvas.setShapeList(editList.get(1));
            editList.clear();
            panel.setVisible(false);
        });
        panel.add(field);
        panel.add(button);
        panel.setVisible(false);
        add(panel, BorderLayout.SOUTH);
        setResizable(false);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    private JPanel getPanel() {
        JPanel pan = new JPanel(new GridBagLayout());
        ArrayList<JButton> buttons = new ArrayList<>(Arrays.asList(new JButton("Badewanne"), new JButton("Doppelbett"),
                new JButton("Schrank"), new JButton("Schrankwand"), new JButton("Stuhl"),
                new JButton("Tisch"), new JButton("Waschmaschine"), new JButton("Reset"), new JButton("Neue Gruppe")));
        templateList.forEach((key, value) -> {
            JButton b = new JButton(key);
            b.addActionListener(e -> {
                //for (Moebel m: value) Canvas.list.add(m.clone());
            });
            //b.addActionListener(e -> Canvas.list.addAll((ArrayList<Moebel>)value.clone()));
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
            editList.add(new ArrayList<>(canvas.getList()));
            editList.add(new ArrayList<>(canvas.getShapeList()));
            canvas.reset();
            field.grabFocus();
            field.requestFocus();
            panel.setVisible(true);
        });
        buttons.forEach(e -> e.addActionListener(x -> canvas.updateView()));
        return pan;
    }
}
