package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class VoidDecl extends CParseRule {
	// VoidDecl ::= VOID IDENT LPAR RPAR {COMMA IDENT LPAR RPAR} SEMI
	private ArrayList<CToken> ident;

	public VoidDecl(CParseContext pcx) {
		ident = new ArrayList<CToken>();
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_VOID;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if (Ident.isFirst(tk)) {
			ident.add(tk);
			tk = ct.getNextToken(pcx);
			if (tk.getType() == CToken.TK_LPAR) {
				tk = ct.getNextToken(pcx);
				if (tk.getType() == CToken.TK_RPAR) {
					tk = ct.getNextToken(pcx);
				} else {
					pcx.fatalError(tk.toExplainString() + "\"(\" (RPAR) does not exsist(void decl part).");
				}
			} else {
				pcx.fatalError(tk.toExplainString() + "\"(\" (LPAR) does not exsist(void decl part).");
			}
		} else {
			pcx.fatalError(tk.toExplainString() + "\"ident\" does not exsist(void decl part).");
		}
		while (true) {
			if (tk.getType() == CToken.TK_COMMA) {
				tk = ct.getNextToken(pcx);
				if (Ident.isFirst(tk)) {
					ident.add(tk);
					tk = ct.getNextToken(pcx);
					if (tk.getType() == CToken.TK_LPAR) {
						tk = ct.getNextToken(pcx);
						if (tk.getType() == CToken.TK_RPAR) {
							tk = ct.getNextToken(pcx);
						} else {
							pcx.fatalError(tk.toExplainString() + "\"(\" (RPAR) does not exsist(void decl part).");
							break;
						}
					} else {
						pcx.fatalError(tk.toExplainString() + "\"(\" (LPAR) does not exsist(void decl part).");
						break;
					}
				} else {
					pcx.fatalError(tk.toExplainString() + "\"ident\" does not exsist(void decl part).");
					break;
				}
			} else {
				break;
			}
		}
		if (tk.getType() == CToken.TK_SEMI) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "\";\" (SEMI) does not exsist(void decl part).");
		}
		for(CToken t : ident){
			System.out.println(t.getText() + ":void");
			pcx.getTable().register(t.getText(), false, CType.getCType(CType.T_void), 1, pcx.getSeqPlus());
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException{
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; const item starts");
		for(CToken t : ident){
		String a = t.getText();
		CType type = pcx.getTable().search(a).getType();
		if (pcx.getTable().search(a).getSize() > 0) {
			if (type == CType.getCType(CType.T_int) || type == CType.getCType(CType.T_pint)) {
				if (pcx.getTable().search(a).getConstp()) {
					o.println(a + ":\t.WORD\t" + pcx.getTable().search(a).getAddress());
				} else {
					o.println(a + ":\t.WORD\t0");
				}
			} else if (type == CType.getCType(CType.T_int_a) || type == CType.getCType(CType.T_pint_a)) {
				o.println(a + ":\t.BLKW\t" + pcx.getTable().search(a).getSize());
			} else if (type == CType.getCType(CType.T_void)) {
				o.println(a + ":\t.WORD\t0");
			}
		}
		}
		o.println(";;; const item completes");
	}
}
