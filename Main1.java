import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Main1 {
    private static int N;
    private static int S;

    public static void main(String[] args) throws IOException {

        for (Universe.Pair<String, String> stringPair : Test.generate()) {
            System.out.println(stringPair);
        }
        /*Random random1 = new Random(4);
        for (int i = 0; i < 16; i++) {
            System.out.println(random1.nextBoolean());
        }*/
        if (true) return;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int k=0;
        for (String s : (reader.readLine().split("\\s"))) {
            if (k++==0) N = Integer.parseInt(s);
            else S = Integer.parseInt(s);
        }
        char[][] m = new char[N][N];
        Random random = new Random(S);
        boolean b;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                b= random.nextBoolean();
                m[i][j] = b?'O':' ';
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(m[i][j]);
            }
            System.out.println();
        }
    }
}