package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class ElsePart extends CParseRule {
	// StatementAssign ::= if [else]
	//private CToken assign;
	private CParseRule ifStatement;
	private CParseRule contents;

	public ElsePart(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_ELSE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if (IfStatement.isFirst(tk)) {
			ifStatement = new IfStatement(pcx);
			ifStatement.parse(pcx);
			tk = ct.getCurrentToken(pcx);
		} else if (Contents.isFirst(tk)) {
			contents = new Contents(pcx);
			contents.parse(pcx);
			tk = ct.getCurrentToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "\"IfStatement or Contents\" does not exists.");
		}

	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(ifStatement != null){
			ifStatement.semanticCheck(pcx);
		}
		if(contents != null){
			contents.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; else part starts");
		if (ifStatement != null) {
			ifStatement.codeGen(pcx);
		}
		if (contents != null) {
			contents.codeGen(pcx);
		}
		o.println(";;; else part completes");
	}
}
