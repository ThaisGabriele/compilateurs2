int f(int p)
{
    if (p < 7)
       return 1;
    else if (p > 9)
       return 2;
}

/*
-----------------------
 marcador: UC
 codeSize: 42
 dataSize: 0
   mainPC: -1
startStrz: 42
-----------------------
0: enter 1 1
3: load 0
5: const 7
10: jge 23
13: const 1
18: exit
19: return
20: jmp 40
23: load 0
25: const 9
30: jle 40
33: const 2
38: exit
39: return
40: trap 1
*/
