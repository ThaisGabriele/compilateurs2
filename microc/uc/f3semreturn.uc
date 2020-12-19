/* funcoes */

const int MAX = 10;
int g;

int func()
{
	int lf;
	printf(MAX, " ", g, " ", lf, "\n"); /* sem return! */
}

void main()
{
	int l;
	func(); /* chamada como procedimento */
}

/*
-----------------------
 marcador: UC
 codeSize: 42
 dataSize: 1
   mainPC: 27
startStrz: 36
-----------------------
0: enter 0 1
3: const 10
8: printi
9: prints 36
12: getstatic 0
15: printi
16: prints 38
19: load 0
21: printi
22: prints 40
25: trap 1
27: enter 0 1
30: call 0
33: pop
34: exit
35: return
36:  
38:  
40: \n
*/
