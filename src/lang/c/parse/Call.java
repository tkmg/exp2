package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Call extends CParseRule {
	// Call ::= LRAR RPAR
	private CToken par;
	private CToken func;
	public Call(CParseContext pcx, CToken func) {
		this.func = func;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(tk.getType() == CToken.TK_RPAR){
			par = tk;
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\")\" (RPAR) does not exsist (call part).");
		}
		int a = pcx.getSeqPlus();
		pcx.getTable().register(func.getText() + "_a", false, CType.getCType(CType.T_int), 1, a);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {

	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; call starts");
		if (par != null) {
			o.println("\tMOV\tR7\t,(R6)+\t; call : 戻り番地をスタックに積む");
			o.println("\tJSR\t"+ func.getText() +"\t\t; call : 関数呼び出し");
			o.println("\tMOV\t-(R6)\t, R1\t; call: アドレスを取り出す");
			o.println("\tMOV\tR0\t, (R1)\t; call: 返り値を入れる");
			o.println("\tMOV\tR1\t, (R6)+\t; call: 変数のアドレスを積む");
		}
		o.println(";;; call completes");
	}
}
