package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class StatementCall extends CParseRule {
	// StatementCall ::= CALL ident LPAR RPAR SEMI;
	private CParseRule ident;
	private CToken i;
	public StatementCall(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CALL;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(Ident.isFirst(tk)){
			ident = new Ident(pcx);
			ident.parse(pcx);
			i = tk;
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_LPAR){
				tk = ct.getNextToken(pcx);
				if(tk.getType() == CToken.TK_RPAR){
					tk = ct.getNextToken(pcx);
					if(tk.getType() == CToken.TK_SEMI){
						tk = ct.getNextToken(pcx);
					}else {
						pcx.fatalError(tk.toExplainString() + "\";\" (SEMI) does not exsist(statement call part).");
					}
				}else {
					pcx.fatalError(tk.toExplainString() + "\")\" (RPAR) does not exsist(statement call part).");
				}
			}else {
				pcx.fatalError(tk.toExplainString() + "\"(\" (LPAR) does not exsist(statement call part).");
			}
		}else {
			pcx.fatalError(tk.toExplainString() + "\"ident\" does not exsist(statement call part).");
		}
		int a = pcx.getSeqPlus();
		pcx.getTable().register(i.getText() + "_a", false, CType.getCType(CType.T_int), 1, a);
		System.out.println(a + ":func");
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			ident.semanticCheck(pcx);
			this.setCType(ident.getCType());
			this.setConstant(ident.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statement call starts");
		o.println("\tMOV\tR7\t,(R6)+\t; call : 戻り番地をスタックに積む");
		o.println("\tJSR\t"+ i.getText() +"\t\t; call : 関数呼び出し");
		//o.println("\tMOV\tR0\t,R1\t; call : 返却値を指定の関数の場所に積み直す");
		if (ident != null) {

			ident.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6)\t,R0\t; call : スタックを戻しておく");
		o.println("\tCLR\tR0\t\t; call : スタックを戻しておく");
		o.println(";;; statement call completes");
	}
}
