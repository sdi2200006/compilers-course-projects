import java_cup.runtime.*;
import java.io.*;

class Main2 {
    public static void main(String[] argv) throws Exception {
        Parser2 p = new Parser2(new Scanner(new InputStreamReader(System.in)));
        p.parse();
    }
}