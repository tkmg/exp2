package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class IfStatement extends CParseRule {
	// StatementAssign ::= if [else]
	//private CToken assign;
	private CParseRule ifPart;
	private CParseRule elsePart;

	public IfStatement(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return IfPart.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(IfPart.isFirst(tk)){
			ifPart = new IfPart(pcx);
			ifPart.parse(pcx);
			tk = ct.getCurrentToken(pcx);
		}
		if (ElsePart.isFirst(tk)) {
			elsePart  = new ElsePart(pcx);
			elsePart.parse(pcx);
			tk = ct.getCurrentToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(ifPart != null){
			ifPart.semanticCheck(pcx);
		}
		if(elsePart != null){
			elsePart.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		int seq2 = pcx.getSeq();
		int seq = pcx.getSeqId();
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; if statement starts");
		pcx.setSeq(seq);
		if (ifPart != null) {
			ifPart.codeGen(pcx);
		}
		if (elsePart != null) {
			elsePart.codeGen(pcx);
		}
		o.println("IF" + seq + ":\t\t\t\t;if statement");
		o.println(";;; if statement completes");
		pcx.setSeq(seq2);
	}
}
