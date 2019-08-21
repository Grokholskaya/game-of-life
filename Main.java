import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main extends JFrame implements ActionListener {
    public final JLabel GenerationLabel,AliveLabel,colorLabel;
    public final JToggleButton btnToggle;
    public final JButton btnStart, btnSave, btnLoad;
    public final JSlider sb;
    private String fileName;
    DrawPanel drawPanel;
    Generation generation = null;

    public void setColor(Color color){
        drawPanel.setClr(color);
    }

    private PausableSwingWorker currentWorker = null;

    private JButton makeButton(JPanel addToPanel, String caption, String action) {
        JButton b = new JButton(caption);
        b.setSize(50,30);
        b.setActionCommand(action);
        b.addActionListener(this);
        addToPanel.add(b);
        return b;
    }

    public Main(/*Generation generation*/) throws HeadlessException {
        super("Game of Life");
        //this.generation = generation;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        //setLayout (new BoxLayout (getContentPane(), BoxLayout.X_AXIS));
        setLayout (new BorderLayout());

        JPanel panel = new JPanel();
        BoxLayout box = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(box);
        //panel.setBorder(new TitledBorder("Generation:"));

        JPanel btnPanel = new JPanel();
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,60));
        btnPanel.setLayout(new GridLayout(2,2));

        btnStart = makeButton(btnPanel,"Start","Start");
        btnStart.setToolTipText("Start new life");
        btnStart.setName("ResetButton");

        //btnToggle= makeButton(btnPanel,"Pause","Toggle");
        btnToggle = new JToggleButton("Pause");
        btnToggle.setSize(50,30);
        btnToggle.setActionCommand("Toggle");
        btnToggle.addActionListener(this);
        btnPanel.add(btnToggle);
        btnToggle.setName("PlayToggleButton");

        btnToggle.setEnabled(false);
        btnToggle.setToolTipText("Pause/resume life generation");
        btnSave = makeButton(btnPanel,"Save","Save");
        btnSave.setEnabled(false);
        btnLoad = makeButton(btnPanel,"Load","Load");

        panel.add(btnPanel/*,BorderLayout.NORTH*/);

        GenerationLabel= new JLabel();
        GenerationLabel.setName("GenerationLabel");
        setLabelGen("0");
        GenerationLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(GenerationLabel/*,BorderLayout.CENTER*/);

        AliveLabel= new JLabel();
        AliveLabel.setName("AliveLabel");
        setLabelAlive("0");
        AliveLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(AliveLabel/*,BorderLayout.SOUTH*/);

        add(panel, BorderLayout.WEST);

        sb = new JSlider(200,1000);

        sb.setBorder(new TitledBorder("Generation speed:"));
        sb.setPaintTicks(true);
        sb.setMajorTickSpacing(100);
        sb.setMinorTickSpacing(50);
        sb.setSnapToTicks(true);
        sb.setValue(400);
        //sb.setLabelTable(labels);
        sb.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = ((JSlider)e.getSource()).getValue();
                if (currentWorker != null) currentWorker.setSleepTime(value);
            }
        });
        sb.setPaintLabels(true);
        sb.setLabelTable(sb.createStandardLabels(200));
        panel.add(sb);

        colorLabel = new JLabel();
        colorLabel.setText("current color");
        colorLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        //colorLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

        panel.add(colorLabel);
        JButton btnColor = makeButton(panel,"Set color","Color");
        btnColor.setToolTipText("Set color of universe");
        btnColor.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton btnSize = makeButton(panel,"Set size","Size");
        btnSize.setToolTipText("Set size of universe");
        btnSize.setAlignmentX(Component.RIGHT_ALIGNMENT);


        drawPanel = new DrawPanel((generation==null)?0:generation.getUniverseSize());
        //drawPanel.setSize(350,350);
        add(drawPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
                              @Override
                              public void windowClosing(WindowEvent e) {
                                  if (currentWorker != null && !currentWorker.isDone()) {
                                      currentWorker.cancel(true);
                                  }
                                  e.getWindow().dispose();
                              }
                          });
        setVisible(true);
    }

    public void setGeneration(Generation generation) {
        this.generation = generation;
        drawPanel.initPane(generation.getUniverseSize());
    }

    public void setLabelGen(String text){
        this.GenerationLabel.setText("Generation #"+text);
    }
    public void setLabelAlive(String text){
        this.AliveLabel.setText("Alive: "+text);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Size" == e.getActionCommand()) {
            int oldIntValue, newIntValue;
            if (generation != null){
                oldIntValue =  this.generation.getUniverseSize();
            } else{
                oldIntValue = PausableSwingWorker.getInitSize();
            }

            String inputValue = JOptionPane.showInputDialog(null, "Enter the size of a universe", oldIntValue);
            try {
                newIntValue = Integer.parseInt(inputValue);
                if (oldIntValue != newIntValue){
                    PausableSwingWorker.setInitSize(newIntValue);
                    //создать вселенную с новым размером
                    btnStart.setEnabled(true);
                    btnStart.doClick();
                }
            } catch (Exception e1){
                JOptionPane.showMessageDialog(null,"Incorrect number","Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("Start" == e.getActionCommand()) {
            JOptionPane.showMessageDialog(null,
                    fileName==null?"Starting new universe":"Loading universe",
                    "Info", JOptionPane.INFORMATION_MESSAGE
            );

            if (currentWorker != null && currentWorker.getState() == SwingWorker.StateValue.STARTED){
                currentWorker.cancel(true);
            }
            try {
                currentWorker = new PausableSwingWorker(sb.getValue(),this, fileName);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, e1.getMessage(), "Error loading data", JOptionPane.ERROR_MESSAGE);
                currentWorker = null;
                //e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                JOptionPane.showMessageDialog(null, e1.getMessage(), "Error loading data", JOptionPane.ERROR_MESSAGE);
                //e1.printStackTrace();
                currentWorker = null;
            }
            if (currentWorker != null) {
                currentWorker.execute();
                btnToggle.setText("Pause");
                btnStart.setEnabled(false);
                btnToggle.setEnabled(true);
                btnSave.setEnabled(false);
                btnLoad.setEnabled(false);
            }
        } else if ("Toggle" == e.getActionCommand()) {
            if (currentWorker.isPaused()){
                //возобновляем работу
                btnStart.setEnabled(false);
                btnToggle.setText("Pause");
                btnLoad.setEnabled(false);
                btnSave.setEnabled(false);
                currentWorker.resume();
            } else {
                //пауза
                btnToggle.setText("Resume");
                btnStart.setEnabled(true);
                btnLoad.setEnabled(true);
                btnSave.setEnabled(true);
                currentWorker.pause();
            }
        } else if ("Stop" == e.getActionCommand()) {
            btnStart.setEnabled(true);
            btnToggle.setEnabled(false);
            currentWorker.cancel(true);
            currentWorker = null;
        } else if ("Color" == e.getActionCommand()){
            Color color = JColorChooser.showDialog(null,
                    "Set color for universe",
                    drawPanel.getColor());
            if (color != null) {
                setColor(color);
                ((JButton)e.getSource()).setForeground(color);
                colorLabel.setForeground(color);
                repaint();
            }
        } else if ("Save" == e.getActionCommand()){
            if (generation != null){
                JFileChooser fileSave = new JFileChooser();
                fileSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int ret = fileSave.showDialog(null, "Сохранить файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    fileName = fileSave.getSelectedFile().getAbsolutePath();
                    try {
                        generation.saveDataToFile(fileName);
                        JOptionPane.showMessageDialog(null, "Data is saved successfully!", "Information", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Error saving data", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    fileName = null;
                }
//              fileName = "C:\\Users\\Elmirka\\IdeaProjects\\GameOfLife\\universe.txt";
            } else
                JOptionPane.showMessageDialog(null, "Nothing to save!", "Information", JOptionPane.INFORMATION_MESSAGE);
        } else if ("Load" == e.getActionCommand()){
//            fileName = "C:\\Users\\Elmirka\\IdeaProjects\\GameOfLife\\universe.txt";
            JFileChooser fileSave = new JFileChooser();
            FileFilter filter=new FileNameExtensionFilter("data files", "gol","txt");
            fileSave.setFileFilter(filter);
//            fileSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = fileSave.showDialog(null, "Открыть файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                fileName = fileSave.getSelectedFile().getAbsolutePath();
                btnStart.doClick();
                fileName = null;
            } else {
                fileName = null;
            }

        }
    }

    //очищаем консоль вывода
    public static void clearConsoleOutput(){
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        }
        catch (IOException | InterruptedException e) {}
    }

    public class DrawPanel extends JPanel{
        private volatile Color clr;
        private int N;
        //координаты
        private int[] xCoords;
        private int[] yCoords;

        public void setClr(Color color) {
            this.clr = color;
        }

        public Color getColor() {
            return clr;
        }

        public void initPane(int N){
            this.N = N;
            if (N!=0) {
                xCoords = new int[N + 1];
                yCoords = new int[N + 1];
            }
            this.repaint();
        }

        public DrawPanel(int N) {
            this.N = N;
            clr = Color.blue;
            if (N!=0) {
                xCoords = new int[N+1];
                yCoords = new int[N+1];
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(clr);
            g.drawRect( 5, 5,this.getWidth()-10,this.getHeight()-10);
            if (generation != null) {
                drawGrid(g,5,5,this.getWidth()-10,this.getHeight()-10);
                boolean[][] m = generation.getM(true);
                for (int i = 0; i < m.length; i++) {
                    for (int j = 0; j < m[i].length; j++) {
                        if (m[i][j]) fillRect(j,i,g);
                    }
                }
            }
            //fillRect(N-1,5,g);
            //fillRect(N-1,N-1,g);
        }
        private void drawGrid(Graphics g, int deltaX, int deltaY, int width, int height){
            //int n = generation.getUniverseSize();
            g.setColor(clr);
            if (N != 0) {
                int cellWidth = Math.round((width * 1f) / N);
                int cellHeight = Math.round((height * 1f) / N);
                xCoords[0]=deltaX+1;
                yCoords[0]=deltaY+1;
                for (int i = 1; i < N; i++) {
                    //g.setColor(Color.black);
                    xCoords[i]=deltaX+(cellWidth*i)+1;
                    yCoords[i]=deltaY+(cellHeight*i)+1;
                    g.drawLine(deltaX+(cellWidth*i),deltaY,deltaX+(cellWidth*i),deltaY+height);
                    g.drawLine(deltaX,deltaY+(cellHeight*i),deltaX+width,deltaY+(cellHeight*i));
                }
                xCoords[N]=deltaX+width+1;
                yCoords[N]=deltaY+height+1;
            }
        }
        private void fillRect(int x, int y, Graphics g){
            g.setColor(clr);
            g.fillRect(xCoords[x],yCoords[y],xCoords[x+1]-xCoords[x],yCoords[y+1]-yCoords[y]);
        }

    }
}
