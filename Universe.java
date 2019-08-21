import java.io.*;
import java.util.Random;

//в объекте класса будем хранить состояние матрицы
public class Universe implements Serializable{
    public transient boolean debug = false;
    public final int N;
    public final long S;
    private boolean[][] m;
    private int generation;
    private int alivesCnt;

    public void calcAlivesCnt(){
        alivesCnt = 0;
        for (boolean[] booleans : m) {
            for (boolean b : booleans) {
                if (b) alivesCnt++;
            }
        }
    }

    public int getGeneration() {
        return generation;
    }

    public int getAlivesCnt() {
        return alivesCnt;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public void incGeneration(){
        this.generation++;
    }

    public boolean[][] getM() {
        return m;
    }

    public void setM(boolean[][] m) {
        this.m = m;
    }

    public Universe(int n, long s) {
        N = n;
        S = s;
        m = new boolean[N][N];
        Random random = new Random(S);
        alivesCnt = 0;
        generation = 1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                m[i][j] = random.nextBoolean();
                if (m[i][j]) alivesCnt++;
            }
        }
    }

    private Universe(int n, long s, int generation, int alivesCnt) {
        N = n;
        S = s;
        this.generation = generation;
        this.alivesCnt = alivesCnt;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Generation #"+generation).append("\n");
        sb.append("Alive: "+alivesCnt).append("\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                sb.append(m[i][j]?'O':' ');
            }
            if (i<N-1) sb.append("\n");
        }
        return sb.toString();
    }
    public static class Pair<T,U>{
        public T x; //столбцы
        public U y; //строки

        public Pair(T t, U u) {
            this.x = t;
            this.y = u;
        }

        @Override
        public String toString() {
            if (x instanceof String && y instanceof String)
                return x+"\n"+y;
            else
            return "{x=" + x + ";y=" + y + '}';
        }
    }
    public Pair<Integer,Integer>[] getNeigbours(Pair<Integer,Integer> coords){
        Pair[] result = new Pair[8];
        //переворачиваем координаты для удобства x - столбцы,y - строки
        int x= coords.x-1;
        //int y= coords.y;
        int ind=0;
        if (x<0) x=N-1;
        int ix = 1;
        while (ix++<=3){
            int y = coords.y-1;
            if (y < 0) y=N-1;
            int jy = 1;
            while (jy++<=3){
                if (!(x==coords.x && y == coords.y)) result[ind++] = new Pair(x,y);
                if (++y>N-1) y=0;
            }
            if (++x > N-1) x=0;
        }
        return result;
    }

    public void printNeigbours(Pair<Integer,Integer> coords){
        System.out.println(coords);
        for (Pair<Integer, Integer> pair : getNeigbours(coords)) {
            System.out.print(pair);
            System.out.print(";");
        }
        System.out.println();
    }

    //возвращаем кол-во живых для клетки с координатой coords,
    //количество мертвых будет 8-getCntOfLifes
    public int getCntOfLifes(Pair<Integer,Integer> coords){
        Pair[] neigbours = getNeigbours(coords);
        int life=0;
        for (Pair pair : neigbours) {
            if (pair != null)
                if (m[(int)pair.y][(int)pair.x]) life++;
        }
        return life;
    }

    public void saveToFile(String dirPath) throws IOException {
        //имя файла формируем автоматом
        File file = new File(dirPath+"\\"+N+"_"+generation+"_"+alivesCnt+".gol");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            System.out.println("Cannot create the file: " + file.getPath());
            throw e;
        }
    }

    public static Universe loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        Universe result;
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Universe u = (Universe) objectInputStream.readObject();
        result = new Universe(u.N,u.S,u.generation,u.alivesCnt);
        //result.m = new boolean[u.N][u.N];
        result.setM(u.m);
        objectInputStream.close();
        fileInputStream.close();
        return result;
    }

    public Universe(int n) {
        N = n;
        S = 0;
        this.m =  new boolean[n][n];
    }
}
