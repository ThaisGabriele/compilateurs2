/*
	Tabela de Simbolos
	Marco Cristo, 2014 - Adaptado por David e Thais
*/

import java.lang.*;

class Struct {
	static final int // categorias de tipos 
	Int = 1; 
	int cat; // Nenhum, Int, Int e Vetor

	public Struct(int cat) {
		this.cat = cat;
	}

	// checa se dois tipos sao o mesmo (equivalencia estrutural para vetor e equivalencia de nome pro resto)
	public boolean igual (Struct outro) {
			return outro == this;
	}

	// checa se tipos sao compativeis (e.g. em comparacoes)
	public boolean compativelCom (Struct outro) {
		return this.igual(outro);
	}
}

class Obj {
	static final int 	Const = 0, 
				Var = 1, 
				Tipo = 2, 
				Func = 3,
				Prog = 4;
	int cat; // categoria do objeto = Const, Var, Tipo, Func, Prog
	String nome;
	Obj prox;
	int val; // valor pra Const
	int end; // Endereco pra Var e Funcao
	int nivel; // Var: 0 = global, 1 = local
	int nPars; // Func: nro de parametros
	Obj locais; // Func: parametros locais
	Obj ultObj; // Func: ultimo Obj em locais

	public Obj(int cat, String nome) {
		this.cat = cat; 
		this.nome = nome; 
		this.locais = null;
		this.prox = null;
		this.ultObj = null;
		nPars = 0;
	}
	
	public void adicionaALocais(Obj o) {
		if (locais == null) 
			locais = o;
		else 
			ultObj.prox = o;
		ultObj = o;
		nPars ++;
	}
}

class Escopo {
	String nome;  // nome do escopo para DUMP
	Escopo acima; // proximo escopo acima
	Obj locais; // objetos nesse escopo
	int nVars; // numero de variaveis nesse escopo (necessario para alocar enderecos)

	public Escopo(String nome)
	{
		this.nome = nome;
	}
}

public class Tab {
	static Escopo escopoAtual; // escopo atual (topo)
	static int nivelAtual; // nivel do escopo atual		
	static Obj objTamVetor; // objetos pre-definidos
	static Obj semObj;
	Parser parser;

	public Tab(Parser parser) {
		this.parser = parser;
		iniciar();
	}

	// inserir objeto na tabela
	Obj inserir (int cat, String nome) {
		Obj obj = new Obj(cat, nome);
		if (cat == Obj.Var) {
			obj.end = escopoAtual.nVars; 
			escopoAtual.nVars++;
			obj.nivel = nivelAtual;
		}
		//--- adiciona noh objeto
		Obj p = escopoAtual.locais, ult = null;
		while (p != null) {
			if (p.nome.equals(nome)) 
				parser.erro("\"" + nome + "\" já foi declarado!");
			ult = p; 
			p = p.prox;
		}
		if (ult == null) 
			escopoAtual.locais = obj; 
		else 
			ult.prox = obj;
		return obj;
	}

	// tenta encontrar objeto
	Obj buscar (String nome) {
		for (Escopo s = escopoAtual; s != null; s = s.acima)
			for (Obj p = s.locais; p != null; p = p.prox)
				if (p.nome.equals(nome)) 
					return p;
		parser.erro(nome + " não foi declarado!");
		return semObj;
	}

	// abrir escopo
	void abrirEscopo(String nome) { 
		Escopo s = new Escopo(nome);
		s.acima = escopoAtual;
		escopoAtual = s;
		nivelAtual ++;
	}

	// fechar escopo
	void fecharEscopo() { 
		escopoAtual = escopoAtual.acima;
		nivelAtual --;
	}

	// dump Structs
	public void dumpStruct(Struct tipo) {
		String cat;
		switch (tipo.cat) {
			case Struct.Int:    cat = "Int"; break;
		//	case Struct.Vetor:  cat = "Vetor"; break;
			default: cat = "Null";
		}
		System.out.print(cat);
	}

	// dump Objs
	public void dumpObj(Obj o, int n) {
		for (int i = 0; i < n; i ++)
			System.out.print("  ");
		switch (o.cat) {
			case Obj.Const: 
				System.out.print("Const " + o.nome + " = " + o.val + " (");
				break;
			case Obj.Var:   
				System.out.print("Var " + o.nome + " @" + o.end + " n=" + o.nivel + " (");
				break;
			case Obj.Tipo:  
				System.out.print("Tipo " + o.nome + " (");
				break;
			case Obj.Func:  
				System.out.print("Funcao \"" + o.nome + "\" @" + o.end + " #" + o.nPars + " -> (");
				break;
			default: 
				System.out.print("Null " + o.nome + " (");
		}
		System.out.println(")");
	}

	// dump Escopos
	public void dumpEscopo(Obj head, int n) {
		for (Obj o = head; o != null; o = o.prox) {
			dumpObj(o, n);
			if (o.cat == Obj.Func || o.cat == Obj.Prog) 
				dumpEscopo(o.locais, n+1);
		}
	}

	// dump de escopo atual
	public void dump() {
		System.out.println("=================================================");
		for (Escopo e = escopoAtual; e != null; e = e.acima) {
			System.out.println(e.nome);
			System.out.println("-------------------------------------------------");
			dumpEscopo(e.locais, 0);
			System.out.println("-------------------------------------------------");
		}
		System.out.println("=================================================");
	}

	//-------------- inicialização da tabela de simbolos  ------------
	public void iniciar() {  // constroi universo
		Obj o;
		escopoAtual = new Escopo("Universo");
		escopoAtual.acima = null;
		nivelAtual = -1;
	}
}


