package lang.c;

import lang.SymbolTable;

public class CSymbolTable {
	private class OneSymbolTable extends SymbolTable<CSymbolTableEntry>{
		@Override
		public CSymbolTableEntry register(String name, CSymbolTableEntry e){
			return put(name, e);
		}

		@Override
		public CSymbolTableEntry search(String name){
			return get(name);
		}

		public CSymbolTableEntry delete(String name){
			return get(name);
		}
	}

	private OneSymbolTable global;
	private OneSymbolTable local;
	//private SymbolTable<CSymbolTableEntry> global;
	//private SymbolTable<CSymbolTableEntry> local;

	public CSymbolTable(){
		this.global = new OneSymbolTable();
	}

	public CSymbolTableEntry search(String name) {
		CSymbolTableEntry e;
		if (local != null) {
			if (local.search(name) != null) {
				e = local.search(name);
				//System.out.println(e.getIsGlobal() + ",name:" + name + ",local");
			} else if(global.search(name) != null){
				e = global.search(name);
				//System.out.println(e.getIsGlobal() + ",name:" + name + ",global");
			}else {
				e = null;
			}
		} else {
			if (global.search(name) != null) {
				e = global.search(name);
				//System.out.println(e.getIsGlobal() + ",name:" + name + ",global");
			} else {
				e = null;
			}
		}
		return e;
	}

	public void makeLocalSymbolTable(){
		this.local = new OneSymbolTable();
	}

	public void deleteLocalSymbolTable(){
		local = null;
	}

	public boolean isLocal(){
		return local != null;
	}

	public CSymbolTableEntry register(String name, boolean constp, CType type, int size, int addr){
		CSymbolTableEntry e;
		if(local != null){
			e = new CSymbolTableEntry(type, size, constp, false, addr);
			//System.out.println("local register:" + name);
			return local.register(name, e);
		}else {
			e = new CSymbolTableEntry(type, size, constp, true, addr);
			//System.out.println("global register:" + name);
			return global.register(name, e);
		}
	}

	public int getGlobalSize(){
		return global.size();
	}

	public int getLocalSize(){
		return local.size();
	}

	public Object[] getGlobalKey(){
		return global.keySet().toArray();
	}

	public Object[] getLocalKey(){
		return local.keySet().toArray();
	}

	public OneSymbolTable getGlobal(){
		return global;
	}
	public OneSymbolTable getLocal(){
		return local;
	}
}
