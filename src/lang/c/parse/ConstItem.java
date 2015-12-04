package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class ConstItem extends CParseRule {
	// Variable ::= ident [ array ]
	private CToken mult;
	private CToken ident;
	private CToken amp;
	private CToken num;
	public ConstItem(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MUL || Ident.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_MUL){
			mult = tk;
			tk = ct.getNextToken(pcx);
		}
		if(Ident.isFirst(tk)){
			ident = tk;
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\"IDENT\" does not exists.");
		}
		if(tk.getType() == CToken.TK_ASSIGN){
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\"=\" (ASSIGN) does not exsits.");
		}
		if(tk.getType() == CToken.TK_AMP){
			amp = tk;
			tk = ct.getNextToken(pcx);
		}
		if(Number.isFirst(tk)){
			num = tk;
			tk = ct.getNextToken(pcx);
		}else {
				pcx.fatalError(tk.toExplainString() + "\"NUMBER does not exists\"");
		}
		if(pcx.getTable().search(ident.getText()) != null){
			if(!pcx.getTable().search(ident.getText()).getIsGlobal()){
				pcx.fatalError(ident.toExplainString() + "multiple declaration ("+ ident.getText() +").");
			}
		}
		if(mult == null && amp == null){
			pcx.getTable().register(ident.getText(), true, CType.getCType(CType.T_int), num.getIntValue(), pcx.getSeqPlus());
		}else if(mult != null && amp != null){
			pcx.getTable().register(ident.getText(), true, CType.getCType(CType.T_pint), num.getIntValue(), pcx.getSeqPlus());
		}else {
			pcx.fatalError(tk.toExplainString() + "irregal type of const variable.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {

	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; const item starts");
		String a = ident.getText();
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
		o.println(";;; const item completes");
	}
}
