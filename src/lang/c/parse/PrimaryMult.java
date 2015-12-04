package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class PrimaryMult extends CParseRule {
	// primaryMult ::= MULT primary
	private CToken mult;
	private CParseRule variable;
	public PrimaryMult(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MUL;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		mult = tk;
		tk = ct.getNextToken(pcx);
		if(Variable.isFirst(tk)){
			variable = new Variable(pcx);
			variable.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		variable.semanticCheck(pcx);
		if(variable.getCType() == CType.getCType(CType.T_pint) || variable.getCType() == CType.getCType(CType.T_pint_a)){
			this.setCType(variable.getCType());
			this.setConstant(variable.isConstant());
		}else {
			pcx.fatalError("irreagal type of variable(" + variable.getCType() + ").");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary mult starts");
		if (mult != null && variable != null) {
			variable.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; PrimaryMult: アドレスを取り出して内容を参照して、積む<" + mult.toExplainString() + ">");
			o.println("\tMOV\t(R0), (R6)+\t; PrimaryMult:");
		}
		o.println(";;; primary mult completes");
	}
}
