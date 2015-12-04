package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class IntDecl extends CParseRule {
	// Variable ::= ident [ array ]

	private ArrayList<CParseRule> declItem;

	public IntDecl(CParseContext pcx) {
		declItem = new ArrayList<CParseRule>();
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_INT){
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\"INT\" is not exists.");
		}
		if(DeclItem.isFirst(tk)){
			declItem.add(new DeclItem(pcx));
			declItem.get(0).parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\"declItem\" is not exists.");
		}
		int i = 1;
		while(true){
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_COMMA){
				tk = ct.getNextToken(pcx);
				if(DeclItem.isFirst(tk)){
					declItem.add(new DeclItem(pcx));
					declItem.get(i).parse(pcx);
					i++;
				}else {
					pcx.fatalError(tk.toExplainString() + "\"declItem"+ i +"\" is not exists");
				}
			}else {
				break;
			}
		}
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_SEMI){
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\";\"(SEMI) is not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		for(int i = 0; i < declItem.size(); i ++){
			declItem.get(i).semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; declItem starts");
		for(CParseRule r : declItem){
			r.codeGen(pcx);
		}
		o.println(";;; int decl completes");
	}
}
