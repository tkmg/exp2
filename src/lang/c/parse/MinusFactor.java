package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class MinusFactor extends CParseRule {
	// uFactorAmp ::= MINUS num
	private CToken minus;
	private CParseRule uFactor;
	public MinusFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		minus = tk;
		tk = ct.getNextToken(pcx);
		if(tk.getType() == CToken.TK_AMP ){
			pcx.fatalError("irrigal token");
		}
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
		o.println(";;; minus factor starts");
		if (minus != null && uFactor != null) {
			uFactor.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; MinusFactor: 数を取り出して、負にして、積む<" + minus.toExplainString() + ">");
			o.println("\tMOV\t#0, R1   \t; MinusFactor:");
			o.println("\tSUB\tR0, R1   \t; MinusFactor:");
			o.println("\tMOV\tR1, (R6)+\t; MinusFactor:");
		}
		o.println(";;; minus factor completes");
	}
}
