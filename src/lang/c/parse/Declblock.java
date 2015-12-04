package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.SymbolTable;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Declblock extends CParseRule {
	// program ::= expression EOF
	private ArrayList<CParseRule> program;
	private SymbolTable<CSymbolTableEntry> l;
	private CToken func;

	public Declblock(CParseContext pcx, CToken func) {
		this.func = func;
		program = new ArrayList<CParseRule>();
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LCUR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		pcx.getTable().makeLocalSymbolTable();
		int i = 0;
		pcx.setSeq(0);
		while(true){
			tk = ct.getCurrentToken(pcx);
			if(Declaration.isFirst(tk)){
				program.add(new Declaration(pcx));
				program.get(i).parse(pcx);
				i++;
			}else {
				break;
			}
		}
		while(true){
			tk = ct.getCurrentToken(pcx);
			if(Statement.isFirst(tk)){
				program.add(new Statement(pcx, func));
				program.get(i).parse(pcx);
				i++;
			}else {
				break;
			}
		}
		pcx.setSeq(0);
		if(tk.getType() == CToken.TK_RCUR){
			l = pcx.getTable().getLocal();
			pcx.getTable().deleteLocalSymbolTable();
			tk = ct.getNextToken(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "\"}\"(RCUR) does not exists.(in declblock)");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		for(int i = 0; i < program.size(); i ++){
			if (program != null) {
				program.get(i).semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		int p = l.size() + 1;
		for(int i = l.size() - 1; i >= 0 ; i --){
			String a = (String)l.keySet().toArray()[i];
			//System.out.print(a + ",");
			if(l.search(a).getSize() != 1 && !l.search(a).getConstp()){
				p += l.search(a).getSize() - 1;
			}
		}
		o.println(";;; declblock starts");
		o.println("\tMOV\tR4\t, (R6)+\t; declblock : フレームポインタをスタックへ");
		o.println("\tMOV\tR6\t, R4\t; declblock : フレームポインタの保存");
		o.println("\tADD\t#" + p + "\t, R6\t; declblock : ポインタを移動させて局所変数用の領域を確保");

		for(int i = l.size() - 1; i >= 0 ; i --){
			String a = (String)l.keySet().toArray()[i];
			if(l.search(a).getConstp()){
				o.println("\tMOV\t#" + l.search(a).getSize() + "\t,R0\t: const ; 定数の値を保存");
				o.println("\tMOV\t#" + l.search(a).getAddress() + "\t,R1\t: const ; 定数の相対位置を保存");
				o.println("\tADD\tR4\t,R1\t: const ; 定数のアドレス計算");
				o.println("\tMOV\tR0\t,(R1)\t: const ; 定数の値を設定");
			}
		}
		if (program != null) {
			for(CParseRule r : program){
				r.codeGen(pcx);
			}
		}
		o.println("RET_"+func.getText() + ":\t\t\t\t; declblock : 関数の最終位置設定");
		o.println("\tSUB\t#" + p + "\t, R6\t; declblock : ポインタを移動させて局所変数用の領域を削除");
		o.println("\tMOV\t-(R6)\t, R4\t; declblock : フレームポインタの復旧");
		o.println(";;; declblock completes");
	}
}
