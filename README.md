# Compilers Course Projects
The projects cover different stages of compiler construction, including parsing, lexical analysis, syntax analysis and MiniJava semantic analysis.
A collection of projects developed for the Compilers course during the 6th semester.

## Projects

### 1. Recursive Descent Parser (1-recursive-descent-parser/)
Implementation of a recursive-descent parser and evaluator for arithmetic expressions based on a context-free grammar (read README).

Main concepts covered:
- Context-free grammars
- Recursive-descent parsing
- Expression evaluation
- Parsing errors and exception handling
- Java implementation of grammar productions

#### Run
```bash
cd 1-recursive-descent-parser
make
```

### 2. JFlex & Java CUP Translator (2-jflex-cup-translator/)
Implementation of lexical and syntax analysis using JFlex and Java CUP.
The project includes a lexer, grammar definitions and a two-stage translation process.

The project uses the Java CUP libraries located in the repository root java-cup-11b.jar
and java-cup-11b-runtime.jar.
 
Main concepts covered:
- Lexical analysis
- Token recognition
- Syntax analysis
- JFlex
- Java CUP
- Grammar rules
- Intermediate representation
- Source-to-source translation

Example input files are located in jflex-cup-translator/examples/

#### Compile
```bash
cd 2-jflex-cup-translator
make
```


### 3. MiniJava Semantic Analysis (3-minijava-semantic-analysis/)
Implementation of semantic analysis and type checking for the MiniJava language.
The project uses a generated syntax tree together with custom visitors to construct and inspect program information.

Main concepts covered:
- JavaCC
- Abstract syntax trees
- Visitor pattern
- Symbol tables
- Semantic analysis
- Type checking
- Class inheritance
- Method and variable lookup
- Method parameter validation
- Semantic error detection

The main implementation is in minijava-semantic-analysis/minijava/ and the test programs are in minijava-semantic-analysis/minijava/examples/

#### Compile
```bash
cd 3-minijava-semantic-analysis/minijava
make
```

#### Run
For example:
```bash
make run
make run[1-5]
make error[1-22]
```

## Repository Structure
```text
compilers-course-projects/
├── 1-recursive-descent-parser/
│   ├── Main.java
│   ├── TernaryEvaluator.java
│   ├── ParseError.java
│   ├── Makefile
│   └── README.md
├── 2-jflex-cup-translator/
│   ├── Main.java
│   ├── Main2.java
│   ├── scanner.flex
│   ├── parser.cup
│   ├── parser2.cup
│   ├── Makefile
│   ├── README.md
│   └── examples/
├── 3-minijava-semantic-analysis/
│   ├── javacc5.jar
│   ├── jtb132di.jar
│   ├── README.md
│   └── minijava/
│       ├── Main.java
│       ├── MyVisitor.java
│       ├── MyVisitor2.java
│       ├── SymbolTable.java
│       ├── classType.java
│       ├── methodType.java
│       ├── minijava.jj
│       ├── Makefile
│       └── examples/
├── java-cup-11b.jar
├── java-cup-11b-runtime.jar
└── README.md
```

## Technologies
* Java
* JFlex
* Java CUP
* JavaCC
* JTB
* Makefile

## What I Practiced
- Designing and implementing parsers
- Working with context-free grammars
- Performing lexical and syntax analysis
- Using parser and lexer generators
- Traversing abstract syntax trees
- Building and using symbol tables
- Implementing semantic and type checks
- Handling inheritance and subtype relationships
- Detecting invalid programs during semantic analysis
- Organizing multi-stage compiler projects in Java
