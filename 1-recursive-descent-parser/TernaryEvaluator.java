import java.io.InputStream;
import java.io.IOException;


class TernaryEvaluator {
    private final InputStream in;
    private int lookahead;

    public TernaryEvaluator(InputStream in) throws IOException {
        this.in = in;
        lookahead = in.read();
    }

    private void consume(int symbol) throws IOException, ParseError {
        if (lookahead == symbol)
            lookahead = in.read();
        else
            throw new ParseError();
    }

    private void consume_spaces() throws IOException, ParseError {
        while(lookahead == ' ' || lookahead == '\t') lookahead = in.read();
    }

    private boolean isDigit(int c) {
        return '0' <= c && c <= '9';
    }

    private int evalDigit(int c) {
        return c - '0';
    }

    public int eval() throws IOException, ParseError {
        int value = Expr();

        if (lookahead != -1 && lookahead != '\n')
            throw new ParseError();

        return value;
    }

    private int Expr() throws IOException, ParseError {
        int i = Term();
        return Expr2(i);
    }

    private int Expr2(int i) throws IOException, ParseError {
        consume_spaces();
        if (lookahead == '+') {
            consume(lookahead);
            consume_spaces();
            int j = Term();
            return Expr2(i+j);
        }
        if (lookahead == '-') {
            consume(lookahead);
            consume_spaces();
            int j = Term();
            return Expr2(i-j);
        }
        return i; //periptwsh kenhs
    }

    private int Term() throws IOException, ParseError {
        int i = Factor();
        return Term2(i);
    }

    private int Term2(int i) throws IOException, ParseError {
        consume_spaces();
        if (lookahead == '*') {
            consume(lookahead);
            if (lookahead == '*') { 
                consume(lookahead);
                consume_spaces();
                int j = Factor();
                consume_spaces();
                if (lookahead == '*') {
                    int k =Term2(j);
                    return Term2((int)Math.pow(i,k));
                }
                return Term2((int)Math.pow(i,j));
            } else {
                throw new ParseError(); 
            }
        }
        return i; //periptwsh kenhs
    }   

    private int Factor() throws IOException, ParseError {
        consume_spaces();
        if (isDigit(lookahead)) {
            return Num(0);
        }
        if (lookahead == '('){
            consume(lookahead);
            int cond = Expr();
            if (lookahead == ')'){
                consume(lookahead);
                return cond;
            }
        }
        throw new ParseError(); 
    }

    private int Num(int cond) throws IOException, ParseError {
        if (isDigit(lookahead)) {  
            int value = evalDigit(lookahead);  
            consume(lookahead); 
            if (isDigit(lookahead)) {
                return Num(cond * 10 + value);
            }
            return cond * 10 + value; 
        }
        throw new ParseError();
    }

    private int Digit() throws IOException, ParseError {
        if (isDigit(lookahead)) {
            int cond = evalDigit(lookahead);
            consume(lookahead);
            return cond ; 
        }
        throw new ParseError();
    }
}