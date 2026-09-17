import java.util.*;

public class SymbolTable {
    
    public LinkedHashMap<String, classType> classes = new LinkedHashMap<>();
    
    public void put_classes(String name, classType cl) throws Exception	{
        //check if there is already a class with the same name in ST
        if (has_classes(name)) throw new Exception(name + " class already exists");
        classes.put(name, cl) ;
    }

    public boolean has_classes(String name){
        return classes.containsKey(name);
    }
    
    public classType get_classes(String name){
        return classes.get(name);
    }

    // print offset of variables and methods at each class
    public void print_offset(String main){
        int s = classes.size();
        String [] classname= new String[s];
        int [] varoff = new int[s];
        int [] maroff = new int[s];
        int i = 0;

        Iterator<Map.Entry<String,classType>> it = classes.entrySet().iterator();
        while (it.hasNext()){
            String name=it.next().getKey();
            if (name.equals(main)) continue;
            classType cl = get_classes(name);
            System.out.println("\n\n----------class " + name + "--------");
            int v_offset=0;
            int m_offset=0;
            classname[i] = name;
            if (cl.hasparent){
                for(int j=0; j<i; j++){
                    if (classname[j].equals(cl.parent)){
                        v_offset=varoff[j];
                        m_offset = maroff[j];
                        break;
                    }
                }
            }

            System.out.println("----Variables----");
            Iterator<Map.Entry<String,String>> it1 = cl.variables.entrySet().iterator();
            while (it1.hasNext()){
                Map.Entry<String,String> var = it1.next();
                String varname = var.getKey();
                String type = var.getValue();
                System.out.println(name + "." + varname  + " :" + v_offset);
                if (type.equals("int")) v_offset +=4;
                else if (type.equals("boolean")) v_offset ++;
                else { v_offset +=8;}
            }
            System.out.println("----Methods----");
            Iterator<Map.Entry<String,methodType>> it2 = cl.methods.entrySet().iterator();
            while (it2.hasNext()){
                Map.Entry<String,methodType> m  = it2.next();
                String method_name = m.getKey();
                boolean found = false;
                classType par = cl;
                while (par.hasparent) {
                    par = get_classes(par.parent);
                    if (par.has_methods(method_name)) {
                        found = true;
                        break;
                    }
                }
                if (found) continue;
                else{
                    System.out.println(name + "." + method_name + " :" + m_offset);
                    m_offset +=8;
                }
            }
            classname[i]= name;
            varoff[i] = v_offset;
            maroff[i] = m_offset;
            i++;
        }
    }
}
