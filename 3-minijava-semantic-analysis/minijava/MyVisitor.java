import syntaxtree.*;
import visitor.*;


class MyVisitor extends GJDepthFirst<String, Void>{
    public  SymbolTable st;
    public  String temp_class;
    public  String temp_method;
    public  boolean current ;    //1->class  0->method  (used at varDeclaration)
    public String main_class;
         

    public MyVisitor(SymbolTable s){
        this.st = s;
    }

    /**
     * f0 -> Goal	::=	MainClass
     * f1 -> ( TypeDeclaration )* 
     * f2 -> <EOF>
     */
    @Override
    public String visit(Goal n, Void argu) throws Exception {
        String ret = n.f0.accept(this,argu);
        for ( Node node: n.f1.nodes) {
            ret +=  node.accept(this, argu);
        }
        return ret;
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
        String classname = n.f1.accept(this, null);
        main_class = classname;
        String arg = n.f11.accept(this, argu);
        //create thw main class and the main method
        classType c = new classType();
        c.put_name(classname);
        //keep the main class
        temp_class = classname;
        methodType m = new methodType();
        m.put_name("main");
        m.put_type("void"); 
        m.put_parametr(arg, "String[]"); 
        c.put_methods("main",m);
        st.put_classes(classname, c);
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
        n.f0.accept(this, argu);
        //create new class
        String classname = n.f1.accept(this, argu);
        classType c = new classType();
        c.put_name(classname);
        st.put_classes(classname, c);
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
        n.f0.accept(this, argu);
        String classname = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String parentclass = n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        //If paren class has not been defined 
        if (!(st.has_classes(parentclass))) throw new Exception(parentclass + " parentclass doesn't exist");
        //create new class
        classType c = new classType();
        c.put_name(classname);
        c.put_parent(parentclass);
        c.hasparent = true;
        temp_class = classname;
        current = true;  //class
        st.put_classes(classname, c);

        String var = "";
        for ( Node node: n.f5.nodes) {
            var +=  node.accept(this, argu);
        }
        String method = "";
        for ( Node node: n.f6.nodes) {
            method +=  node.accept(this, argu);
        }
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
        String var = n.f1.accept(this, argu);
        if (var.equals("this")) throw new Exception("this is bound word. So we can't have variable with this name "); 
        n.f2.accept(this, argu);
        //if it is in class's variable
        if (current){
            classType c = st.get_classes(temp_class);
            c.put_variables(var, type);
        }
        else{    
            //if it is method's variable   
            classType c = st.get_classes(temp_class);
            methodType m = c.get_methods(temp_method);
            m.put_lvariables(var, type);
        }
        return  type + " " + var + "\n" ;
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
        n.f0.accept(this, argu);
        String type = n.f1.accept(this, argu);
        String id = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        // create method
        methodType m = new methodType();
        m.put_name(id);
        m.put_type(type);
        //put method in temp class
        classType c = st.get_classes(temp_class);
        c.put_methods(id, m);
        temp_method = id;
        current = false;  // method

        String arg = n.f4.present() ? n.f4.accept(this, argu) : "";
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        //if temp class has parent
        classType par;
        if (c.hasparent){
            String temp_parent = c.parent ;
            //for all the parent classes
            while(true){
                par = st.get_classes(temp_parent);
                //if temp parent class has the same method
                if (par.has_methods(id)){
                    methodType method_p = par.get_methods(id);
                    //check that it has the same return type and parametrs
                    if (!(par.same_method(method_p, m))) throw new Exception("Wrong override at class" + temp_class + " from " + temp_parent);
                }
                //when you go to a class without parent stop
                if (!(par.hasparent)) break;
                temp_parent = par.parent;

            }
        }
        String var = "";
        for ( Node node: n.f7.nodes) {
            var +=  node.accept(this, argu);
        }
        String statement = "";
        for ( Node node: n.f8.nodes) {
            statement +=  node.accept(this, argu);
        }
        n.f9.accept(this, argu);
        String exp = n.f10.accept(this,argu);
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
        classType c = st.get_classes(temp_class);
        //put parametr at temp method
        methodType m = c.get_methods(temp_method);
        m.put_parametr(name, type);
        return type + " " + name ;
    }

    /**
     * f0 -> ( FormalParameterTerm )*
     */
    @Override
    public String visit(FormalParameterTail n, Void argu) throws Exception {
        String ret = " ";
        for ( Node node: n.f0.nodes) {
            ret +=  node.accept(this, argu);
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
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String i2 = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return i1 + " = "  + i2+ ";\n";
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
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String i2 = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        String i3 = n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return i1 + " [ " + i2 + " ] = " + i3 + " ;\n";
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
        String i1 = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        String i2 = n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        String i3 =n.f6.accept(this, argu);
        return "if ( " + i1 +" )\n"+ i2 + "\nelse \n" + i3 +"\n";
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
        String i1 = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        String i2 = n.f4.accept(this, argu);
        return " while( " + i1 +" )\n "+ i2 + "\n";
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
        String i = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return "System.out.println( " + i + " );\n";
    } 

    /**
     * f0 -> Clause
     * f1 -> "&&"
     * f2 -> Clause
     */
    @Override
    public String visit(AndExpression n, Void argu) throws Exception {
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String i2 =n.f2.accept(this, argu);
        return i1 + " && "+ i2 ;
    } 

    /**
     * f0 ->PrimaryExpression 
     * f1 -> "<"
     * f2 -> PrimaryExpression
     */
    @Override
    public String visit(CompareExpression n, Void argu) throws Exception {
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String i2 =n.f2.accept(this, argu);
        return i1 + "<"+ i2;
    } 

    /**
     * f0 ->PrimaryExpression 
     * f1 -> "+"
     * f2 -> PrimaryExpression
     */
    @Override
    public String visit(PlusExpression n, Void argu) throws Exception {
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String i2 =n.f2.accept(this, argu);
        return i1 + "+"+ i2;
    } 

    /**
     * f0 ->PrimaryExpression 
     * f1 -> "-"
     * f2 -> PrimaryExpression
     */
    @Override
    public String visit(MinusExpression n, Void argu) throws Exception {
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String i2 =n.f2.accept(this, argu);
        return i1 + "-"+ i2;
    } 
    
    /**
     * f0 ->PrimaryExpression 
     * f1 -> "*"
     * f2 -> PrimaryExpression
     */
    @Override
    public String visit(TimesExpression n, Void argu) throws Exception {
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String i2 =n.f2.accept(this, argu);
        return i1 + "*"+ i2;
    } 

    /**
     * f0 ->PrimaryExpression 
     * f1 -> "["
     * f2 -> PrimaryExpression
     * f3 -> "]" 
     */
    @Override
    public String visit(ArrayLookup n, Void argu) throws Exception {
        String i1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String i2 = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return i1 +" [ " + i2 +"] ";
    } 

    /**
     * f0 -> PrimaryExpression 
     * f1 -> "." 
     * f2 -> "length" 
     */
    @Override
    public String visit(ArrayLength n, Void argu) throws Exception {
        String e = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return e + ".length ";
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
        String i1 =n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String i2 = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        String argumentList = n.f4.present() ? n.f4.accept(this, argu) : "";
        n.f5.accept(this, argu);
        return i1 + "." + i2 + "( "+ argumentList +" )\n";
    } 

     /**
     * f0 -> Expression 
     * f1 -> ExpressionTail
     */
    @Override
    public String visit(ExpressionList n, Void argu) throws Exception {
        String i1 = n.f0.accept(this, argu);
        String i2 = n.f1.accept(this, argu);
        return i1 + i2;
    } 

    /**
     * f0 -> ( ExpressionTerm )*
     */
    @Override
    public String visit(ExpressionTail n, Void argu) throws Exception {
        String ret = "";
        for (Node node:n.f0.nodes){
            ret += node.accept(this,argu);
        }
        return ret;
    } 

    /**
     * f0 -> ","  
     * f1 -> Expression
     */
    @Override
    public String visit(ExpressionTerm n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String e = n.f1.accept(this, argu);
        return " , " + e ;
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
        return n.f0.toString();
    }

    @Override
    public String visit(TrueLiteral n, Void argu) {
        return "true";
    }
    
    @Override
    public String visit(FalseLiteral n, Void argu) {
        return "false";
    }

    @Override
    public String visit(Identifier n, Void argu) {
        return n.f0.toString();
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
        String e = n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return " new boolean [ " + e + " ] ";
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
        String e = n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return " new int [ " + e + " ] ";
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
        String i = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return " new " + i + " ( ) " ;
    }

    /**
     * f0 -> "!"
     * f1 -> Clause 
    */
    @Override
    public String visit(NotExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String c = n.f1.accept(this, argu);
        return " ! " + c + " ";

    }

    /**
     * f0 -> "(" 
     * f1 -> Expression 
     * f2-> ")"
    */
    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String s = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return " ( "+ s +" ) ";
    }
}

