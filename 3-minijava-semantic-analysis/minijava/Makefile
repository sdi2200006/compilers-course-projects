all: compile

compile:
	java -jar ../jtb132di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac classType.java
	javac methodType.java
	javac SymbolTable.java
	javac Main.java

run:
	java Main examples/Example.java
run1:
	java Main examples/BinaryTree.java examples/BubbleSort.java examples/Factorial.java examples/LinearSearch.java examples/LinkedList.java examples/MoreThan4.java examples/QuickSort.java examples/TreeVisitor.java
run2:
	java Main  examples/minijava-extra/Add.java   examples/minijava-extra/ArrayTest.java examples/minijava-extra/CallFromSuper.java examples/minijava-extra/Classes.java examples/minijava-extra/DerivedCall.java 
run3:
	java Main  examples/minijava-extra/Example1.java   examples/minijava-extra/FieldAndClassConflict.java examples/minijava-extra/Main.java examples/minijava-extra/ManyClasses.java examples/minijava-extra/OutOfBounds1.java 
run4: 
	java Main  examples/minijava-extra/Overload2.java   examples/minijava-extra/ShadowBaseField.java examples/minijava-extra/ShadowField.java examples/minijava-extra/test06.java examples/minijava-extra/test07.java 
run5: 
	java Main  examples/minijava-extra/test15.java   examples/minijava-extra/test17.java examples/minijava-extra/test20.java examples/minijava-extra/test62.java examples/minijava-extra/test73.java examples/minijava-extra/test82.java examples/minijava-extra/test93.java examples/minijava-extra/test99.java 
error1:
	java Main examples/BubbleSort-error.java 
error2:
	java Main examples/Factorial-error.java
error3:
	java Main examples/LinearSearch-error.java 
error4:
	java Main examples/LinkedList-error.java 
error5:
	java Main examples/MoreThan4-error.java 
error6:
	java Main examples/QuickSort-error.java 
error7:
	java Main examples/TreeVisitor-error.java
error8:
	java Main examples/minijava-error-extra/BadAssign.java 
error9:
	java Main examples/minijava-error-extra/BadAssign2.java 
error10:
	java Main examples/minijava-error-extra/Classes-error.java 
error11:
	java Main examples/minijava-error-extra/DoubleDeclaration1.java 
error12:
	java Main examples/minijava-error-extra/DoubleDeclaration4.java 
error13:
	java Main examples/minijava-error-extra/DoubleDeclaration6.java 
error14:
	java Main examples/minijava-error-extra/NoMatchingMethod.java
error15:
	java Main examples/minijava-error-extra/NoMethod.java 
error16:
	java Main examples/minijava-error-extra/Overload1.java 
error17:
	java Main examples/minijava-error-extra/test18.java 
error18:
	java Main examples/minijava-error-extra/test21.java 
error19:
	java Main examples/minijava-error-extra/test35.java
error20:
	java Main examples/minijava-error-extra/test52.java 
error21:
	java Main examples/minijava-error-extra/test68.java 
error22:
	java Main examples/minijava-error-extra/UseArgs.java
clean:
	rm -rf *.class syntaxtree visitor minijava-jtb.jj *~

