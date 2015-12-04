package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class WhileStatement extends CParseRule {
	// StatementAssign ::= if [else]
	//private CToken assign;
	private CParseRule conditionalStatement;
	private CParseRule contents;

	public WhileStatement(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_WHILE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(ConditionalStatement.isFirst(tk)){
			conditionalStatement = new ConditionalStatement(pcx);
			conditionalStatement.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(Contents.isFirst(tk)){
				contents = new Contents(pcx);
				contents.parse(pcx);
				tk = ct.getCurrentToken(pcx);
			}else {
				pcx.fatalError(tk.toExplainString() + "\"contents\" does not exists.");
			}
		}else {
			pcx.fatalError(tk.toExplainString() + "\"conditionalStatement\" does not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(conditionalStatement != null){
			conditionalStatement.semanticCheck(pcx);
		}if(contents != null){
			contents.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		int seq = pcx.getSeqId();
		int seq2 = pcx.getSeqId();
		o.println(";;; while statement starts");
		o.println("WHILE" + seq + ":\t\t\t;while statement");
		if(conditionalStatement != null){
			conditionalStatement.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6)\t, R0\t;while statement");
		o.println("\tCMP\t0x0000\t, R0\t;while statement");
		o.println("\tBRZ\tWHILE"+ seq2 +"\t\t;while statement");
		if(contents != null){
			contents.codeGen(pcx);
		}
		o.println("\tJMP\tWHILE" + seq + "\t\t;while statement");
		o.println("WHILE" + seq2 + ":\t\t\t;while statement");
		o.println(";;; while statement completes");
	}
}
