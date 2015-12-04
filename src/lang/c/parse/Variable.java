package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Variable extends CParseRule {
	// Variable ::= ident [ array | call]
	private CParseRule ident;
	private CToken i;
	private CParseRule array;
	private CParseRule call;
	public Variable(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Ident.isFirst(tk)){
			 i = tk;
			ident = new Ident(pcx);
			ident.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if(Array.isFirst(tk)){
			array = new Array(pcx);
			array.parse(pcx);
		}else if(Call.isFirst(tk)){
			call = new Call(pcx, i);
			call.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (array == null) {
			ident.semanticCheck(pcx);
			this.setCType(ident.getCType());
			this.setConstant(ident.isConstant());
		}else {
			ident.semanticCheck(pcx);
			array.semanticCheck(pcx);
			if((ident.getCType() == CType.getCType(CType.T_pint) || ident.getCType() == CType.getCType(CType.T_pint_a))
					&& (array.getCType() == CType.getCType(CType.T_int))){
				this.setCType(CType.getCType(CType.T_pint));
				this.setConstant(ident.isConstant());
			}else if(ident.getCType() == CType.getCType(CType.T_int_a) && (array.getCType() == CType.getCType(CType.T_int))){
				this.setCType(CType.getCType(CType.T_int));
				this.setConstant(ident.isConstant());
			}else {
				pcx.fatalError("irrigal type of variable(array)" + ident.getCType() +".");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; variable starts");
		if (ident != null) {
			ident.codeGen(pcx);
		}if (array != null) {
			array.codeGen(pcx);
		}else if(call != null){
			call.codeGen(pcx);
		}
		o.println(";;; variable completes");
	}
}
