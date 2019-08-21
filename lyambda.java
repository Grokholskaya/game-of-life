import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class lyambda {
    final static String prefix="prefix";
    final static String suffix="suffix";
    public static void main(String[] args) {
        Function<String,String> closure = s -> prefix+s.trim()+suffix;
        Function<List<String>, List<String>> uniqueString = s ->{
            HashSet<String> set = new HashSet<>();
            for (int i = 0; i < s.size(); i++) {
                set.add(s.get(i));
            }
            List<String> result = new LinkedList<>();
            Iterator<String> iterator = set.iterator();
            int i = 0;
            while (iterator.hasNext()){
                result.add(iterator.next());
            }
            return result;
        };
        List<String> s = new LinkedList<>(Arrays.asList("java", "scala", "java", "clojure", "clojure"));;
        acceptFunctionalInterface(uniqueString, s);

        //Callable<String> callable = () -> {Thread.sleep(100);return "hello";};

    }
    public static void acceptFunctionalInterface(Function<List<String>, List<String>> f, List<String> s) {
        System.out.println(f.apply(s));
    }
}
