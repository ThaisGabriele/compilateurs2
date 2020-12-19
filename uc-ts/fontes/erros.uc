/*
    Programa em uC
*/

const int TAM = 10;

void main()
{
    int v[], v2[];

    v = new int { 61, 57, 72, 18, 
                   8, 30, 21, 93, 
                  67, 15, 78, 12, 
                  81, 32, 27, 92 };
    v2 = new int [len(v)];

    printf(" ORIGINAL = "); 
    imprime(v); 
    printf("\n");
}

void imprime(int v[])
{
    int i;
    for (i = 0; i < len(v); i = i + 1)
        printf(v[i], " ");
$d
    int i; i = len(v);
}
