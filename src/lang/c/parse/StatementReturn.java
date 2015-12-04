package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class StatementReturn extends CParseRule {
	// StatementReturn ::= primaryMult || variable
	private CParseRule expression;
	private CToken func;
	public StatementReturn(CParseContext pcx, CToken func) {
		this.func = func;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_RETURN;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)){
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
		}
		if(tk.getType() == CToken.TK_SEMI){
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\";\" does not exsist (statementReturn part).");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
			this.setCType(expression.getCType());
			this.setConstant(expression.isConstant());
		}
		CType t = pcx.getTable().search(func.getText()).getType();
		if((expression != null && expression.getCType() != t) || (expression == null && t != CType.getCType(CType.T_void))){
			pcx.fatalError("irregal return value " + t + "(func), "+ expression.getCType() + "(return value).");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statement return starts");
		if (expression != null) {
			expression.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6)\t,R0\t; statement return : スタックから返却値を取り出しておく");
		o.println("\tMOV\tR4\t,R6\t; statement return : スタックポインタの復旧");
		o.println("\tMOV\tRET_" + func.getText() + "\t\t; statement return : 関数の最後へ移動");
		o.println(";;; statement return completes");
	}
}
