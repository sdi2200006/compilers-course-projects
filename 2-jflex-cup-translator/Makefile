compile:
	jflex scanner.flex
	java -jar ../java-cup-11b.jar -interface -parser Parser  parser.cup
	java -jar ../java-cup-11b.jar -interface -parser Parser2  parser2.cup
	javac -cp ../java-cup-11b-runtime.jar *.java

execute1:
	java -cp ../java-cup-11b-runtime.jar:. Main < examples/input1.txt > examples/Translated.ir
	java -cp ../java-cup-11b-runtime.jar:. Main2 < examples/Translated.ir > examples/Translated.java

execute2:
	java -cp ../java-cup-11b-runtime.jar:. Main < examples/input2.txt > examples/Translated.ir
	java -cp ../java-cup-11b-runtime.jar:. Main2 < examples/Translated.ir  > examples/Translated.java

execute3:
	java -cp ../java-cup-11b-runtime.jar:. Main < examples/input3.txt > examples/Translated.ir
	java -cp ../java-cup-11b-runtime.jar:. Main2 < examples/Translated.ir  > examples/Translated.java

clean:
	rm -f *.class *~
	rm -f Parser.java Parser2.java Scanner.java sym.java examples/Translated.java examples/Translated.ir 