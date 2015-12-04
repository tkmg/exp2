package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

class TermMult extends CParseRule {
	// expressionMult ::= '*' term
	private CToken mult;
	private CParseRule left, right;

	public TermMult(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MUL;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		mult = ct.getCurrentToken(pcx);
		// +の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "*の後ろはfactorです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 引き算の型計算規則
		final int s[][] = {
			//		T_err			T_int			T_pint			int[]			int*[]
			{	CType.T_err,	CType.T_err ,	CType.T_err ,	CType.T_err,	CType.T_err	},	// T_err
			{	CType.T_err,	CType.T_int ,	CType.T_err ,	CType.T_int,	CType.T_err	},	// T_int
			{	CType.T_err,	CType.T_err ,	CType.T_err ,	CType.T_err,	CType.T_err	},	// T_pint
			{	CType.T_err,	CType.T_int ,	CType.T_err ,	CType.T_err,	CType.T_err	},	// int[]
			{	CType.T_err,	CType.T_err ,	CType.T_err ,	CType.T_err,	CType.T_err	}	// int*[]
		};
		int lt = 0, rt = 0;
		boolean lc = false, rc = false;
		if (left != null) {
			left.semanticCheck(pcx);
			lt = left.getCType().getType();		// *の左辺の型
			lc = left.isConstant();
		} else {
			pcx.fatalError(mult.toExplainString() + "左辺がありません");
		}
		if (right != null) {
			right.semanticCheck(pcx);
			rt = right.getCType().getType();	// *の右辺の型
			rc = right.isConstant();
		} else {
			pcx.fatalError(mult.toExplainString() + "右辺がありません");
		}
		int nt = s[lt][rt];						// 規則による型計算
		if (nt == CType.T_err) {
			pcx.fatalError(mult.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は足せません");
		}
		this.setCType(CType.getCType(nt));
		this.setConstant(lc && rc);				// *の左右両方が定数のときだけ定数
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			o.println("\tJSR\tMUL\t; TermMult: ２数を掛けてもらう<" + mult.toExplainString() + ">");
			o.println("\tMOV\t-(R6),R0\t; TermMult");
			o.println("\tSUB\t#2   ,R6\t; TermMult");
			o.println("\tMOV\tR0   ,(R6)+\t; TermMult");
		}
	}
}
