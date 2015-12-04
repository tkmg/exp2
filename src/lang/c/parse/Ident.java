package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.SymbolTable;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Ident extends CParseRule {
	// Ident ::= IDENT

	private CToken ident;
	private CSymbolTableEntry symbol;
	private SymbolTable<CSymbolTableEntry> l;
	public Ident(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ident = tk;
		l = pcx.getTable().getLocal();
		symbol = pcx.getTable().search(tk.getText());
		if(symbol == null){
			pcx.fatalError(tk.toExplainString() + "this is not a declared variable \"" + tk.getText() + "\".");
		}
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		this.setCType(symbol.getType());
		this.setConstant(symbol.getConstp());
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; ident("+ ident.getText() + ") starts");
		if (ident != null) {
			if(l.search(ident.getText() + "_a") != null || !symbol.getIsGlobal()){
				o.println("\tMOV\t#" + symbol.getAddress()  + "\t, R0\t; Ident: ローカル変数だから相対アドレスを計算させる");
				o.println("\tADD\tR4\t, R0\t; Ident: ローカル変数だから相対アドレスを計算させる");
			}else {
				o.println("\tMOV\t#" + ident.getText() + "\t, R0\t; Ident: 変数アドレスを保持<" + ident.toExplainString() + ">");
			}
			o.println("\tMOV\tR0\t, (R6)+\t; Ident: アドレスを積む");
		}
		o.println(";;; ident completes");
	}
}
