package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Statement extends CParseRule {
	// addressToValue ::= statement | if | loop | in | out
	private CParseRule statement;
	private CToken func;
	public Statement(CParseContext pcx, CToken func) {
		this.func = func;
	}
	public static boolean isFirst(CToken tk) {
		return StatementAssign.isFirst(tk) || IfStatement.isFirst(tk) || LoopStatement.isFirst(tk)
				|| InputStatement.isFirst(tk) || OutputStatement.isFirst(tk) || StatementCall.isFirst(tk)
				|| StatementReturn.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(StatementAssign.isFirst(tk)){
			statement = new StatementAssign(pcx);
		}else if(IfStatement.isFirst(tk)){
			statement = new IfStatement(pcx);
		}else if(LoopStatement.isFirst(tk)){
			statement = new LoopStatement(pcx);
		}else if(InputStatement.isFirst(tk)){
			statement = new InputStatement(pcx);
		}else if(OutputStatement.isFirst(tk)){
			statement = new OutputStatement(pcx);
		}else if(StatementCall.isFirst(tk)){
			statement = new StatementCall(pcx);
		}else if(StatementReturn.isFirst(tk)){
			statement = new StatementReturn(pcx, func);
		}
		statement.parse(pcx);
		tk = ct.getCurrentToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statement != null) {
			statement.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statement starts");
		if (statement != null) {
			statement.codeGen(pcx);
		}
		o.println(";;; statement completes");
	}
}
