package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementAssign extends CParseRule {
	// StatementAssign ::= primary ASSGN expression SEMI
	//private CToken assign;
	private CParseRule primary;
	private CParseRule expression;
	private CToken pri;
	public StatementAssign(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		pri = tk;
		primary = new Primary(pcx);
		primary.parse(pcx);
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_ASSIGN){
			pcx.fatalError(tk.toExplainString() + "\"=\" is not exist.");
		}
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)){
			expression = new Expression(pcx);
			expression.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\"expression\" does not exist.");
		}
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() != CToken.TK_SEMI){
			pcx.fatalError(tk.toExplainString() + "\";\" does not exist.");
		}
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null && expression != null) {
			primary.semanticCheck(pcx);
			expression.semanticCheck(pcx);
			if(primary.isConstant()){
				pcx.fatalError(pri.toExplainString() + "Irregal substitution, for constant.");
			}if(primary.getCType() != expression.getCType()){
				pcx.fatalError(pri.toExplainString() + "Irregal substitution, " + primary.getCType() + "(variable) for " + expression.getCType() + "(value).");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statement assign starts");
		if (primary != null) {
			primary.codeGen(pcx);
		}
		if (expression != null) {
			expression.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6)\t, R0\t;statement assign");
		o.println("\tMOV\t-(R6)\t, R1\t;statement assign");
		o.println("\tMOV\tR0\t, (R1)\t;statement assign");
		o.println(";;; statement assign completes");
	}
}
