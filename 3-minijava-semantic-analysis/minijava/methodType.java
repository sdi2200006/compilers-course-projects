import java.util.*;

public class methodType {
    public String name, type;    //name, return type
    public LinkedHashMap<String, String> parametrs =  new LinkedHashMap<>(); // parametrs
    public LinkedHashMap<String, String> lvariables = new LinkedHashMap<>();  //local variables

    public void put_name(String name){
        this.name = name ;
    }
    public void put_type(String type){
        this.type = type ;
    }

    public void put_parametr(String name, String type) throws Exception{
        //check if parametr already exist in method
        if (has_parametrs(name)) throw new Exception(name + " parametr already exists");
        parametrs.put(name, type);
    }

    public void put_lvariables(String name, String type) throws Exception{
        //check if variable already exist as local or as parametr
        if (has_lvariables(name) || has_parametrs(name)) throw new Exception(name + " local variable already exists");
        lvariables.put(name, type);
    }

    public boolean has_lvariables(String name){
        return lvariables.containsKey(name);
    }

    public boolean has_parametrs(String name){
        return parametrs.containsKey(name);
    }
    
    public String get_lvariables(String name){
        return lvariables.get(name);
    }

    public String get_parametrs(String name){
        return parametrs.get(name);
    }
    
}
