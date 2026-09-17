class Example {
    public static void main(String[] args) {
        A a;
        B b;
        b = new B();
        a=b;
    }
}

class A{
      int i;
      boolean flag;
      int j;
      public int foo() { return i; }
      public boolean fa() { return flag; }
  }

  class B extends A{
      A type;
      int k;
      public int foo() {return k;}
      public boolean bla() {return flag;}
  }