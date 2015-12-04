package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Array extends CParseRule {
	// array ::= LBAR expression RBAR
	private CParseRule array;
	public Array(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LBAR;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_LBAR){
			tk = ct.getNextToken(pcx);
			if(Expression.isFirst(tk)){
				array = new Expression(pcx);
				array.parse(pcx);
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() != CToken.TK_RBAR){
					pcx.fatalError("\"]\" does not exist.");
				}
				tk = ct.getNextToken(pcx);
			}else {
				pcx.fatalError("\"expression\" does not exist.");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (array != null) {
			array.semanticCheck(pcx);
			this.setCType(array.getCType());
			this.setConstant(array.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; array starts");
		if (array != null) {
			array.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t;address");
			o.println("\tMOV\t-(R6), R1\t;address");
			o.println("\tADD\tR1, R0   \t;address");
			o.println("\tMOV\tR0, (R6)+\t;address");
		}
		o.println(";;; array completes");
	}
}
