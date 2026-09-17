import java.util.*;
import java.util.Iterator;
import java.util.Set;
import java.util.LinkedHashMap;



public class classType {
    public String name, parent;    //name of class and parent class
    public boolean hasparent = false;
    public LinkedHashMap<String, String> variables =  new LinkedHashMap<>(); // variables (name,type)
    public LinkedHashMap<String, methodType> methods = new LinkedHashMap<>();  // method (name, methodtype)
    
    public void put_parent (String parent) { 
        this.parent= parent;
        this.hasparent = true ;
    }

    public void put_name(String name){
        this.name = name ;
    }

    public void put_variables(String name, String type)throws Exception{
        //check if the same variable already exist in class
        if (has_variables(name)) throw new Exception(name + " variable already exists");
        variables.put(name, type);
    }

    public void put_methods(String name, methodType type)throws Exception{
        //check if there is already a method with the same name in class
        if (has_methods(name)) throw new Exception(name + " method already exists at class " + this.name);        
        methods.put(name, type);
    }

    public boolean has_variables(String name){
        return variables.containsKey(name);
    }

    public boolean has_methods(String name){
        return methods.containsKey(name);
    }
    
    public String get_variables(String name){
        return variables.get(name);
    }

    public methodType get_methods(String name){
        return methods.get(name);
    }

    //check if it's override with the same return type and parametrs
    public boolean same_method(methodType m1, methodType m2){
        //check returntype and number of parametrs
        if (!(m1.type.equals(m2.type)) || m1.parametrs.size()!=m2.parametrs.size()) return false; 
        //check one by one the parametrs type
        Iterator<Map.Entry<String,String>> it2 = m2.parametrs.entrySet().iterator();
        Iterator<Map.Entry<String,String>> it1 = m1.parametrs.entrySet().iterator();
        while (it1.hasNext() && it2.hasNext()){
            if (!(it1.next().getValue().equals(it2.next().getValue()))) return false;
        }
        return true;
    }

}
