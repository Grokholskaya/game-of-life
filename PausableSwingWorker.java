import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PausableSwingWorker<K, V> extends SwingWorker<Void, Third<String,String,boolean[][]>> {
    private static int initSize = 40;
    private volatile boolean isPaused;
    private volatile int sleepTime;

    private Main frame;
    private Generation g;
    private Universe u;

    public static int getInitSize() {
        return initSize;
    }

    public static void setInitSize(int initSize) {
        PausableSwingWorker.initSize = initSize;
    }

    public PausableSwingWorker(int sleepTime, Main frame, String fileName) throws IOException, ClassNotFoundException {
        this.sleepTime = sleepTime;
        this.frame = frame;
        init(initSize, fileName);
    }

    public void setSleepTime(int sleepTime) {
        //время задержки между шагами обновления генерации
        this.sleepTime = sleepTime;
    }

    public void init(int n, String fileName) throws IOException, ClassNotFoundException {
        if (fileName == null) {
            long s = 0;
            s = System.currentTimeMillis();
            //n = 40;
            //m = 15;
            u = new Universe(n, s);//model
        } else {
            u = Universe.loadFromFile(fileName);
        }
        g = new Generation(u);//controller
        frame.setGeneration(g);
        g.setV(frame);
        frame.setLabelAlive(Integer.valueOf(u.getAlivesCnt()).toString());
        frame.setLabelGen(Integer.valueOf(u.getGeneration()).toString());
    }

    public final void pause() {
        if (!isPaused() && !isDone()) {
            isPaused = true;
            //firePropertyChange("paused", false, true);
        }
    }

    public final void resume() {
        if (isPaused() && !isDone()) {
            isPaused = false;
            //firePropertyChange("paused", true, false);
        }
    }

    public final boolean isPaused() {
        return isPaused;
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (!isCancelled()) {
            if (!isPaused()) {
                g.makeStepOfEvolution();
                publish(new Third<>(Integer.valueOf(u.getGeneration()).toString(),
                        Integer.valueOf(u.getAlivesCnt()).toString(),
                        copyArray(u.getM())
                        )
                );
                Thread.sleep(sleepTime);
            } else {
                Thread.sleep(200);
            }
        }
        return null;
    }

    @Override
    protected void process(List<Third<String, String, boolean[][]>> chunks) {
        //super.process(chunks);
        frame.setLabelGen(chunks.get(chunks.size()-1).x);
        frame.setLabelAlive(chunks.get(chunks.size()-1).y);
        g.setWorkerUniverseData(chunks.get(chunks.size()-1).z);
        frame.drawPanel.repaint();
    }

    public boolean[][] copyArray(boolean[][] m){
        boolean[][] copyM = new boolean[m.length][m[0].length];
        for (int i = 0; i < m.length; i++) {
            copyM[i] = Arrays.copyOf(m[i],m[i].length);
        }
        return copyM;
    }
}

class Third<T,U,Z>{
    public T x;
    public U y;
    public Z z;

    public Third(T t, U u, Z z) {
        this.x = t;
        this.y = u;
        this.z = z;
    }
}
