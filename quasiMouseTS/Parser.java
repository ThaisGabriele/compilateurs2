

import java.util.List;
import java.util.ArrayList;

class NDesig {
	String nome;
	public NDesig(String n) {
		nome = n;
	}
	public String getNome() {
		return nome;
	}
}



public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _numero = 2;
	public static final int _string = 3;
	public static final int _dolarSign = 4;
	public static final int _interrogacao = 5;
	public static final int _aspas = 6;
	public static final int _ecomercial = 7;
	public static final int _circunflexo = 8;
	public static final int _exclamacao = 9;
	public static final int _ponto = 10;
	public static final int _doispontos = 11;
	public static final int _cerquilha = 12;
	public static final int _arroba = 13;
	public static final int _abreColchete = 14;
	public static final int _barraVertical = 15;
	public static final int _fechaColchete = 16;
	public static final int _abreParenteses = 17;
	public static final int _fechaparenteses = 18;
	public static final int _pontoEvirgula = 19;
	public static final int _virgula = 20;
	public static final int _maior = 21;
	public static final int _menor = 22;
	public static final int _igual = 23;
	public static final int maxT = 28;
	public static final int _option = 29;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	private Tab ts;
	private Obj atual; // indica qual Ã© a funÃ§Ã£o atual

  private static final int // compare operators
            eq = 0,
            lt = 2,
            gt = 4;
	
	public void erro(String msg) {
		errors.SemErr(t.line, t.col, msg);
	}

  private boolean verificaTipo(String s) {
    return s != null && s.matches("[0-9]*");
  }



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			if (la.kind == 29) {
				ts.dump(); 
			}
			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void QuasiMouse() {
		atual = null; // nÃ£o hÃ¡ funÃ§Ã£o sendo compilada
		ts = new Tab(this);
		ts.abrirEscopo("Escopo Global"); 
		Instrucao();
		while (la.kind == 1 || la.kind == 2 || la.kind == 4) {
			Instrucao();
		}
		Expect(4);
		ts.fecharEscopo(); 
	}

	void Instrucao() {
		if (la.kind == 4) {
			DeclaracaoFuncao();
		} else if (la.kind == 1 || la.kind == 2) {
			Assinalamento();
		} else SynErr(29);
	}

	void DeclaracaoFuncao() {
		String s = ""; NDesig id; 
		Expect(4);
		id = DesigIdent();
		atual = ts.inserir(Obj.Func, id.getNome());
		ts.abrirEscopo("Funcao " + id.getNome()); 
		while (la.kind == 20) {
			Get();
			id = DesigIdent();
			ts.inserir(Obj.Var, id.getNome()); atual.nPars++;
		}
		Expect(19);
		Bloco();
		atual.locais = ts.escopoAtual.locais; // Bloco com as instruÃ§Ãµes da func
		ts.fecharEscopo(); 
	}

	void Assinalamento() {
		Obj o; NDesig id; 
		Operand op = Exp();
		if (la.kind == 1) {
			id = DesigIdent();
			o = ts.buscar(id.getNome());  
			op = new Operand(o); 
			Expect(11);
		}
	}

	NDesig  DesigIdent() {
		NDesig  n;
		Expect(1);
		String nome = t.val; 
		n = new NDesig(nome); 
		return n;
	}

	void Bloco() {
		while (la.kind == 1 || la.kind == 2 || la.kind == 4) {
			Instrucao();
		}
		Expect(13);
	}

	Operand  Exp() {
		Operand  op;
		op=null; int opr; 
		op = Termo();
		if (la.kind == 1 || la.kind == 2) {
			op = Termo();
		}
		if (verificaTipo(String(op))) {
		  erro("deve ser inteiro"); }
		if (la.kind == 24 || la.kind == 25) {
			if (la.kind == 24) {
				Get();
			} else {
				Get();
			}
		}
		while (la.kind == 1 || la.kind == 2) {
			op = Termo();
			if (la.kind == 1 || la.kind == 2) {
				op = Termo();
			}
			if (la.kind == 24) {
				Get();
			} else if (la.kind == 25) {
				Get();
			} else SynErr(30);
		}
		return op;
	}

	int  Condicional() {
		int  opr;
		Operand opx, opy; opx = opy = null; opr = 0; 
		Expect(14);
		Instrucao();
		if (la.kind == 15) {
			Get();
			Instrucao();
		}
		Expect(16);
		return opr;
	}

	int  OpRel() {
		int  opr;
		opr = -1; 
		if (la.kind == 23) {
			Get();
			opr = eq; 
		} else if (la.kind == 21) {
			Get();
			opr = gt; 
		} else if (la.kind == 22) {
			Get();
			opr = lt; 
		} else SynErr(31);
		return opr;
	}

	Operand  Termo() {
		Operand  op;
		op = null; 
		op = Fator();
		while (la.kind == 26 || la.kind == 27) {
			if (la.kind == 26) {
				Get();
			} else {
				Get();
			}
			op = Fator();
		}
		return op;
	}

	Operand   Fator() {
		Operand   op;
		op = null; NDesig id;  
		if (la.kind == 2) {
			Get();
			op = new Operand(Integer.parseInt(t.val)); 
		} else if (la.kind == 1) {
			id = DesigIdent();
			Obj o = ts.buscar(id.getNome()); op = new Operand(o);  
		} else SynErr(32);
		return op;
	}

	Operand  Designador() {
		Operand  op;
		op=null; NDesig id; 
		Expect(1);
		Obj o = ts.buscar(t.val);
		op = new Operand(o); 
		Expect(10);
		return op;
	}

	void Imprime() {
		if (la.kind == 3) {
			Get();
			if (la.kind == 7) {
				Get();
			}
		} else if (la.kind == 1 || la.kind == 2 || la.kind == 12) {
			if (la.kind == 1 || la.kind == 2) {
				Operand op = Exp();
			} else {
				Get();
				Operand op = Exp();
			}
			Expect(9);
			if (la.kind == 7) {
				Get();
			}
		} else if (la.kind == 7) {
			Get();
		} else SynErr(33);
	}

	void Leitura() {
		if (la.kind == 5) {
			Get();
		} else if (la.kind == 5) {
			Get();
			Expect(1);
			Expect(11);
		} else SynErr(34);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		QuasiMouse();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "ident expected"; break;
			case 2: s = "numero expected"; break;
			case 3: s = "string expected"; break;
			case 4: s = "dolarSign expected"; break;
			case 5: s = "interrogacao expected"; break;
			case 6: s = "aspas expected"; break;
			case 7: s = "ecomercial expected"; break;
			case 8: s = "circunflexo expected"; break;
			case 9: s = "exclamacao expected"; break;
			case 10: s = "ponto expected"; break;
			case 11: s = "doispontos expected"; break;
			case 12: s = "cerquilha expected"; break;
			case 13: s = "arroba expected"; break;
			case 14: s = "abreColchete expected"; break;
			case 15: s = "barraVertical expected"; break;
			case 16: s = "fechaColchete expected"; break;
			case 17: s = "abreParenteses expected"; break;
			case 18: s = "fechaparenteses expected"; break;
			case 19: s = "pontoEvirgula expected"; break;
			case 20: s = "virgula expected"; break;
			case 21: s = "maior expected"; break;
			case 22: s = "menor expected"; break;
			case 23: s = "igual expected"; break;
			case 24: s = "\"-\" expected"; break;
			case 25: s = "\"+\" expected"; break;
			case 26: s = "\"*\" expected"; break;
			case 27: s = "\"/\" expected"; break;
			case 28: s = "??? expected"; break;
			case 29: s = "invalid Instrucao"; break;
			case 30: s = "invalid Exp"; break;
			case 31: s = "invalid OpRel"; break;
			case 32: s = "invalid Fator"; break;
			case 33: s = "invalid Imprime"; break;
			case 34: s = "invalid Leitura"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
