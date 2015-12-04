package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Condition extends CParseRule {
	// Ident ::= IDENT

	//private CToken bool;
	private CParseRule expression;
	private CParseRule condition;
	private CToken t;
	private CToken f;

	public Condition(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Expression.isFirst(tk) || tk.getType() == CToken.TK_TRUE || tk.getType() == CToken.TK_FALSE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if (ConditionLT.isFirst(tk)) {
				condition = new ConditionLT(pcx, expression);
			}else if(ConditionLE.isFirst(tk) ){
				condition = new ConditionLE(pcx, expression);
			}else if(ConditionGT.isFirst(tk) ){
				condition = new ConditionGT(pcx, expression);
			}else if(ConditionGE.isFirst(tk) ){
				condition = new ConditionGE(pcx, expression);
			}else if(ConditionEQ.isFirst(tk) ){
				condition = new ConditionEQ(pcx, expression);
			}else if(ConditionNE.isFirst(tk) ){
				condition = new ConditionNE(pcx, expression);
			}else {
				pcx.fatalError(tk.toExplainString() + "irregal type of sentence (comparison1)");
			}
			condition.parse(pcx);
			tk = ct.getCurrentToken(pcx);
		} else if (tk.getType() == CToken.TK_TRUE ){
			t = tk;
			tk = ct.getNextToken(pcx);
		}else if (tk.getType() == CToken.TK_FALSE) {
			f = tk;
			tk = ct.getNextToken(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "irregal type of sentence (comparison2)");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(condition != null){
			condition.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition starts");
		if(condition != null){
			condition.codeGen(pcx);
		}
		if(t != null){
			o.println("\tMOV\t#0x0001\t, (R6)+\t; True:");
		}
		if(f != null){
			o.println("\tMOV\t#0x0000\t, (R6)+\t; False:");
		}
		o.println(";;; condition completes");
	}
}

class ConditionLT extends CParseRule {
	// conditionLT ::= LT expression
	//private int seq;
	private CParseRule left, right;

	public ConditionLT(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "\"expression\"does not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("cannnot compare. left type[" + left.getCType() + "] is different to right type["
						+ right.getCType() + "].");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition < (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6)\t, R0\t; ConditionLT: 2数を取り出して、比べる");
			o.println("\tMOV\t-(R6)\t, R1\t; ConditionLT:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionLT:");
			o.println("\tCMP\tR0\t, R1\t; ConditionLT: R1<R0 = R1-R0<0");
			o.println("\tBRN\tLT" + seq + "\t\t; ConditionLT:");
			o.println("\tCLR\tR2 \t\t; ConditionLT: set false");
			o.println("LT" + seq + ":\tMOV\tR2\t, (R6)+\t; ConditionLT:");
		}
		o.println(";;; condition < (compare) completes");
	}
}

class ConditionLE extends CParseRule {
	// conditionLE ::= LE expression
	//private int seq;
	private CParseRule left, right;

	public ConditionLE(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "\"expression\"does not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("cannnot compare. left type[" + left.getCType() + "] is different to right type["
						+ right.getCType() + "].");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition <= (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6)\t, R0\t; ConditionLE: 2数を取り出して、比べる");
			o.println("\tMOV\t-(R6)\t, R1\t; ConditionLE:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionLE:");
			o.println("\tCMP\tR0\t, R1\t; ConditionLE: R1<=R0 = R1-R0<=0");
			o.println("\tBRN\tLE" + seq + "\t\t; ConditionLE:");
			o.println("\tBRZ\tLE" + seq + "\t\t; ConditionLE:");
			o.println("\tCLR\tR2 \t\t; ConditionLE: set false");
			o.println("LE" + seq + ":\tMOV\tR2\t, (R6)+\t; ConditionLE:");
		}
		o.println(";;; condition <= (compare) completes");
	}
}

class ConditionGT extends CParseRule {
	// conditionGT ::= GT expression
	//private int seq;
	private CParseRule left, right;

	public ConditionGT(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_GT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "\"expression\"does not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("cannnot compare. left type[" + left.getCType() + "] is different to right type["
						+ right.getCType() + "].");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition > (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6)\t, R0\t; ConditionGT: 2数を取り出して、比べる");
			o.println("\tMOV\t-(R6)\t, R1\t; ConditionGT:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionGT:");
			o.println("\tCMP\tR1\t, R0\t; ConditionGT: R1>R0 = R0-R1<0");
			o.println("\tBRN\tLE" + seq + "\t\t; ConditionGT:");
			o.println("\tCLR\tR2 \t\t; ConditionGT: set false");
			o.println("LE" + seq + ":\tMOV\tR2\t, (R6)+\t; ConditionGT:");
		}
		o.println(";;; condition > (compare) completes");
	}
}

class ConditionGE extends CParseRule {
	// conditionGE ::= GE expression
	//private int seq;
	private CParseRule left, right;

	public ConditionGE(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_GE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "\"expression\"does not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("cannnot compare. left type[" + left.getCType() + "] is different to right type["
						+ right.getCType() + "].");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition >= (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6)\t, R0\t; ConditionGE: 2数を取り出して、比べる");
			o.println("\tMOV\t-(R6)\t, R1\t; ConditionGE:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionGE:");
			o.println("\tCMP\tR1\t, R0\t; ConditionGE: R1>=R0 = R0-R1<=0");
			o.println("\tBRN\tLE" + seq + "\t\t; ConditionGE:");
			o.println("\tBRZ\tLE" + seq + "\t\t; ConditionGE:");
			o.println("\tCLR\tR2 \t\t; ConditionGE: set false");
			o.println("LE" + seq + ":\tMOV\tR2\t, (R6)+\t; ConditionGE:");
		}
		o.println(";;; condition >= (compare) completes");
	}
}

class ConditionEQ extends CParseRule {
	// conditionEQ ::= EQ expression
	//private int seq;
	private CParseRule left, right;

	public ConditionEQ(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_EQ;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "\"expression\"does not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("cannnot compare. left type[" + left.getCType() + "] is different to right type["
						+ right.getCType() + "].");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition== (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6)\t, R0\t; ConditionEQ: 2数を取り出して、比べる");
			o.println("\tMOV\t-(R6)\t, R1\t; ConditionEQ:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionEQ:");
			o.println("\tCMP\tR1\t, R0\t; ConditionEQ: R1=R0 = R0-R1=0");
			o.println("\tBRZ\tLE" + seq + "\t\t; ConditionEQ:");
			o.println("\tCLR\tR2 \t\t; ConditionEQ: set false");
			o.println("LE" + seq + ":\tMOV\tR2\t, (R6)+\t; ConditionEQ:");
		}
		o.println(";;; condition == (compare) completes");
	}
}

class ConditionNE extends CParseRule {
	// conditionNE ::= NE expression
	//private int seq;
	private CParseRule left, right;

	public ConditionNE(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_NE;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "\"expression\"does not exists.");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("cannnot compare. left type[" + left.getCType() + "] is different to right type["
						+ right.getCType() + "].");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition != (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6)\t, R0\t; ConditionNE: 2数を取り出して、比べる");
			o.println("\tMOV\t-(R6)\t, R1\t; ConditionNE:");
			o.println("\tCLR\tR2\t\t; ConditionNE:");
			o.println("\tCMP\tR1\t, R0\t; ConditionNE: R1!=R0 = R0-R1!=0");
			o.println("\tBRZ\tLE" + seq + "\t\t; ConditionNE:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionNE: set true");
			o.println("LE" + seq + ":\tMOV\tR2\t,(R6)+\t; ConditionNE:");
		}
		o.println(";;; condition != (compare) completes");
	}
}


