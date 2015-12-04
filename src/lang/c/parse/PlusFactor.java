package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class PlusFactor extends CParseRule {
	// uFactorAmp ::= PLUS num
	private CToken plus;
	private CParseRule uFactor;
	public PlusFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		plus = tk;
		tk = ct.getNextToken(pcx);
		if(UnsignedFactor.isFirst(tk)){
			uFactor = new UnsignedFactor(pcx);
			uFactor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (uFactor != null) {
			uFactor.semanticCheck(pcx);
			this.setCType(uFactor.getCType());
			this.setConstant(uFactor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; plus factor starts");
		if (plus != null && uFactor != null) {
			uFactor.codeGen(pcx);
		}
		o.println(";;; plus factor completes");
	}
}
