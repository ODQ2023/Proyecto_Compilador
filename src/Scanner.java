import javax.xml.stream.events.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);
        palabrasReservadas.put("string", TipoToken.STRING);
        palabrasReservadas.put("$",      TipoToken.EOF);
        palabrasReservadas.put("==",     TipoToken.EQUAL_EQUAL);
        palabrasReservadas.put("=",      TipoToken.EQUAL);
        palabrasReservadas.put("<",      TipoToken.LESS);
        palabrasReservadas.put("<=",     TipoToken.LESS_EQUAL);
        palabrasReservadas.put(">",      TipoToken.GREATER);
        palabrasReservadas.put(">=",     TipoToken.GREATER_EQUAL);
        palabrasReservadas.put("!",      TipoToken.BANG);
        palabrasReservadas.put("!=",     TipoToken.BANG_EQUAL);
        palabrasReservadas.put(",",      TipoToken.COMMA);
        palabrasReservadas.put(".",      TipoToken.DOT);
        palabrasReservadas.put(";",      TipoToken.SEMICOLON);
        palabrasReservadas.put("-",      TipoToken.MINUS);
        palabrasReservadas.put("+",      TipoToken.PLUS);
        palabrasReservadas.put("/",      TipoToken.SLASH);
        palabrasReservadas.put("(",      TipoToken.LEFT_PAREN);
        palabrasReservadas.put(")",      TipoToken.RIGHT_PAREN);
        palabrasReservadas.put("{",      TipoToken.LEFT_BRACE);
        palabrasReservadas.put("}",      TipoToken.RIGHT_BRACE);
        palabrasReservadas.put("*",      TipoToken.STAR);
    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();
    
    public Scanner(String source){
        this.source = source + " ";
    }

    public List<Token> scan() throws IOException {
        String lexema = "",frase = "";
        int estado = 0,renglon = 0,comment = 0;
        char c;


        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);

            switch (estado){
                case 0:
                    if (c == '=') {
                    lexema += c;
                    estado =1;
                    }
                    else if (c == '/') {
                      estado = 3;
                      lexema += c;
                    }
                    else if(c =='-'){
                        estado = 6;
                        lexema+= c;
                    }
                    else if(c == '"'){
                        estado = 8;
                        lexema+=c;
                    }
                    else if(Character.isLetter(c)){
                        estado = 9;
                        lexema += c;
                    }
                    else if(Character.isDigit(c)) {
                        estado = 11;
                        lexema += c;
                    }
                    else if((c == ' ') || (c == '\t')){
                    }
                    else if((c == '\n') || (c == '\r')){
                        renglon ++;
                    }
                    else {
                        estado = 14;
                        lexema += c;
                    }
                    break;
                        /*while(Character.isDigit(c)){
                            lexema += c;
                            i++;
                            c = source.charAt(i);
                        }
                        Token t = new Token(TipoToken.NUMBER, lexema);
                        lexema = "";
                        estado = 0;
                        tokens.add(t);
                        */
                case 1:
                    if (c=='='){
                        lexema+=c;
                        Token t=new Token(TipoToken.EQUAL_EQUAL,lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema ="";
                    }
                    else {
                        Token t=new Token(TipoToken.EQUAL,lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema="";
                        i--;
                    }
                    break;
                case  2:
                    comment++;
                    lexema+=c;
                    if (lexema.charAt(comment-1)=='*' && lexema.charAt(comment)=='/'){
                    estado= 0;
                    lexema= "";
                    }
                    break;
                case  3:
                    if(c=='*'){
                        lexema +=c;
                        comment++;
                        estado = 2;
                    }
                    else if (c=='/'){
                        lexema +=c;
                        estado = 4;
                    }
                    else {
                       Token t=new Token(TipoToken.SLASH,lexema);
                       tokens.add(t);
                       lexema="";
                    }
                    break;
                case  4:
                    if(c=='\r'|| c== '\n'){
                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    else {
                        lexema +=c;
                    }
                    break;
                case  6:
                    if(Character.isDigit(c)){
                        estado=11;
                        lexema+=c;
                    }
                    else{
                        Token t= new Token(TipoToken.MINUS,lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema="";
                        i--;
                    }
                    break;
                case  8:
                    if(c!='"'){
                        if (c == '\n' || c== '\r'){
                            Interprete.error(renglon,"\" Expected");
                            return tokens;
                        }
                        else{
                        lexema +=c;
                        frase += c;
                        }
                    }
                    else{
                        lexema+=c;
                        Token t = new Token(TipoToken.STRING, lexema, frase);
                        tokens.add(t);
                        estado = 0;
                        lexema = "";
                        frase = "";
                    }

                    break;

                case 9:
                    if(Character.isLetterOrDigit(c)){
                        //estado = 9;
                        lexema += c;
                    }
                    else{
                        // Vamos a crear el Token de identificador o palabra reservada
                        TipoToken tt = palabrasReservadas.get(lexema);

                        if(tt == null){
                            Token t = new Token(TipoToken.IDENTIFIER, lexema);
                            tokens.add(t);
                        }
                        else{
                            Token t = new Token(tt, lexema);
                            tokens.add(t);
                        }

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 11:
                    if(Character.isDigit(c)){
                        lexema += c;
                    }
                    else if(c == '.'){
                        estado = 12;
                        lexema += c;
                    }
                    else if(c == 'E'){
                        estado = 13;
                        lexema += c;
                    }
                    else{
                        Token t = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                        tokens.add(t);

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 12:
                    if(Character.isDigit(c)){
                        lexema += c;
                    }
                    else{
                        /*if (!Character.isAlphabetic(c) && (c != ' ')&& (c != ';')){
                            Interprete.error(i,c+" Not Expected");
                            return tokens;
                        }*/
                        //else{
                        Token t = new Token(TipoToken.NUMBER, lexema, Float.valueOf(lexema));
                        tokens.add(t);
                        estado = 0;
                        lexema = "";
                        i--;
                        //}
                    }
                    break;
                case 13:
                    if(Character.isDigit(c)){
                        lexema += c;
                    }
                    else{
                        Token t = new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema));
                        tokens.add(t);
                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 14:
                    if(c=='='){
                        lexema+=c;
                    }
                    else {
                        // Vamos a crear el Token de identificador o palabra reservada
                        TipoToken tt = palabrasReservadas.get(lexema);

                        if (tt == null) {
                            Interprete.error(renglon, c + " not expected");
                            return tokens;
                        } else {
                            Token t = new Token(tt, lexema);
                            tokens.add(t);
                        }
                        estado = 0;
                        lexema = "";
                        i--;
                    }

                    break;
            }
        }

        return tokens;
    }
}
