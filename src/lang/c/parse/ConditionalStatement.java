package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class ConditionalStatement extends CParseRule {
	// ConditionalStatement ::= LPAR condition RPAR
	//private CToken assign;
	private CParseRule condition;

	public ConditionalStatement(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LPAR;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(Condition.isFirst(tk)){
			condition = new Condition(pcx);
			condition.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_RPAR){
				tk = ct.getNextToken(pcx);
			}else {
				pcx.fatalError(tk.toExplainString() + "\")\"(RPAR) does not exists.");
			}
		}else {
			pcx.fatalError(tk.toExplainString() + "\"condition\" does not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(condition != null){
			condition.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statement assign starts");
		if(condition != null){
			condition.codeGen(pcx);
		}
		o.println(";;; condition completes");
	}
}
