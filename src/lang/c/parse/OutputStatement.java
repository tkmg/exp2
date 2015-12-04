package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class OutputStatement extends CParseRule {
	// StatementAssign ::= OUTPUT expression SEMI
	//private CToken assign;
	private CParseRule expression;
	public OutputStatement(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_OUTPUT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)){
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_SEMI){
				tk = ct.getNextToken(pcx);
			}else {
				pcx.fatalError(tk.toExplainString() + "\";\"(SEMI) does not exists.");
			}
		}else {
			pcx.fatalError(tk.toExplainString() + "\"expression\" does not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(expression != null){
			expression.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; output statement starts");
		if (expression != null) {
			expression.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6)\t, R0\t;output statement:0xFFE0に値を入れる");
		o.println("\tMOV\t#0xFFE0\t,R1\t;output statement");
		o.println("\tMOV\tR0\t, (R1)\t;output statement");
		o.println(";;; output statement completes");
	}
}
