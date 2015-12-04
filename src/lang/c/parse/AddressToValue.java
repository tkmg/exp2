package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class AddressToValue extends CParseRule {
	// addressToValue ::= primary
	private CToken pri;
	private CParseRule primary;
	public AddressToValue(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Primary.isFirst(tk)){
			pri = tk;
			primary = new Primary(pcx);
			primary.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
			setCType(primary.getCType());
			setConstant(primary.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; address to value starts");
		if (primary != null) {
			primary.codeGen(pcx);
			o.println("\tMOV\t-(R6),   R0\t; AddressToValue:");
			o.println("\tMOV\t(R0) , (R6)+\t; AddressToValue: 値を指定番地から取り出して積む<" + pri.toExplainString() + ">");
		}
		o.println(";;; address to value completes");
	}
}
