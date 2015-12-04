package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Program extends CParseRule {
	// program ::= expression EOF
	private ArrayList<CParseRule> program;

	public Program(CParseContext pcx) {
		program = new ArrayList<CParseRule>();
	}

	public static boolean isFirst(CToken tk) {
		return Function.isFirst(tk) || Declaration.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		int i = 0;
		while (true) {
			tk = ct.getCurrentToken(pcx);
			if (Declaration.isFirst(tk)) {
				program.add(new Declaration(pcx));
				program.get(i).parse(pcx);
				i++;
			} else {
				break;
			}
		}
		while (true) {
			tk = ct.getCurrentToken(pcx);
			if (Function.isFirst(tk)) {
				program.add(new Function(pcx));
				program.get(i).parse(pcx);
				i++;
			} else {
				break;
			}
		}
		if (tk.getType() != CToken.TK_EOF) {
			pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		for (int i = 0; i < program.size(); i++) {
			if (program != null) {
				program.get(i).semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; program starts");
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ");
		for (int i = pcx.getTable().getGlobalSize() - 1; i >= 0; i--) {
			String a = (String) pcx.getTable().getGlobalKey()[i];
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
		}
		if (program != null) {
			o.println("__START:");
			o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
			for (int i = 0; i < program.size(); i++) {
				program.get(i).codeGen(pcx);
			}
			o.println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用");
		}
		o.println("\tHLT\t\t\t; ProgramNode:");
		o.println("\t.END\t\t\t; ProgramNode:");
		o.println(";;; program completes");
	}
}
