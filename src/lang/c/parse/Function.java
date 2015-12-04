package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Function extends CParseRule {
	// Function ::= FUNC (INT | VOID) [MULT] IDENT LPAR RPAR declblock
	private CParseRule declblock;
	private CToken mul;
	private CToken type;
	private CToken ident;
	public Function(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_FUNC;
	}

	public CToken getFunc(){
		return ident;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(tk.getType() == CToken.TK_INT){
			type = tk;
			tk = ct.getNextToken(pcx);
		}else if(tk.getType() == CToken.TK_VOID){
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\"int\" or \"void\" does not exsist(function part).");
		}
		if(tk.getType() == CToken.TK_MUL){
			mul = tk;
			tk = ct.getNextToken(pcx);
		}
		if(Ident.isFirst(tk)){
			ident = tk;
			if(pcx.getTable().search(tk.getText() + "_a") == null){
				pcx.fatalError(ident.toExplainString() + "multiple declartion(function).");
			}
			tk = ct.getNextToken(pcx);
			if(tk.getType() == CToken.TK_LPAR){
				tk = ct.getNextToken(pcx);
				if(tk.getType() == CToken.TK_RPAR){
					tk = ct.getNextToken(pcx);
					if(Declblock.isFirst(tk)){
						declblock = new Declblock(pcx, ident);
						declblock.parse(pcx);
						tk = ct.getCurrentToken(pcx);
					}else {
						pcx.fatalError(tk.toExplainString() + "\"declblock\" does not exsist(function part).");
					}
				}else {
					pcx.fatalError(tk.toExplainString() + "\")\" (RPAR) does not exsist(function part).");
				}
			}else {
				pcx.fatalError(tk.toExplainString() + "\"(\" (LPAR) does not exsist(function part).");
			}
		}else {
			pcx.fatalError(tk.toExplainString() + "\"IDENT\" does not exsist(function part).");
		}
		pcx.setFunc(ident);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		CType c =  pcx.getTable().search(ident.getText()).getType();
		if(type != null){
			if(mul != null){
				if(c != CType.getCType(CType.T_pint)){
					pcx.getTable().search(ident.getText()).setType(CType.getCType(CType.T_pint));
				}
			}else {
				if(c != CType.getCType(CType.T_int)){
					pcx.getTable().search(ident.getText()).setType(CType.getCType(CType.T_int));
				}
			}
		}else {
			if(mul == null){
				if(c != CType.getCType(CType.T_void)){
					pcx.getTable().search(ident.getText()).setType(CType.getCType(CType.T_void));
				}
			}
		}
		if (declblock != null) {
			declblock.semanticCheck(pcx);
			this.setCType(declblock.getCType());
			this.setConstant(declblock.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; function starts");
		o.println(ident.getText() + ":\t\t\t\t; function : 関数の初期位置設定");
		if (declblock != null) {
			declblock.codeGen(pcx);
		}
		o.println("\tMOV\tR4\t, R6\t; function : フレームポインタから局所変数の復旧");
		o.println("\tRET\t\t\t; function : RET");
		o.println(";;; function completes");
	}
}
