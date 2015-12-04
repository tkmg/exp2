package lang.c;

import lang.SymbolTableEntry;

public class CSymbolTableEntry extends SymbolTableEntry{
	private CType type;
	private int size;
	private boolean constp;
	private boolean isGlobal;
	private int address;

	public CSymbolTableEntry(CType type, int size, boolean constp, boolean isGlobal, int addr){
		this.type = type;
		this.size = size;
		this.constp = constp;
		this.isGlobal = isGlobal;
		this.address = addr;
	}

	public String toExplainString(){
		return this.toString();
	}

	public CType getType(){
		return type;
	}

	public void setType(CType type){
		this.type = type;
	}

	public int getSize(){
		return size;
	}

	public boolean getConstp(){
		return constp;
	}

	public boolean getIsGlobal(){
		return isGlobal;
	}

	public int getAddress(){
		return address;
	}
}
