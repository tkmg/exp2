package lang.c;

public class CType {
	public static final int T_err		= 0;		// 型エラー
	public static final int T_int		= 1;		// int
	public static final int T_pint		= 2;		// int*
	public static final int T_int_a		= 3;		// int[]
	public static final int T_pint_a	= 4;		// int*[]
	public static final int T_bool		= 5;		// boolean
	public static final int T_void		= 6;		// void

	private static CType[] typeArray = {
		new CType(T_err,	"error"),
		new CType(T_int,	"int"),
		new CType(T_pint,	"int*"),
		new CType(T_int_a,	"int[]"),
		new CType(T_pint_a,	"int*[]"),
		new CType(T_bool,	"boolean"),
		new CType(T_void,	"void")
	};

	private int type;
	private String string;

	private CType(int type, String s) {
		this.type = type;
		this.string = s;
	}
	public static CType getCType(int type) {
		return typeArray[type];
	}
	public boolean isCType(int t)	{ return t == type; }
	public int getType()			{ return type; }
	public String toString()		{ return string; }
}
