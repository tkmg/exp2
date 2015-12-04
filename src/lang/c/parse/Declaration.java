package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Declaration extends CParseRule {
	// Variable ::= ident [ array ]

	private CParseRule declaration;

	public Declaration(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk) || VoidDecl.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(IntDecl.isFirst(tk)){
			declaration = new IntDecl(pcx);
		}else if(ConstDecl.isFirst(tk)){
			declaration = new ConstDecl(pcx);
		}else if(VoidDecl.isFirst(tk)){
			declaration = new VoidDecl(pcx);
		}else {
			pcx.fatalError("\"intDecl\" , \"constDecl\" or \"voidDecl\" do not exist.");
		}
		declaration.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		declaration.semanticCheck(pcx);
		this.setConstant(declaration.isConstant());
		this.setCType(declaration.getCType());

	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
//		PrintStream o = pcx.getIOContext().getOutStream();
//		o.println(";;; declaration starts");
//		if(declaration != null){
//			declaration.codeGen(pcx);
//		}
//		o.println(";;; declaration completes");
	}
}
