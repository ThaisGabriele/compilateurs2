/*
	comparacao de metodos de ordenacao
*/

void imprime(int v[])
{
    int i;
    for (i = 0; i < len(v); i = i + 1)
        printf(v[i], " ");
    /* printf("\n"); */
}

void copia (int v1[], int v2[])
{
    int i;
    for (i = 0; i < len(v1); i = i + 1)
        v1[i] = v2[i];
}

void troque(int v[], int i, int j)
{
    int tmp;
    tmp = v[i];
    v[i] = v[j];
    v[j] = tmp;
}

/**** mergesort ****/

int esqMEfimEsqEmeioMEdir(int esq, int fimEsq, int meio, int dir)
{
    if (esq <= fimEsq) if (meio <= dir) return 1;
    return 0;
}

int merge(int v[], int tmp[], int esq, int meio, int dir)
{
    int i, fimEsq, tam, k, custo;

    custo = 0;
    fimEsq = meio - 1;
    k = esq;
    tam = dir - esq + 1;

    while (esqMEfimEsqEmeioMEdir(esq, fimEsq, meio, dir) == 1) {
        custo = custo + 1;
        if (v[esq] <= v[meio]) {
            tmp[k] = v[esq];
            esq = esq + 1;
        } else {
            tmp[k] = v[meio];
            meio = meio + 1;
        }
        k = k + 1;
    }

    while (esq <= fimEsq) {
        tmp[k] = v[esq];
        esq = esq + 1;
        k = k + 1;
    }

    while (meio <= dir) {
        tmp[k] = v[meio];
        meio = meio + 1;
        k = k + 1;
    }

    for (i = 0; i < tam; i = i + 1)
    {
        v[dir] = tmp[dir];
        dir = dir - 1;
    }
    return custo + 2;
}

int msort(int v[], int tmp[], int esq, int dir)
{
    int meio, custo;
    custo = 0;
    if (dir > esq)
    {
        meio = (dir + esq) / 2;
        msort(v, tmp, esq, meio);
        msort(v, tmp, meio + 1, dir);
        custo = custo + merge(v, tmp, esq, meio + 1, dir);
    }
    return custo;
}

int mergesort(int v[])
{
    int tmp[];
    tmp = new int [len(v)];
    return msort(v, tmp, 0, len(v) - 1);
}

/**** quicksort ****/

int custoq;

int iMenorFimEviMenorPivo(int i, int fim, int v[], int pivo)
{
	if (i < fim) if (v[i] < pivo) return 1;
	return 0;
}

/* particione */
int particione(int v[], int ini, int fim) {
    int i, j, pivo;
    i = ini + 1;
    j = fim;
    pivo = v[ini];    

    while (i <= j) {
        while (iMenorFimEviMenorPivo(i, fim, v, pivo) == 1) {
            i = i + 1;
            custoq = custoq + 1;
        }
        while (v[j] > pivo) {
            j = j - 1;
            custoq = custoq + 1;
        }
        custoq = custoq + 2;
        if (i < j) {
            troque(v, i, j);
            i = i + 1;
            j = j - 1;

        } else
            i = i + 1;
    }
    troque(v, j, ini);
    return j;
}

/* quicksort */
int quicksort(int v[], int ini, int fim) {
    custoq = 0;
    if ((fim - ini) < 1)
        return 0;
    int p;
    p = particione(v, ini, fim);
    if (ini < (p - 1))
        quicksort(v, ini, p - 1);
    if (fim > (p + 1))
        quicksort(v, p + 1, fim);
    return custoq;
}

/***** main *******/

void main()
{
    int custo, i; 
    int v[], v2[];

    v = new int { 61, 57, 72, 18, 8, 30, 21, 93 };
    v2 = new int [len(v)];

    printf(" ORIGINAL = "); imprime(v); printf("\n");

    copia(v2, v);
    custo = mergesort(v2);
    printf("MERGESORT = "); imprime(v2); printf("custo = ", custo, "\n");
$d
}

