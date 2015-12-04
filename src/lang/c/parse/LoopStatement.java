package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class LoopStatement extends CParseRule {
	// LoopStatement ::= while | doWhile
	private CParseRule loopStatement;

	public LoopStatement(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return WhileStatement.isFirst(tk) || DoWhileStatement.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(WhileStatement.isFirst(tk)){
			loopStatement = new WhileStatement(pcx);
		}else if(DoWhileStatement.isFirst(tk)){
			loopStatement = new DoWhileStatement(pcx);
		}
		loopStatement.parse(pcx);
		tk = ct.getCurrentToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(loopStatement != null){
			loopStatement.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; loop statement completes");
		if(loopStatement != null){
			loopStatement.codeGen(pcx);
		}
		o.println(";;; loop statement completes");
	}
}
