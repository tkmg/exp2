package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Contents extends CParseRule {
	//Contents ::= LCUR {statement} RCUR
	//private CToken assign;
	private CParseRule s;
	private ArrayList<CParseRule> statement;

	public Contents(CParseContext pcx) {
		statement = new ArrayList<CParseRule>();
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LCUR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		while (true) {
			if (Statement.isFirst(tk)) {
				s = new Statement(pcx, null);
				s.parse(pcx);
				statement.add(s);
				tk = ct.getCurrentToken(pcx);
			} else {
				break;
			}
		}
		if (tk.getType() == CToken.TK_RCUR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "\"}\"(RCUR) does not exists.");
		}

	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		for (CParseRule p : statement) {
			p.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; contents starts");
		if(!statement.isEmpty()){
			for (CParseRule p : statement) {
				p.codeGen(pcx);
			}
		}
		o.println(";;; contents completes");
	}
}
