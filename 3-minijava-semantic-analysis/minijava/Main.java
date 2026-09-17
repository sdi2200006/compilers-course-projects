import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import syntaxtree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length == 0){
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }
        for (int i = 0; i < args.length; i++){
            FileInputStream fis = null;
            try{
                fis = new FileInputStream(args[i]);
                System.out.println("\nINPUT " + args[i]);
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();
                System.err.println("Program parsed successfully.");
                SymbolTable st = new SymbolTable();
                MyVisitor eval = new MyVisitor(st);
                String r = root.accept(eval, null);
                System.out.println("Visitor 1 success.\n");
                //System.out.println(r);
                MyVisitor2 eval2 = new MyVisitor2(st);
                root.accept(eval2, null);   
                System.out.println("\nVisitor 2 success.\n");

            }
            catch(ParseException ex){
                System.out.println(ex.getMessage());
            }
            catch(FileNotFoundException ex){
                System.err.println(ex.getMessage());
            }
            finally{
                try{
                    if(fis != null) fis.close();
                }
                catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }

        }
}