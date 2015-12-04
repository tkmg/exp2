package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class InputStatement extends CParseRule {
	// StatementAssign ::= INPUT ident SEMI
	private CParseRule ident;
	public InputStatement(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INPUT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(Ident.isFirst(tk)){
			ident = new Ident(pcx);
			ident.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_SEMI){
				tk = ct.getNextToken(pcx);
			}else {
				pcx.fatalError(tk.toExplainString() + "\";\"(SEMI) does not exists.");
			}
		}else {
			pcx.fatalError(tk.toExplainString() + "\"ident\" does not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(ident != null){
			ident.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (ident != null) {
			ident.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6)\t, R0\t;input statement:0xFFE0の値を変数に入れる");
		o.println("\tMOV\t#0xFFE0\t, R1\t;input statement");
		o.println("\tMOV\t(R1)\t, (R0)\t;input statement");
		o.println(";;; input statement completes");
	}
}
