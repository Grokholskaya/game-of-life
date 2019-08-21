import java.io.IOException;
import java.util.Arrays;

public class Generation {
    private Main v;
    private Universe u;
    private volatile Universe workerUniverse;
    //private int step;

    public void saveDataToFile(String filePath) throws IOException {
        u.saveToFile(filePath);
    }

    public void setV(Main v) {
        this.v = v;
    }
    public boolean[][] getM(boolean fromWorker){
        if (!fromWorker)
            return u.getM();
        else
            return workerUniverse.getM();
    }

    public int getUniverseSize(){
        return u.N;
    }
    public Generation(Universe u/*, int step*/) {
        this.u = u;
        workerUniverse = new Universe(u.N);
        //this.step = step;
    }

    public void setWorkerUniverseData(boolean[][] m){
        workerUniverse.setM(m);
    }

    //одна итерация эволюции
    //1.живая клетка выживает, если у нее есть [2,3] живых соседа
    //2.мертвая клетка возрождается, если у нее есть ровно 3 живых соседа
    public void makeStepOfEvolution(){
        boolean[][] nextM = new boolean[u.N][u.N];
        for (int i = 0; i < u.getM().length; i++) {
            nextM[i] = Arrays.copyOf(u.getM()[i],u.N);
        }

        int cntLifes;
        for (int i = 0; i < u.getM().length; i++) {
            for (int j = 0; j < u.getM()[i].length; j++) {
                cntLifes = u.getCntOfLifes(new Universe.Pair(j,i));
                if (u.getM()[i][j]){
                    //живой
                    if (cntLifes == 2 || cntLifes == 3)
                        nextM[i][j]=true;
                    else nextM[i][j]=false;
                } else {
                    //мертвый
                    if (cntLifes==3)
                        nextM[i][j]=true;
                    else nextM[i][j]=false;
                }
            }
        }
        u.setM(nextM);
        u.incGeneration();
        u.calcAlivesCnt();
        //v.repaint();
        /*for (int i = 0; i < nextM.length; i++) {
            for (int j = 0; j < nextM.length; j++) {
                u.m[i][j] = nextM[i][j];
            }
        }*/
    }
}
