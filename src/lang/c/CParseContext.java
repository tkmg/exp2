package lang.c;

import java.util.ArrayList;

import lang.IOContext;
import lang.ParseContext;

public class CParseContext extends ParseContext {

	private CSymbolTable table;

	public CParseContext(IOContext ioCtx, CTokenizer tknz) {
		super(ioCtx, tknz);
		table = new CSymbolTable();
		func = new ArrayList<CToken>();
	}

	@Override
	public CTokenizer getTokenizer() {
		return (CTokenizer) super.getTokenizer();
	}

	private int seqNo = 0;
	private int seq = 0;
	private ArrayList<CToken> func;

	public int getSeqId() {
		return seqNo++;
	}

	public void setSeq(int seq){
		this.seq = seq;
	}

	public int getSeq(){
		return seq;
	}

	public int getSeqPlus(){
		return ++seq;
	}

	public CSymbolTable getTable() {
		return table;
	}

	public void setFunc(CToken func){
		this.func.add(func);
	}

	public CToken getFunc(int n){
		return func.get(n);
	}
}
