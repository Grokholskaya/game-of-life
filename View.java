import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    JLabel lblGen;
    JLabel lblAlive;
    DrawPanel drawPanel;
    Generation generation = null;

    public View(/*Generation generation*/) throws HeadlessException {
        super("Game of Life");
        //this.generation = generation;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        lblGen= new JLabel();
        add(lblGen);
        lblAlive= new JLabel();
        add(lblAlive);

        drawPanel = new DrawPanel((generation==null)?0:generation.getUniverseSize());
        //drawPanel.setSize(350,350);
        add(drawPanel);
        setLayout (new BoxLayout (getContentPane(), BoxLayout.Y_AXIS));
        setVisible(true);
    }

    public void setGeneration(Generation generation) {
        this.generation = generation;
        drawPanel.initPane(generation.getUniverseSize());
    }

    public void setLabelGen(String text){
        this.lblGen.setText("Generation #"+text);
    }
    public void setLabelAlive(String text){
        this.lblAlive.setText("Alive: "+text);
    }

    public class DrawPanel extends JPanel{
        private int N;
        //координаты
        private int[] xCoords;
        private int[] yCoords;

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
            if (N!=0) {
            xCoords = new int[N+1];
            yCoords = new int[N+1];
        }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.black);
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
            if (N != 0) {
                int cellWidth = Math.round((width * 1f) / N);
                int cellHeight = Math.round((height * 1f) / N);
            xCoords[0]=deltaX+1;
            yCoords[0]=deltaY+1;
                for (int i = 1; i < N; i++) {
                g.setColor(Color.black);
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
            g.setColor(Color.black);
            g.fillRect(xCoords[x],yCoords[y],xCoords[x+1]-xCoords[x],yCoords[y+1]-yCoords[y]);
        }

    }
}
