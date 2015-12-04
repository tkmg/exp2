package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class DeclItem extends CParseRule {
	// Variable ::= ident [ array ]
	private CToken mult;
	private CToken ident;
	private CToken num;
	private CToken func;

	public DeclItem(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MUL || Ident.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_MUL) {
			mult = tk;
			tk = ct.getNextToken(pcx);
		}
		if (Ident.isFirst(tk)) {
			ident = tk;
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "\"variable\" does not exists.");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_LBAR) {
			tk = ct.getNextToken(pcx);
			if (Number.isFirst(tk)) {
				num = tk;
				tk = ct.getNextToken(pcx);
				if (tk.getType() == CToken.TK_RBAR) {
					tk = ct.getNextToken(pcx);
				} else {
					pcx.fatalError(tk.toExplainString() + "\"]\" does not exist");
				}
			} else {
				pcx.fatalError(tk.toExplainString() + "\"number\" does not exist.");
			}
		} else if (tk.getType() == CToken.TK_LPAR) {
			func = tk;
			tk = ct.getNextToken(pcx);
			if (tk.getType() == CToken.TK_RPAR) {
				tk = ct.getNextToken(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "\")\" (RPAR) does not exsist (funcall part).");
			}
		}
		if(pcx.getTable().search(ident.getText()) != null){
			if(!pcx.getTable().search(ident.getText()).getIsGlobal()){
				pcx.fatalError(ident.toExplainString() + "multiple declaration ("+ ident.getText() +").");
			}
		}
		int a;
		if(func == null){
			if(mult == null){
				if(num == null){
					a = pcx.getSeqPlus();
					pcx.getTable().register(ident.getText(), false, CType.getCType(CType.T_int), 1, a);
				}else {
					a = pcx.getSeqPlus() + num.getIntValue() - 1;
					pcx.getTable().register(ident.getText(), false, CType.getCType(CType.T_int_a), Integer.valueOf(num.getText()), a);
					pcx.setSeq(a);
				}
			}else {
				if(num == null){
					a = pcx.getSeqPlus();
					pcx.getTable().register(ident.getText(), false, CType.getCType(CType.T_pint), 1, a);
				}else {
					a = pcx.getSeqPlus() + num.getIntValue() - 1;
					pcx.getTable().register(ident.getText(), false, CType.getCType(CType.T_pint_a), Integer.valueOf(num.getText()), a);
					pcx.setSeq(a);
				}
			}
		}
		if(func != null){
			if(pcx.getTable().getLocal() != null){
				pcx.fatalError(ident.toExplainString() + "this is function.");
			}
			if(mult == null){
				if(num == null){
					a = pcx.getSeqPlus();
					pcx.getTable().register(ident.getText(), true, CType.getCType(CType.T_int), 1, a);
				}
			}else {
				if(num == null){
					a = pcx.getSeqPlus();
					pcx.getTable().register(ident.getText(), true, CType.getCType(CType.T_pint), 1, a);
				}
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {

	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; decl item starts");
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
		o.println(";;; decl item completes");
	}
}
