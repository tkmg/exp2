package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class ConstDecl extends CParseRule {
	// Variable ::= ident [ array ]

	private ArrayList<CParseRule> constItem;

	public ConstDecl(CParseContext pcx) {
		constItem = new ArrayList<CParseRule>();
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CONST;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_CONST){
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\"CONST\" is not exists.");
		}
		if(tk.getType() == CToken.TK_INT){
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\"INT\" is not exists.");
		}
		if(ConstItem.isFirst(tk)){
			constItem.add(new ConstItem(pcx));
			constItem.get(0).parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\"constItem\" is not exists.");
		}
		int i = 1;
		while(true){
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() == CToken.TK_COMMA){
				tk = ct.getNextToken(pcx);
				if(ConstItem.isFirst(tk)){
					constItem.add(new ConstItem(pcx));
					constItem.get(i).parse(pcx);
					i++;
				}else {
					pcx.fatalError(tk.toExplainString() + "\"constItem"+ i +"\" is not exists");
				}
			}else {
				break;
			}
		}
		if(tk.getType() == CToken.TK_SEMI){
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\";\"(SEMI) is not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		for(int i = 0; i < constItem.size(); i ++){
			constItem.get(i).semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; const decl starts");
		for(CParseRule r : constItem){
			r.codeGen(pcx);
		}
		o.println(";;; const decl completes");
	}
}
