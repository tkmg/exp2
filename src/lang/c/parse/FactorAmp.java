package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class FactorAmp extends CParseRule {
	// factorAmp ::= AMP ( num | primary )
	private CToken amp;
	private CParseRule factorAmp;
	public FactorAmp(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		amp = tk;
		tk = ct.getNextToken(pcx);
		if(Number.isFirst(tk)){
			factorAmp = new Number(pcx);
			factorAmp.parse(pcx);
		}else if(Primary.isFirst(tk)){
			factorAmp = new Primary(pcx);
			factorAmp.parse(pcx);
		}
	}


	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factorAmp != null) {
			factorAmp.semanticCheck(pcx);
		}
		this.setCType(CType.getCType(CType.T_pint));
		this.setConstant(true);
		if(factorAmp instanceof PrimaryMult){
			pcx.fatalError("irregal variable exsists.");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor amp starts");
		if (amp != null && factorAmp != null) {
			factorAmp.codeGen(pcx);
		}
		o.println(";;; factor amp completes");
	}
}
