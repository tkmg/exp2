package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class UnsignedFactor extends CParseRule {
	// unsignedFactor ::= factorAMP | number | LPAR expression RPAR
	private CParseRule unsignedFactor;

	public UnsignedFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Number.isFirst(tk) || FactorAmp.isFirst(tk) || tk.getType() == CToken.TK_LPAR || AddressToValue.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Number.isFirst(tk)) {
			unsignedFactor = new Number(pcx);
			unsignedFactor.parse(pcx);
		} else if (FactorAmp.isFirst(tk)) {
			unsignedFactor = new FactorAmp(pcx);
			unsignedFactor.parse(pcx);
		}else if(tk.getType() == CToken.TK_LPAR){
			tk = ct.getNextToken(pcx);
			if(Expression.isFirst(tk)){
				unsignedFactor = new Expression(pcx);
				unsignedFactor.parse(pcx);
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() != CToken.TK_RPAR){
					pcx.fatalError(tk.toExplainString() + "\")\" is not exist.");
				}
				tk = ct.getNextToken(pcx);
			}else {
				pcx.fatalError(tk.toExplainString() + "\"expression\" is not exist.");
			}
		}else if(AddressToValue.isFirst(tk)){
			unsignedFactor = new AddressToValue(pcx);
			unsignedFactor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unsignedFactor != null) {
			unsignedFactor.semanticCheck(pcx);
			this.setCType(unsignedFactor.getCType());
			this.setConstant(unsignedFactor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; unsigned factor starts");
		if (unsignedFactor != null) {
			unsignedFactor.codeGen(pcx);
		}
		o.println(";;; unsigned factor completes");
	}
}