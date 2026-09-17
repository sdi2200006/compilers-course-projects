import syntaxtree.*;
import visitor.*;
import java.util.*;


class MyVisitor2 extends GJDepthFirst<String, Void>{
    public  SymbolTable st;
    public  String temp_class;
    public  String temp_method;
    public String main_class;
    public  boolean current ; //1->class 0->method  (used at varDeclaration)
    public int use_var = 0; // i can know when i need type (1), id (0), method (2)
    public methodType m_g;
    public int param;

    public MyVisitor2(SymbolTable s){
        this.st = s;
    }

    /**
     * f0 -> Goal	::=	MainClass
     * f1 -> ( TypeDeclaration )* 
     * f3 -> <EOF>
     */
    @Override
    public String visit(Goal n, Void argu) throws Exception {
        n.f0.accept(this,argu);
        for (Node node: n.f1.nodes) {
            node.accept(this, argu);
        }
        //print offset at the end
        st.print_offset(main_class);
        return "";
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        use_var = 0;
        String classname = n.f1.accept(this, null);
        use_var = 0;
        String arg = n.f11.accept(this, argu);
        main_class = temp_class = classname;
        temp_method = "main";
        current = false;  //method
        String var = "";
        for ( Node node: n.f14.nodes) {
            var +=  node.accept(this, argu);
        }
        String statement = "";
        for ( Node node: n.f15.nodes) {
            statement +=  node.accept(this, argu);
        }
        return " class "+ classname + "{\n public static void main (String[] "+ arg  + "){\n" + var + " "+ statement  + "\n}\n }\n ";
    }


    /**
     * f0 -> ClassDeclaration|	ClassExtendsDeclaration
     */
    @Override
    public String visit(TypeDeclaration n, Void argu) throws Exception {
        use_var = 0;
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n, Void argu) throws Exception {
        use_var = 0;
        n.f0.accept(this, argu);
        use_var = 0;
        String classname = n.f1.accept(this, argu);
        temp_class = classname;
        current = true;   // class

        String var = "";
        for ( Node node: n.f3.nodes) {
            var +=  node.accept(this, argu);
        }
        String method = "";
        for ( Node node: n.f4.nodes) {
            method +=  node.accept(this, argu);
        }
        return " class " + classname + " {\n" + var + " " + method + "\n} \n";
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        use_var = 0;
        n.f0.accept(this, argu);
        use_var = 0;
        String classname = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        use_var = 0;
        String parentclass = n.f3.accept(this, argu);
        use_var = 0;
        n.f4.accept(this, argu);
        
        temp_class = classname;
        current = true;  //class

        String var = "";
        for ( Node node: n.f5.nodes) {
            var +=  node.accept(this, argu);
        }
        String method = "";
        for ( Node node: n.f6.nodes) {
            method +=  node.accept(this, argu);
        }
        use_var = 0;
        n.f7.accept(this, argu);

        return " class " + classname + " extends " + parentclass + " {\n" +var + " " +   method + "\n} \n";
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
   public String visit(VarDeclaration n, Void argu) throws Exception {
        String type = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        //check now the type because we have all the classes in ST
        if (!type.equals("int") && !type.equals("int[]") && !type.equals("boolean") && !type.equals("boolean[]") && !st.has_classes(type)){
            throw new Exception("VarDeclaration: " + type + " is not an acceptable type or class name");
        }
        return  " "  ;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n, Void argu) throws Exception {
        use_var = 0;
        n.f0.accept(this, argu);
        use_var = 0;
        String type = n.f1.accept(this, argu);
        use_var = 0;
        String id = n.f2.accept(this, argu);
        use_var = 0;
        n.f3.accept(this, argu);

        temp_method = id;
        current = false;  // method
        
        String arg = n.f4.present() ? n.f4.accept(this, argu) : "";
        use_var = 0;
        n.f5.accept(this, argu);
        use_var = 0;
        n.f6.accept(this, argu);
        String var = "";
        for ( Node node: n.f7.nodes) {
            var +=  node.accept(this, argu);
        }
        //use_var = true;
        String statement = "";
        for ( Node node: n.f8.nodes) {
            statement +=  node.accept(this, argu);
        }
        use_var = 0;
        n.f9.accept(this, argu);
        //checks return type
        use_var = 1;
        String exp = n.f10.accept(this,argu);
        //System.out.println("MethodDeclaration at " "": return method " + temp_method + " from class " + temp_class + " wants "  + type + " not " + exp);
        exp = exp.replace("()", ""); // if return type was new class(), returns class() and we dont need () so we replase them
        //check that the method return a class and return type is a class 
        if (st.has_classes((type)) && st.has_classes(exp)){
            String ch = exp;
            classType c = st.get_classes(ch);
            //checks if it is its parent class
            while(c.hasparent){
                ch = c.parent;
                if (ch.equals(type)){
                    return type;
                }
                c = st.get_classes(ch);
            }
        }
        // if it was different classes without a connection parent-child or different types then throw exception
        if (!(exp.equals(type))) throw new Exception("MethodDeclaration at "+ temp_method +"() : Wrong return method " + temp_method +" from class " + temp_class + " wants "  + type + " not " + exp);
        n.f11.accept(this,argu);
        n.f12.accept(this,argu);
        return " public " + type +  " " + id + "("+ arg +"){\n" + var +  " " + statement + " return " + exp + ";\n} \n ";
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, Void argu) throws Exception {
        String ret = n.f0.accept(this, argu);

        if (n.f1 != null) {
            ret += n.f1.accept(this, argu);
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, Void argu) throws Exception{
        String type = n.f0.accept(this, argu);
        String name = n.f1.accept(this, argu);
        //check now the type because we have all the classes in ST
        if (!type.equals("int") && !type.equals("int[]") && !type.equals("boolean") && !type.equals("boolean[]") && !st.has_classes(type)){
            throw new Exception("FormalParameter: " + type + " is not an acceptable type or class name");
        }
        return type + " " + name ;
    }

    /**
     * f0 -> ( FormalParameterTerm )*
     */
    @Override
    public String visit(FormalParameterTail n, Void argu) throws Exception {
        String ret = " ";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, argu);
        }
        return ret;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public String visit(FormalParameterTerm n, Void argu) throws Exception {
        return "," + n.f1.accept(this, argu);
    }


    @Override
    public String visit(Type n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /*ArrayType	::=	BooleanArrayType|integerArrayType */
    @Override
    public String visit(ArrayType n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(BooleanArrayType n, Void argu) {
        return "boolean[]";
    }

    @Override
    public String visit(IntegerArrayType	 n, Void argu) {
        return "int[]";
    }

    public String visit(BooleanType n, Void argu) {
        return "boolean";
    }

    public String visit(IntegerType n, Void argu) {
        return "int";
    }

    @Override
    public String visit(Statement n, Void argu) throws Exception {
        return n.f0.accept(this, argu); 
    } 

    /**
     * f0 -> "{" 
     * f1 -> ( Statement )*
     * f2 -> "}"
     */
    @Override
    public String visit(Block n, Void argu) throws Exception {
        String ret = "";
        for (Node node : n.f1.nodes) {
           ret += node.accept(this, argu);
        }
        return " { \n" + ret + " }\n";
    } 

    /**
     * f0 -> Identifier 
     * f1 -> "="
     * f2 -> Expression
     * f3 -> ";"
     */
    @Override
    public String visit(AssignmentStatement n, Void argu) throws Exception {
        // id
        use_var = 0;
        String i1 = n.f0.accept(this, argu);
        // type of id
        use_var = 1;
        String i = find_type(i1);  // it throws exception if it isn't defined 
        n.f1.accept(this, argu);
        //type of expr
        String i2 = n.f2.accept(this, argu);
        // if expresion was new class() which returns class(), replace () and make it class
        i2=i2.replace("()","" );
        n.f3.accept(this, argu);
        //if it is this then it needs the temp class
        if (i2.equals("this")) i2 = temp_class;
        //if its the same, ok
        if (i.equals(i2))  return i;
        //if both are classes then check for connection parent-child
        if (st.has_classes((i)) && st.has_classes(i2)){
            String ch = i2;
            classType c = st.get_classes(ch);
            while(c.hasparent){
                ch = c.parent;
                if (ch.equals(i)){
                    return i;
                }
                c = st.get_classes(ch);
            }
        }
        throw new Exception(" AssignmentStatement:different types " + i +"!=" + i2);
    } 

    /**
     * f0 -> Identifier 
     * f1 -> "["
     * f2 -> Expression
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression 
     * f6 ->";"
     */
    @Override
    public String visit(ArrayAssignmentStatement n, Void argu) throws Exception {
        //id
        use_var = 0;
        String i1 = n.f0.accept(this, argu);
        // type of id
        use_var = 1;
        String i = find_type(i1);
        //if id isn't a table
        if (!i.equals("int[]") && !i.equals("boolean[]")) throw new Exception("ArrayAssignmentStatement: Variable " + i1 + " is not a table[]. It is " + i);
        n.f1.accept(this, argu);
        //TYPOS OF EXPR1
        use_var = 1;
        String i2 = n.f2.accept(this, argu);
        //if expression between [] is not a int expression
        if (!i2.equals("int")) throw new Exception("ArrayAssignmentStatement: T[i], i must be of type int, not " + i2);

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        //TYPOS OF EXPR2
        use_var= 1;
        String i3 = n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        //if the type of table is different from the expression type
        if (i.equals("int[]") && !i3.equals("int")) throw new Exception("ArrayAssignmentStatement: " + i3 + " can't be elemest of to int[]");
        if (i.equals("boolean[]") && !i3.equals("boolean")) throw new Exception("ArrayAssignmentStatement: " + i3 + " can't be elemest of to boolean[]");
        return i3;

    } 

    /**
     * f0 -> "if" 
     * f1 -> "("
     * f2 -> Expression 
     * f3 -> ")" 
     * f4 -> Statement 
     * f5 -> "else" 
     * f6 -> Statement
     */
    @Override
    public String visit(IfStatement n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        use_var = 1; 
        String i1 = n.f2.accept(this, argu);
        //if expression isn't a boolean expression
        if (!( i1.equals("boolean"))) throw new Exception("IfStatement:expecting boolean expr, not " + i1);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return "";
    } 

    /**
     * f0 -> "while" 
     * f1 -> "(" 
     * f2 -> Expression 
     * f3 -> ")" 
     * f4 -> Statement
     */
    @Override
    public String visit(WhileStatement n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        use_var = 1; 
        String i1 = n.f2.accept(this, argu);
        //if expression isn't a boolean expression
        if (!( i1.equals("boolean"))) throw new Exception("WhileStatement:expecting boolean expr, not " + i1);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return "";
    } 

    /**
     * f0 -> "System.out.println" 
     * f1 -> "("
     * f2 -> Expression 
     * f3 -> ")" 
     * f4 -> ";"
     */
    public String visit(PrintStatement n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        use_var = 1;  // type
        String i= n.f2.accept(this, argu);
        //if it wants to print something with different type of int
        if (!i.equals("int")) {
            throw new Exception("PrintStatement:System.out.println wants int, not: " + i);
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return "";
    } 

    /**
     * f0 -> Clause
     * f1 -> "&&"
     * f2 -> Clause
     */
    @Override
    public String visit(AndExpression n, Void argu) throws Exception {
        use_var = 1;
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        use_var = 1;
        String i2 =n.f2.accept(this, argu);
        //if one or both clayse aren't boolean
        if (!i1.equals("boolean") || !i2.equals("boolean"))  throw new Exception("AndExpression:expecting boolean-boolean, not " + i1 + " and " + i2);
        return "boolean" ;
    } 

    /**
     * f0 ->PrimaryExpression 
     * f1 -> "<"
     * f2 -> PrimaryExpression
     */
    @Override
    public String visit(CompareExpression n, Void argu) throws Exception {
        use_var = 1;
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        use_var = 1;
        String i2 =n.f2.accept(this, argu);
        //if one or both PrimaryExpr aren't int
        if (!i1.equals("int") || !i2.equals("int")) throw new Exception("CompareExpression: Expected int<int , not "  + i1 + " and " + i2);
        return "boolean";
    } 

    /**
     * f0 ->PrimaryExpression 
     * f1 -> "+"
     * f2 -> PrimaryExpression
     */
    @Override
    public String visit(PlusExpression n, Void argu) throws Exception {
        use_var = 1;
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        use_var = 1;
        String i2 =n.f2.accept(this, argu);
        //if one or both PrimaryExpr aren't int
        if (!i1.equals("int") || !i2.equals("int")) throw new Exception("PlusExpression: Expected int+int, not "  + i1 + " and " + i2);
        return "int";
    } 

    /**
     * f0 ->PrimaryExpression 
     * f1 -> "-"
     * f2 -> PrimaryExpression
     */
    @Override
    public String visit(MinusExpression n, Void argu) throws Exception {
        use_var = 1;
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        use_var = 1;
        String i2 =n.f2.accept(this, argu);   
        //if one or both PrimaryExpr aren't int
        if (!i1.equals("int") || !i2.equals("int")) throw new Exception("MinusExpression:Expected int-int, not "  + i1 + " and " + i2);
        return "int";
    } 
    
    /**
     * f0 ->PrimaryExpression 
     * f1 -> "*"
     * f2 -> PrimaryExpression
     */
    @Override
    public String visit(TimesExpression n, Void argu) throws Exception {
        use_var = 1;
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        use_var = 1;
        String i2 =n.f2.accept(this, argu);
        //if one or both PrimaryExpr aren't int
        if (!i1.equals("int") || !i2.equals("int")) throw new Exception("TimesExpression: Expected int*int, not "  + i1 + " and " + i2);
        return "int";
    } 

    /**
     * f0 ->PrimaryExpression 
     * f1 -> "["
     * f2 -> PrimaryExpression
     * f3 -> "]" 
     */
    @Override
    public String visit(ArrayLookup n, Void argu) throws Exception {

        // id
        use_var = 0;
        String i1 = n.f0.accept(this, argu);
        // type of id
        use_var = 1;
        String i = find_type(i1);
        
        n.f1.accept(this, argu);
        use_var = 1;
        String i2 = n.f2.accept(this, argu);
        //if primary between [] is not a int 
        if (!(i2.equals("int"))) throw new Exception("ArrayLookup: expecting primary[int] not" + i2);
        n.f3.accept(this, argu);
        //if the type of primary expr is  a table
        if ((i.equals("int[]"))) return "int";
        if ((i.equals("boolean[]"))) return "boolean";
        //if the type of primary expr is not a table
        throw new  Exception("ArrayLookup: Expecting a table , not " + i  );
    } 

    /**
     * f0 -> PrimaryExpression 
     * f1 -> "." 
     * f2 -> "length" 
     */
    @Override
    public String visit(ArrayLength n, Void argu) throws Exception {
        // id
        use_var = 0;
        String e = n.f0.accept(this, argu);
        // type of id
        use_var = 1;
        String i = find_type(e);
        //check that primary expr is a table
        if (!(i.equals("boolean[]")) && !(i.equals("int[]")))  throw new Exception("ArrayLength: Expecting a table, not " + i); 
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        //length always returns int
        return "int";
    } 

    /**
     * f0 -> PrimaryExpression 
     * f1 -> "." 
     * f2 -> Identifier 
     * f3 ->"("
     * f4 -> ( ExpressionList )? 
     * f5 -> ")"
     */
      @Override
    public String visit(MessageSend n, Void argu) throws Exception {
        //name-> type -> checks type
        use_var = 0;
        classType c;
        String id = n.f0.accept(this, argu); // id 
        //checks if it is constructor  of a class
        if (id.endsWith("()")){
            //System.out.println("SURPRISE");
            id=id.replace("()","");
            if (!st.has_classes(id)) throw new Exception("MessageSend: class " + id + "isn't found");  //check that class exist
            c = st.get_classes(id);            
            n.f1.accept(this, argu);
        }
        //check if it is this 
        else if(id.equals("this")){
            c = st.get_classes(temp_class);
            n.f1.accept(this, argu);
            //name of method
            use_var = 0;
            String name = n.f2.accept(this, argu); // method name
            //check that the temp claass has a method with the same name
            if (!c.has_methods(name)) {
                throw new Exception ("MessageSend: class " + c + " this -> " + name + "doesn't exist" );
            }
            else{
                //if it has check the parametr
                param=0;
                //get the method
                methodType m1 = c.get_methods(name);
                //creat the temp method
                m_g = new methodType();
                m_g.put_name("given");
                n.f3.accept(this, argu);
                //if it has parametrs
                if (n.f4.present()) {
                    n.f4.accept(this, argu);
                }
                else {
                    //if it has not parametrs check that method has not too
                    if (!m1.parametrs.isEmpty()) throw new Exception("MessageSend: it must have parametrs at call of :" + name );
                    return m1.type;
                }
                n.f5.accept(this, argu);
                //if it had parametrs, check their type
                if (!check_parametrs(m_g,m1)) throw new Exception("MessageSend: False parametr types/num at :" + name );
                return m1.type;
            }
        }
        else {
            String cl;
            // if id is name of class
            if (st.has_classes(id)) {
                cl = id;
            } 
            //if it is name od variable
            else {
                use_var = 0;
                cl = find_type(id); 
            }
            //if it doesn;t have this class
            if (!st.has_classes(cl)) throw new Exception("MessageSend: class " + cl + " isn't found");
            c = st.get_classes(cl);
            n.f1.accept(this, argu);
        }
        //if it didn't go in the else if take the name of method
        use_var = 0;
        String name = n.f2.accept(this, argu); // method name
        //checks if there is the method in class
        if (!c.has_methods(name)) {
            // if not checks in parent classes
            classType parent = c;
            while (parent.hasparent) {
                parent = st.get_classes(parent.parent);
                if (parent.has_methods(name)) {
                    c = parent;
                    break;
                }
            }
            //if there isn't 
            if (!c.has_methods(name)) {
                throw new Exception("MessageSend: Method " + name + " not found in class " + c.name);
            }
        }
        //if you found it create the temp method
        param=0;
        methodType m1 = c.get_methods(name);
        m_g = new methodType();
        m_g.put_name("given");
        n.f3.accept(this, argu);
        //if it has parametrs
        if (n.f4.present()) {
            n.f4.accept(this, argu);
        }
        else {
            //check that m1 doesn't have parametrs too
            if (!m1.parametrs.isEmpty()) throw new Exception("MessageSend: it must have parametrs at call of :" + name );
            return m1.type;
        }
        n.f5.accept(this, argu);
        //check the type of paramets
        if (!check_parametrs(m_g,m1)) throw new Exception("MessageSend: False parametr types/num at :" + name );
        return m1.type;
    } 

     /**
     * f0 -> Expression 
     * f1 -> ExpressionTail
     */
    @Override
    public String visit(ExpressionList n, Void argu) throws Exception {
        use_var =  1;
        String i1 = n.f0.accept(this, argu);
        //if it was new class() -which returns class(), replase () and make it class
        i1=i1.replace("()", "");
        //if it was this then the type is the current class
        if (i1.equals("this")) i1 = temp_class;
        //put the parametrs in temp method which we will use to find if it was called with right parametrs
        m_g.parametrs.put(i1+(param++), i1);
        n.f1.accept(this, argu);
        return "";
    } 

    /**
     * f0 -> ( ExpressionTerm )*
     */
    @Override
    public String visit(ExpressionTail n, Void argu) throws Exception {
        for (Node node:n.f0.nodes){
            node.accept(this,argu);
        }
        return "";
    } 

    /**
     * f0 -> ","  
     * f1 -> Expression
     */
    @Override
    public String visit(ExpressionTerm n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        use_var =  1;
        String e = n.f1.accept(this, argu);
        //if it was new class() -which returns class(), replase () and make it class
        e=e.replace("()", "");
        //if it was this then the type is the current class
        if (e.equals("this")) e = temp_class;
        //put the parametrs in temp method which we will use to find if it was called with right parametrs
        m_g.parametrs.put(e+(param++), e);
        return " "  ;
    } 

    /**
     * f0 -> NotExpression|	PrimaryExpression
     */
    @Override
    public String visit(Clause n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    } 

    @Override
    public String visit(PrimaryExpression n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    public String visit(IntegerLiteral n, Void argu) {
        n.f0.toString();
        return "int";
    }

    @Override
    public String visit(TrueLiteral n, Void argu) {
        return "boolean";
    }
    
    @Override
    public String visit(FalseLiteral n, Void argu) {
        return "boolean";
    }

    @Override
    public String visit(Identifier n, Void argu) throws Exception{
        String s = n.f0.toString();
        if (use_var == 0 ) return s;   // epistrefei id string
        else if (use_var == 1 ) {
            String type = find_type(s);
            //System.out.println("Identifier: " + s + ", Type: " + type);
            return type;
        }   // epistrefei typo
        return s;   // onoma methodou
    }

    @Override
    public String visit(ThisExpression n, Void argu) {
        return "this";
    }

    /**
     * f0 -> BooleanArrayAllocationExpression|IntegerArrayAllocationExpression
    */
    @Override
    public String visit(ArrayAllocationExpression n, Void argu)throws Exception  {
        return n.f0.accept(this, argu);
    }

     /**
     * f0 -> "new" 
     * f1 -> "boolean"
     * f2 -> "["
     * f3 -> Expression 
     * f4 -> "]"
    */
    @Override
    public String visit(BooleanArrayAllocationExpression n, Void argu)throws Exception  {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        use_var = 1;
        String e = n.f3.accept(this, argu);
        //check that expr between [] is int
        if (!(e.equals("int"))) throw new Exception("BooleanArrayAllocationExpression:I was expecting expr = int not " + e);
        n.f4.accept(this, argu);
        return "boolean[]";
    }

     /**
     * f0 -> "new" 
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression 
     * f4 -> "]"
    */
    @Override
    public String visit(IntegerArrayAllocationExpression n, Void argu)throws Exception  {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        use_var = 1;
        String e = n.f3.accept(this, argu);
        //check that expr between [] is int
        if (!(e.equals("int"))) throw new Exception("IntegerArrayAllocationExpression: Expecting int exp not " + e );        
        n.f4.accept(this, argu);
        return "int[]";
    }

     /**
     * f0 -> "new" 
     * f1-> Identifier 
     * f2 -> "(" 
     * f3 -> ")"
    */
    @Override
    public String visit(AllocationExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        use_var = 0;
        String i = n.f1.accept(this, argu);
        //checks that it's not main class
        if (i.equals(main_class))  throw new Exception ("AllocationExpression: Main can't be called at new allocation");
        //check if class exist is ST
        if (!(st.has_classes(i))) throw new Exception ("AllocationExpression: There is't class " + i);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return i + "()";
    }

    /**
     * f0 -> "!"
     * f1 -> Clause 
    */
    @Override
    public String visit(NotExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        use_var= 1;
        String c = n.f1.accept(this, argu);
        //check if clause is boolean type
        if (!( c.equals("boolean"))) throw new Exception("NotExpression: I was expecting boolean expr, not" + c);        
        return  "boolean" ;

    }

    /**
     * f0 -> "(" 
     * f1 -> Expression 
     * f2-> ")"
    */
    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        use_var = 1;
        String s = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        //return type of expr
        return s ;
    }

    public String find_type(String name) throws Exception{

        //take temp class and the temp method in temp class
        classType c = st.get_classes(temp_class);
        methodType m = c.get_methods(temp_method);

        // checks if it is local
        if (m.has_lvariables(name)){
            return m.get_lvariables(name);
        } 

        // checks if it is parametrs
        if (m.has_parametrs(name)){
            return m.get_parametrs(name);
        }

        // checks if it is a variable of class
        if (c.has_variables(name)){
            return c.get_variables(name);
        }

        //checks if it exist in parent classes
        classType par; 
        if (c.hasparent){
            String temp_parent = c.parent ;
            while(true){
                par = st.get_classes(temp_parent);
                if (par.has_variables(name)){
                    return par.get_variables(name);
                }
                if (!(par.hasparent)) break;
                temp_parent = par.parent;
            }
        }
        //if we didn't find it
        throw new Exception("find_type(): Variables " + name + " doesn't define before use at " + temp_class + "->" + temp_method); 
    }

    public boolean check_parametrs(methodType a, methodType b){
        //System.out.println(">>> Comparing parameters");
        //System.out.println("Expected: " + b.parametrs);
        //System.out.println("Given:    " + a.parametrs);
        //System.out.println("name 1" + a.name + "name 2 "+ b.name);
        //System.out.println("1");
        //checks the size of parametrs
        if ( a.parametrs.size()!= b.parametrs.size()){
            System.out.println("size a :  " + a.parametrs.size() + " size b: " + b.parametrs.size());
            return false; 
        } 
        //System.out.println("2");
        //checks one by one
        Iterator<Map.Entry<String,String>> it1 = a.parametrs.entrySet().iterator();
        Iterator<Map.Entry<String,String>> it2 = b.parametrs.entrySet().iterator();
        //check one by one the type of parametrs
        while (it1.hasNext() && it2.hasNext()){
            String g = it1.next().getValue();
            String e = it2.next().getValue();
            //System.out.println("GivenType: " + g + " ExpectedType: " + e);
            //if it's ok continue with the next paramer
            if (g.equals(e)) continue;
            //if both are classes but different check for connection parent-child 
            if (st.has_classes(e) && st.has_classes(g)){
                String ch = g;
                classType c = st.get_classes(ch);
                Boolean f = false;
                //System.err.println(c.name +" c.hasparent " + c.hasparent);
                while(c.hasparent){
                    ch = c.parent;
                    //System.out.println("check parametrs: parent of " + c.name + " is " + ch); 
                    if (ch.equals(e)){
                        f = true;
                        break;
                    }
                    c=st.get_classes(ch);
                } 
                //if it doesn't have connection parent-child
                if (f.equals(false)) {
                    System.out.println("check parametrs: "+ e +" it's not subtype of " + g); 
                    return false;
                }
            }
            else {
                System.out.println("Different type"); 
                return false;
            }
        }
        return true;
    }
}