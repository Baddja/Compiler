/*
 * Compilers Assignment 2
 * Parsing
 * 
 * Harjinder Dhaliwal
 * 094500260
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class dhal0260_a2 {

	public static void main(String[] args){
		Parser p = new Parser();
	}
	
	public static class Parser{
		Token lookahead;
		Tokenizer lex;
		SymbolTables tables;

		int lineNumber = 1;
		
		String varType = "";
		
		boolean parsing = false;
		boolean decFunc = false;
		boolean decVar = false;
		boolean decParams = false;
		
		public Parser(){
			
			lex = new Tokenizer();
			
			while(!lex.ready()){
				
			}
			
			tables = new SymbolTables();
			
			System.out.println("<html><head></head><body style='padding:0 20px 0 20px;font-size:18px;font-family:Courier, monospace;background-color:black;color:#E6E6E6;'>");
			lookahead = lex.nextToken();
			
			program();
			printComments();
			
			System.out.print("<br><br>Parsing Completed Successfully<br><br>");
			
			tables.printHTMLTables();
			
			System.out.println("</body></html>");
			
		}
		
		public void printLineNumber(){
			System.out.print("<font color='#848484'>"+lineNumber);
			int length = Integer.toString(lineNumber).length();
			for(int i=0;i<4-length;i++){
				System.out.print("&nbsp;");
			}
			System.out.print("</font>");
			lineNumber++;
		}
		
		public void printLookahead(){
			String type = lookahead.getType();
			if(type.equals("id")){
				System.out.print("<font color='#0174DF'>"+lookahead.getValue()+"</font>");
			}else if(type.equals("integer") || type.equals("double") || type.equals("exp")){
				System.out.print("<font color='#04B4AE'>"+lookahead.getValue()+"</font>");
			}else if(type.equals("comp")){
				System.out.print("<font color='#DBA901'>"+lookahead.getValue()+"</font>");
			}else if(type.equals("terminal")){
				System.out.print(lookahead.getValue());
			}else if(type.equals("separator")){
				String v=lookahead.getValue();
				if(v.equals("\n")){
					System.out.print("<br>");
					printLineNumber();
				}else if(v.equals("\t")){
					System.out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
				}else if(v.equals(" ")){
					System.out.print("&nbsp;");
				}
			}else if(type.equals("keyword")){
				System.out.print("<font color='#DF0174'>"+lookahead.getValue()+"</font>");
			}else if(type.equals("error")){
				System.out.print("<font color='red'>"+lookahead.getValue()+"</font>");
				error("is not a valid lexial token");
			}else if(type.equals("EOI")){
				//do something to indicate EOF
			}
		}
		
		public void printRemaining(){
			String t;
			while((t=lookahead.getType())!="EOI"){
				if(t.equals("id")){
					System.out.print("<font color='#0174DF'>"+lookahead.getValue()+"</font>");
				}else if(t.equals("integer") || t.equals("double") || t.equals("exp")){
					System.out.print("<font color='#04B4AE'>"+lookahead.getValue()+"</font>");
				}else if(t.equals("comp")){
					System.out.print("<font color='#DBA901'>"+lookahead.getValue()+"</font>");
				}else if(t.equals("terminal")){
					System.out.print(lookahead.getValue());
				}else if(t.equals("separator")){
					String v=lookahead.getValue();
					if(v.equals("\n")){
						System.out.print("<br>");
						printLineNumber();
					}else if(v.equals("\t")){
						System.out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
					}else if(v.equals(" ")){
						System.out.print("&nbsp;");
					}
				}else if(t.equals("keyword")){
					System.out.print("<font color='#DF0174'>"+lookahead.getValue()+"</font>");
				}else if(t.equals("error")){
					System.out.print("<font color='red'>"+lookahead.getValue()+"</font>");
				}
				lookahead = lex.nextToken();
			}
		}
		
		public void printComments(){
			String t;
			System.out.print("<font color='#848484'>");
			while((t=lookahead.getType())!="EOI"){
				if(t.equals("separator")){
					String v=lookahead.getValue();
					if(v.equals("\n")){
						System.out.print("<br>");
						printLineNumber();
					}else if(v.equals("\t")){
						System.out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
					}else if(v.equals(" ")){
						System.out.print("&nbsp;");
					}
				}else{
					System.out.print(lookahead.getValue());
				}
				lookahead = lex.nextToken();
			}
			System.out.print("</font>");
		}
		
		public void match(String type, String value){
			if(lookahead.getType().equals(type) && lookahead.getValue().equals(value)){
				printLookahead();
				lookahead = lex.nextToken();
				if(lookahead.getType().equals("separator")){
					match("separator");
				}
			}else{
				error("expected '"+value+"'");
			}
		}
		
		public void match(String type){
			if(lookahead.getType().equals(type)){
				printLookahead();
				lookahead = lex.nextToken();
				if(lookahead.getType().equals("separator")){
					match("separator");
				}
			}else{
				error("expected type '"+type+"'");
			}
		}
		
		public void error(String msg){
			System.out.print("&nbsp;<span style='background-color:red;color:white;'>Syntax Error at '"+lookahead.getValue()+"': "+msg+" </span>&nbsp;");
			printRemaining();
			System.out.println("</body></html>");
			System.exit(0);
		}
		
		public void program(){
			parsing = true;
			tables.addTable("Main");
			printLineNumber();
			fdecls();declarations();statement_seq();match("terminal",".");
			parsing = false;
		}
		
		public void fdecls(){
			if(first(lookahead, "fdec")){
				fdec();match("terminal",";");fdecls_r();
			}else if(follow(lookahead,"fdecls")){
				return;
			}else{
				error("&lt;fdecls&gt; malformed");
			}
		}
		
		public void fdecls_r(){
			if(first(lookahead, "fdec")){
				fdec();match("terminal",";");fdecls_r();
			}else if(follow(lookahead,"fdecls_r")){
				return;
			}else{
				error("&lt;fdecls&gt; malformed");
			}
			
		}
		
		public void fdec(){
			if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("def")){
				tables.addTable("");
				decFunc = true;
				match("keyword","def");type();fname();match("terminal","(");params();match("terminal",")");declarations();statement_seq();match("keyword","fed");
				decFunc = false;
				tables.swapTopTables();
			}else{
				error("incorrect &lt;fdec&gt;");
			}
			
		}
		
		public void params(){
			if(first(lookahead,"type")){
				decParams = true;
				type();var();params_opt();
				decParams = false;
			}else if(follow(lookahead,"params")){
				return;
			}else{
				error("&lt;params&gt; malformed");
			}
		}
		
		public void params_opt(){
			if(lookahead.getType().equals("terminal") && lookahead.getValue().equals(",")){
				match("terminal",",");params();
			}else if(follow(lookahead,"params_opt")){
				return;
			}else{
				error("&lt;params&gt; malformed");
			}
		}
		
		public void fname(){
			if(lookahead.getType().equals("id")){
				if(decFunc){
					tables.renameTopTable(lookahead.getValue());
				}
				match("id");
			}else{
				error("incorrect &lt;fname&gt;");
			}
		}
		
		public void declarations(){
			if(first(lookahead,"decl")){
				decl();match("terminal",";");declarations_r();
			}else if(follow(lookahead,"declarations")){
				return;
			}else{
				error("&lt;declarations&gt; malformed");
			}
		}
		
		public void declarations_r(){
			if(first(lookahead,"decl")){
				decl();match("terminal",";");declarations_r();
			}else if(follow(lookahead,"declarations_r")){
				return;
			}else{
				error("&lt;declarations&gt; malformed");
			}
		}
		
		public void decl(){
			if(first(lookahead,"type")){
				decVar = true;
				type();varlist();
				decVar = false;
			}else{
				error("incorrect &lt;decl&gt;");
			}
		}
		
		public void type(){
			if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("int")){
				if(decVar || decParams){
					varType = lookahead.getValue();
				}
				match("keyword","int");
				
			}else if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("double")){
				if(decVar || decParams){
					varType = lookahead.getValue();
				}
				match("keyword","double");
			}else{
				error("invalid &lt;type&gt;");
			}
			
		}
		
		public void varlist(){
			if(first(lookahead,"var")){
				var();varlist_opt();
			}else{
				error("&lt;varlist&gt; malformed");
			}
		}
		
		public void varlist_opt(){
			if(lookahead.getType().equals("terminal") && lookahead.getValue().equals(",")){
				match("terminal",",");var();varlist_opt();
			}else if(follow(lookahead,"varlist_opt")){
				return;
			}else{
				error("&lt;varlist&gt; malformed");
			}
		}
		
		public void statement_seq(){
			if(first(lookahead,"statement")){
				statement();statement_seq_opt();
			}else if(first(lookahead,"statement_seq_opt")){
				statement_seq_opt();
			}else if(follow(lookahead,"statement_seq")){
				return;
			}else{
				error("&lt;statement_seq&gt; malformed");
			}
		}
		
		public void statement_seq_opt(){
			if(lookahead.getType().equals("terminal") && lookahead.getValue().equals(";")){
				match("terminal",";");statement();statement_seq_opt();
			}else if(follow(lookahead,"statement_seq_opt")){
				return;
			}else{
				error("&lt;statement_seq&gt; malformed");
			}
		}
		
		public void statement(){
			if(first(lookahead,"var")){
				var();match("terminal","=");expr();
			}else if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("if")){
				match("keyword","if");bexpr();match("keyword","then");statement_seq();statement_opt();
			}else if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("while")){
				match("keyword","while");bexpr();match("keyword","do");statement_seq();match("keyword","od");
			}else if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("print")){
				match("keyword","print");expr();
			}else if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("return")){
				match("keyword","return");expr();
			}else if(follow(lookahead,"statement")){
				return;
			}else{
				error("incorrect &lt;statement&gt;");
			}
		}
		
		public void statement_opt(){
			if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("fi")){
				match("keyword","fi");
			}else if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("else")){
				match("keyword","else");statement_seq();match("keyword","fi");
			}else{
				error("incorrect &lt;statement&gt;");
			}
		}
		
		public void expr(){
			if(first(lookahead,"term")){
				term();expr_r();
			}else{
				error("incorrect &lt;expression&gt;");
			}
		}
		
		public void expr_r(){
			if(lookahead.getType().equals("terminal") && lookahead.getValue().equals("+")){
				match("terminal","+");term();expr_r();
			}else if(lookahead.getType().equals("terminal") && lookahead.getValue().equals("-")){
				match("terminal","-");term();expr_r();
			}else if(follow(lookahead,"expr_r")){
				return;
			}else{
				error("incorrect &lt;expression&gt;");
			}
		}
		
		public void term(){
			if(first(lookahead,"factor")){
				factor();term_r();
			}else{
				error("incorrect &lt;term&gt;");
			}
		}
		
		public void term_r(){
			if(lookahead.getType().equals("terminal") && lookahead.getValue().equals("*")){
				match("terminal","*");factor();term_r();
			}else if(lookahead.getType().equals("terminal") && lookahead.getValue().equals("/")){
				match("terminal","/");factor();term_r();
			}else if(lookahead.getType().equals("terminal") && lookahead.getValue().equals("%")){
				match("terminal","%");factor();term_r();
			}else if(follow(lookahead,"term_r")){
				return;
			}else{
				error("incorrect &lt;term&gt;");
			}
			
		}
		
		public void factor(){
			if(lookahead.getType().equals("id")){
				match("id");factor_opt();
			}else if(first(lookahead,"number")){
				number();
			}else if(lookahead.getType().equals("terminal") && lookahead.getValue().equals("(")){
				match("terminal","(");expr();match("terminal",")");
			}else{
				error("incorrect &lt;factor&gt;");
			}
			
		}
		
		public void factor_opt(){
			if(lookahead.getType().equals("terminal") && lookahead.getValue().equals("(")){
				match("terminal","(");exprseq();match("terminal",")");
			}else if(lookahead.getType().equals("terminal") && lookahead.getValue().equals("(")){
				match("terminal","[");expr();match("terminal","]");
			}else if(follow(lookahead,"factor_opt")){
				return;
			}else{
				error("incorrect &lt;factor&gt;");
			}
		}
		
		public void exprseq(){
			if(first(lookahead,"expr")){
				expr();exprseq_opt();
			}else if(follow(lookahead,"exprseq")){
				return;
			}else{
				error("&lt;exprseq&gt; malformed");
			}
		}
		
		public void exprseq_opt(){
			if(lookahead.getType().equals("terminal") && lookahead.getValue().equals(",")){
				match("terminal",",");expr();exprseq_opt();
			}else if(follow(lookahead,"exprseq_opt")){
				return;
			}else{
				error("&lt;exprseq&gt; malformed");
			}
		}
		
		public void bexpr(){
			if(first(lookahead,"bterm")){
				bterm();bexpr_r();
			}else{
				error("incorrect &lt;bexpr&gt;");
			}
		}
		
		public void bexpr_r(){
			if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("or")){
				match("keyword","or");bterm();bexpr_r();
			}else if(follow(lookahead,"bexpr_r")){
				return;
			}else{
				error("incorrect &lt;bexpr&gt;");
			}
		}
		
		public void bterm(){
			if(first(lookahead,"bfactor")){
				bfactor();bterm_r();
			}else{
				error("incorrect &lt;bterm&gt;");
			}
		}
		
		public void bterm_r(){
			if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("and")){
				match("keyword","and");bfactor();bterm_r();
			}else if(follow(lookahead,"bterm_r")){
				return;
			}else{
				error("incorrect &lt;bterm&gt;");
			}
		}
		
		public void bfactor(){
			if(lookahead.getType().equals("terminal") && lookahead.getValue().equals("(")){
				match("terminal","("); bfactor_opt();
			}else if(lookahead.getType().equals("keyword") && lookahead.getValue().equals("not")){
				match("keyword","not");bfactor();
			}else{
				error("incorrect &lt;bfactor&gt;");
			}
		}
		
		public void bfactor_opt(){
			if(first(lookahead,"bexpr")){
				bexpr();match("terminal",")");
			}else if(first(lookahead,"expr")){
				expr();match("comp");expr();match("terminal",")");
			}else{
				error("incorrect &lt;bfactor&gt;");
			}
		}
		
		public void var(){
			if(lookahead.getType().equals("id")){
				if(decVar || decParams){
					tables.addSymbolToTop(varType, lookahead.getValue(), "unknown");
				}
				match("id");var_opt();
				
			}else{
				error("incorrect &lt;var&gt;");
			}
			
		}
		
		public void var_opt(){
			if(lookahead.getType().equals("terminal") && lookahead.getValue().equals("[")){
				match("terminal","[");expr();match("terminal","]");
			}else if(follow(lookahead,"var_opt")){
				return;
			}else{
				error("incorrect &lt;var&gt;");
			}
		}
		
		public void number(){
			if(lookahead.getType().equals("integer")){
				match("integer");
			}else if(lookahead.getType().equals("double")){
				match("double");
			}else{
				error("incorrect &lt;number&gt;");
			}
		}
		
		
		public boolean first(Token token, String type){
			boolean result = false;
			
			switch(type){
			case "program":
				result = first(token,"fdecls") || first(token,"declarations") || first(token,"statement_seq") || (token.getType().equals("terminal") && token.getValue().equals("."));
				break;
			case "fdecls":
				result = first(token,"fdec");
				break;
			case "fdecls_r":
				result = first(token,"fdec");
				break;
			case "fdec":
				result = token.getType().equals("keyword") && token.getValue().equals("def");
				break;
			case "params":
				result = first(token, "type");
				break;
			case "params_opt":
				result = token.getType().equals("terminal") && token.getValue().equals(",");
				break;
			case "fname":
				result = token.getType().equals("id");
				break;
			case "declarations":
				result = first(token, "decl");
				break;
			case "declarations_r":
				result = first(token, "decl");
				break;
			case "decl":
				result = first(token, "type");
				break;
			case "type":
				result = (token.getType().equals("keyword") && token.getValue().equals("int")) || (token.getType().equals("keyword") && token.getValue().equals("double"));
				break;
			case "varlist":
				result = first(token,"var");
				break;
			case "varlist_opt":
				result = token.getType().equals("terminal") && token.getValue().equals(",");
				break;
			case "statement_seq":
				result = first(token,"statement") || first(token,"statement_seq_opt");
				break;
			case "statement_seq_opt":
				result = token.getType().equals("terminal") && token.getValue().equals(";");
				break;
			case "statement":
				result = first(token,"var") || (token.getType().equals("keyword") && token.getValue().equals("if")) || (token.getType().equals("keyword") && token.getValue().equals("while")) || (token.getType().equals("keyword") && token.getValue().equals("print")) || (token.getType().equals("keyword") && token.getValue().equals("return"));
				break;
			case "statement_opt":
				result = (token.getType().equals("keyword") && token.getValue().equals("fi")) || (token.getType().equals("keyword") && token.getValue().equals("else"));
				break;
			case "expr":
				result = first(token,"term");
				break;
			case "expr_r":
				result = (token.getType().equals("terminal") && token.getValue().equals("+")) || (token.getType().equals("terminal") && token.getValue().equals("-"));
				break;
			case "term":
				result = first(token,"factor");
				break;
			case "term_r":
				result = (token.getType().equals("terminal") && token.getValue().equals("*")) || (token.getType().equals("terminal") && token.getValue().equals("/")) || (token.getType().equals("terminal") && token.getValue().equals("%"));
				break;
			case "factor":
				result = token.getType().equals("id") || first(token,"number") || (token.getType().equals("terminal") && token.getValue().equals("("));
				break;
			case "factor_opt":
				result = (token.getType().equals("terminal") && token.getValue().equals("(")) || (token.getType().equals("terminal") && token.getValue().equals("["));
				break;
			case "exprseq":
				result = first(token,"expr");
				break;
			case "exprseq_opt":
				result = (token.getType().equals("terminal") && token.getValue().equals(","));
				break;
			case "bexpr":
				result = first(token,"bterm");
				break;
			case "bexpr_r":
				result = (token.getType().equals("keyword") && token.getValue().equals("or"));
				break;
			case "bterm":
				result = first(token,"bfactor");
				break;
			case "bterm_r":
				result = (token.getType().equals("keyword") && token.getValue().equals("and"));
				break;
			case "bfactor":
				result = (token.getType().equals("terminal") && token.getValue().equals("(")) || (token.getType().equals("keyword") && token.getValue().equals("not"));
				break;
			case "bfactor_opt":
				result = first(token,"bexpr") || first(token,"expr");
			case "var":
				result = token.getType().equals("id");
				break;
			case "var_opt":
				result = (token.getType().equals("terminal") && token.getValue().equals("["));
				break;
			case "number":
				result = token.getType().equals("integer") || token.getType().equals("double");
				break;
			}
			
			return result; 
		}
		
		public boolean follow(Token token, String type){
			boolean result = false;
			
			switch(type){
			case "program":
				result = true;
				break;
			case "fdecls":
				result = first(token,"declarations") || first(token,"statement_seq") || (token.getType().equals("terminal") && token.getValue().equals("."));
				break;
			case "fdecls_r":
				result = follow(token,"fdecls");
				break;
			case "fdec":
				result = token.getType().equals("terminal") && token.getValue().equals(";");
				break;
			case "params":
				result = (token.getType().equals("terminal") && token.getValue().equals(")"));
				break;
			case "params_opt":
				result = follow(token,"params");
				break;
			case "fname":
				result = (token.getType().equals("terminal") && token.getValue().equals("("));
				break;
			case "declarations":
				result = first(token, "statement_seq") || (token.getType().equals("terminal") && token.getValue().equals("."));
				break;
			case "declarations_r":
				result = follow(token,"declarations");
				break;
			case "decl":
				result = (token.getType().equals("terminal") && token.getValue().equals(";"));
				break;
			case "type":
				result = first(token,"fname") || first(token,"var") || first(token,"varlist");
				break;
			case "varlist":
				result = follow(token,"decl");
				break;
			case "varlist_opt":
				result = follow(token,"varlist");
				break;
			case "statement_seq":
				result = (token.getType().equals("terminal") && token.getValue().equals(".")) || (token.getType().equals("keyword") && token.getValue().equals("fed")) || first(token,"statement_opt") || (token.getType().equals("keyword") && token.getValue().equals("od")) || (token.getType().equals("keyword") && token.getValue().equals("fi"));
				break;
			case "statement_seq_opt":
				result = follow(token,"statement_seq");
				break;
			case "statement":
				result = first(token,"statement_seq_opt") || follow(token,"statement_seq");
				break;
			case "statement_opt":
				result = follow(token,"statement");
				break;
			case "expr":
				result = follow(token,"statement") || (token.getType().equals("terminal") && token.getValue().equals(")")) || first(token,"exprseq_opt") || follow(token,"exprseq") || token.getType().equals("comp") || (token.getType().equals("terminal") && token.getValue().equals("]"));
				break;
			case "expr_r":
				result = follow(token,"expr");
				break;
			case "term":
				result = first(token,"expr_r") || follow(token,"expr");
				break;
			case "term_r":
				result = follow(token,"term");
				break;
			case "factor":
				result = first(token,"term_r") || follow(token,"term");
				break;
			case "factor_opt":
				result = follow(token,"factor");
				break;
			case "exprseq":
				result = (token.getType().equals("terminal") && token.getValue().equals(")"));
				break;
			case "exprseq_opt":
				result = follow(token,"exprseq");
				break;
			case "bexpr":
				result = (token.getType().equals("keyword") && token.getValue().equals("then")) || (token.getType().equals("keyword") && token.getValue().equals("do")) || (token.getType().equals("terminal") && token.getValue().equals(")"));
				break;
			case "bexpr_r":
				result = follow(token,"bexpr");
				break;
			case "bterm":
				result = first(token,"bexpr_r") || follow(token,"bexpr");
				break;
			case "bterm_r":
				result = follow(token,"bterm");
				break;
			case "bfactor":
				result = first(token,"bterm_r") || follow(token,"bterm");
				break;
			case "bfactor_opt":
				result = follow(token,"bfactor");
			case "var":
				result = first(token,"params_opt") || follow(token,"params") || first(token,"varlist_opt") || follow(token,"varlist") || (token.getType().equals("terminal") && token.getValue().equals("=")) || follow(token,"factor");
				break;
			case "var_opt":
				result = follow(token,"var");
				break;
			case "number":
				result = follow(token,"factor");
				break;
			}
			
			return result; 
		}
		
	}
	
	public static class Tokenizer{
		private StdIn in = new StdIn();
		
		public Tokenizer(){
			
		}
		
		public boolean ready(){
			return this.in.ready();
		}
		
		public Token nextToken(){
			Token t = new Token();
			char c;
			String state = "start";
			boolean done = false;
			while(!done){
				c = in.readChar();
				switch(state){
					case "start":	
						if(Character.isDigit(c)){
							state="integer";
							t.setValue(t.getValue()+c);
							break;
						}else if(Character.isAlphabetic(c)){
							state="id";
							t.setValue(t.getValue()+c);
							break;
						}else if("+-*/%(),.;".indexOf(c)>=0){
							t.setType("terminal");
							t.setValue(t.getValue()+c);
							done=true;
							break;
						}else if(c=='<'){
							state="lessthan";
							t.setValue(t.getValue()+c);
							break;
						}else if(c=='>'){
							state="greaterthan";
							t.setValue(t.getValue()+c);
							break;
						}else if(c=='='){
							state="equals";
							t.setValue(t.getValue()+c);
							break;
						}else if(c=='~'){
							t.setType("EOI");
							done=true;
							break;
						}else if(" \r\n\t".indexOf(c)>=0){
							t.setValue(t.getValue()+c);
							t.setType("separator");
							done=true;
							break;
						}else{
							t.setValue(t.getValue()+c);
							state="error";
							break;
						}
					case "integer":
						if(Character.isDigit(c)){
							state="integer";
							t.setValue(t.getValue()+c);
							break;
						}else if(c=='.'){
							state="decimal";
							t.setValue(t.getValue()+c);
							break;
						}else if(" \r\n\t~,;()<>=+-/*%".indexOf(c)>=0){
							in.putChar();
							t.setType("integer");
							done=true;
							break;
						}else{
							t.setValue(t.getValue()+c);
							state="error";
							break;
						}
					case "decimal":
						if(Character.isDigit(c)){
							state="double";
							t.setValue(t.getValue()+c);
							break;
						}else{
							in.putChar();
							state="error";
							break;
						}	
					case "double":
						if(Character.isDigit(c)){
							state="double";
							t.setValue(t.getValue()+c);
							break;
						}else if(" \r\n\t~,;()<>=+-/*%".indexOf(c)>=0){
							in.putChar();
							t.setType("double");
							done=true;
							break;
						}else if(c=='e'){
							state="plusminus";
							t.setValue(t.getValue()+c);
							break;
						}else{
							t.setValue(t.getValue()+c);
							state="error";
							break;
						}
					case "plusminus":
						if(Character.isDigit(c) || "+-".indexOf(c)>=0){
							state="exponent";
							t.setValue(t.getValue()+c);
							break;
						}else if(" \r\n\t~,;()<>=/*%".indexOf(c)>=0){
							in.putChar();
							t.setType("double");
							done=true;
							break;
						}else{
							t.setValue(t.getValue()+c);
							state="error";
							break;
						}
					case "exponent":
						if(Character.isDigit(c)){
							state="exponent";
							t.setValue(t.getValue()+c);
							break;
						}else if(" \r\n\t~,;()<>=+-/*%".indexOf(c)>=0){
							in.putChar();
							t.setType("exp");
							done=true;
							break;
						}else{
							t.setValue(t.getValue()+c);
							state="error";
							break;
						}
					case "id":
						if(Character.isAlphabetic(c) || Character.isDigit(c)){
							state="id";
							t.setValue(t.getValue()+c);
							break;
						}else if(" \r\n\t~,;.()<>=+-/*%".indexOf(c)>=0){
							in.putChar();
							String value = t.getValue();
							if(value.equals("def")||value.equals("fed")||value.equals("int")||value.equals("double")||value.equals("if")||value.equals("then")||value.equals("else")||value.equals("fi")||value.equals("while")||value.equals("do")||value.equals("od")||value.equals("print")||value.equals("return")||value.equals("or")||value.equals("and")||value.equals("not")){
								t.setType("keyword");
							}else{
								t.setType("id");
							}
							done=true;
							break;
						}else{
							t.setValue(t.getValue()+c);
							state="error";
							break;
						}
					case "lessthan":
						if(">=".indexOf(c)>=0){
							t.setValue(t.getValue()+c);
							t.setType("comp");
							done=true;
							break;
						}else if(" \r\n\t~,;.".indexOf(c)>=0 || Character.isAlphabetic(c) || Character.isDigit(c)){
							in.putChar();
							t.setType("comp");
							done=true;
							break;
						}else{
							t.setValue(t.getValue()+c);
							state="error";
							break;
						}
					case "greaterthan":
						if(c=='='){
							t.setValue(t.getValue()+c);
							t.setType("comp");
							done=true;
							break;
						}else if(" \r\n\t~,;.".indexOf(c)>=0 || Character.isAlphabetic(c) || Character.isDigit(c)){
							in.putChar();
							t.setType("comp");
							done=true;
							break;
						}else{
							t.setValue(t.getValue()+c);
							state="error";
							break;
						}
					case "equals":
						if(c=='='){
							t.setValue(t.getValue()+c);
							t.setType("comp");
							done=true;
							break;
						}else if(" \r\n\t~,;.".indexOf(c)>=0 || Character.isAlphabetic(c) || Character.isDigit(c)){
							in.putChar();
							t.setType("terminal");
							done=true;
							break;
						}else{
							t.setValue(t.getValue()+c);
							state="error";
							break;
						}
					case "error":
						if(" \r\n\t~+-*/%(),.;".indexOf(c)>=0){
							in.putChar();
							t.setType("error");
							done=true;
							break;
						}else{
							t.setValue(t.getValue()+c);
							state="error";
							break;
						}
				}
			}
			return t;
		}
	}
	
	public static class Token{
		private String type;
		private String value;
		
		public Token(){
			this.type="";
			this.value="";
		}
		
		public Token(String type, String value){
			this.type = type;
			this.value = value;
		}
		
		public void setType(String type){
			this.type = type;
		}
		
		public String getType(){
			return this.type;
		}
		
		public void setValue(String value){
			this.value = value;
		}
		
		public String getValue(){
			return this.value;
		}
	}
	
	public static class SymbolTables{
		private ArrayList<SymbolTable> tables = new ArrayList<SymbolTable>();
	
		public int size(){
			return tables.size();
		}
		
		public void addTable(String name){
			SymbolTable newTable = new SymbolTable(name);
			tables.add(newTable);
		}
		
		public void printHTMLTables(){
			SymbolTable thisTable;
			System.out.print("<table><tr><th colspan='100%'>Symbol Tables</th></tr><tr>");
			
			for(int i=0; i<this.size(); i++){
				thisTable = tables.get(i);
				System.out.print("<td valign='top'>");
				thisTable.printHTMLTable();
				System.out.println("</td>");
			}
			
			System.out.print("</tr></table>");
		}
		
		public void renameTopTable(String name){
			SymbolTable topTable = tables.get(this.size()-1);
			topTable.setName(name);
		}
		
		public void swapTopTables(){
			if(this.size()>=2){
				SymbolTable topTable = tables.get(this.size()-1);
				SymbolTable secondTable = tables.get(this.size()-2);
				
				tables.remove(this.size()-1);
				tables.remove(this.size()-1);
				
				tables.add(topTable);
				tables.add(secondTable);
			}
		}
		
		public void addSymbolToTop(String type, String name, String value){
			SymbolTable topTable = tables.get(this.size()-1);
			topTable.addSymbol(type, name, value);
		}
	}
	
	public static class SymbolTable{
		private String name;
		
		private ArrayList<Symbol> symbols = new ArrayList<Symbol>();
		
		public SymbolTable(){
			this.name = "";
		}
		
		public SymbolTable(String n){
			this.name = n;
		}
		
		public String getName(){
			return this.name;
		}
		
		public void setName(String n){
			this.name = n;
		}
		
		public int size(){
			return symbols.size();
		}
		
		public void addSymbol(String type, String name, String value){
			Symbol newSymbol = new Symbol(type, name, value);
			if(!symbols.contains(newSymbol)){
				symbols.add(newSymbol);
			}
		}
		
		public void printHTMLTable(){
			System.out.print(	"<table style='text-align:center;border:1px solid #E6E6E6;'>"+
								"<tr><th colspan='3'>"+this.name+"</th></tr>"+
								"<tr><th>Type</th>"+
								"<th>Name</th>"+
								"<th>Value</th></tr>");
			
			Symbol thisSymbol;
			for(int i=0; i<this.size(); i++){
				thisSymbol = symbols.get(i);
				System.out.print(	
									"<tr>"+
									"<td>"+thisSymbol.getType()+"</td>"+
									"<td>"+thisSymbol.getName()+"</td>"+
									"<td>"+thisSymbol.getValue()+"</td>"+
									"</tr>");
			}
			
			System.out.print("</table>");
		}
	}
	
	public static class Symbol{
		String type;
		String name;
		String value;
		
		public Symbol(){
			this.type="";
			this.name="";
			this.value="";
		}
		
		public Symbol(String t, String n, String v){
			this.type = t;
			this.name = n;
			this.value = v;
		}
		
		public void setType(String t){
			this.type = t;
		}
		
		public void setName(String n){
			this.name = n;
		}
		
		public void setValue(String v){
			this.value = v;
		}
		
		public String getType(){
			return this.type;
		}
		
		public String getName(){
			return this.name;
		}
		
		public String getValue(){
			return this.value;
		}
		
		@Override
		public boolean  equals (Object object) {
			boolean result = false;
			if (object == null || object.getClass() != getClass()) {
			    result = false;
			} else {
			    Symbol symbol = (Symbol) object;
			    if (this.name == symbol.getName() && this.type == symbol.getType()   && this.value == symbol.getValue()) {
			        result = true;
			    }
			}
			return result;
		}
	}
	
	public static class StdIn {

		BufferedReader r;
		
		public StdIn(){
			r = new BufferedReader(new InputStreamReader(System.in));
		}
		
		
		public char readChar(){
			try{
				r.mark(1);
				int i=0;
				if((i=r.read())==-1){
					return '~';
				}else{
					return (char)i;
				}
			}catch(IOException io){
				io.printStackTrace();
				return '~';
			}
		}
		
		public void putChar(){
			try{
				r.reset();
			}catch(IOException io){
				io.printStackTrace();
			}
		}
		
		public boolean ready(){
			try {
				return r.ready();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
}
