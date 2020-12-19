/* ======================================================================== */
/* BLACKJACK v 1.0, Marco (c) 2020                                          */
/* ======================================================================== */

const int FALSE = 0;
const int TRUE = 1;

/* ------------------------------------------------------------------------ */
/* Pseudo-random number generator */
/* ------------------------------------------------------------------------ */

/* Parameter for Schrage's method */
const int M = 2147483647; 
const int A = 48271;
const int Q = 44488;
const int R = 3399;

int rand_cur;

/* Gets a random number between 0 and 2147483647 using Park & Miller LCG 
   method (1988). This implementation is restricted to signed 32-bits 
   arithmetics using the approach proposed by Schrage
 */
int rand()
{
	int div; div = rand_cur / Q;	/* max: M / Q = A = 48,271 */
	int rem; rem = rand_cur % Q;	/* max: Q - 1     = 44,487 */

	int s; s = rem * A;	/* max: 44,487 * 48,271 = 2,147,431,977 = 0x7fff3629 */
	int t; t = div * R;	/* max: 48,271 *  3,399 =   164,073,129 */
	int result; result = s - t;

	if (result < 0)
		result = result + M;
    rand_cur = result;

	return rand_cur;
}

void seed(int seed_val)
{
    rand_cur = seed_val;
}

/* returns a random number within the given boundary */
int random(int min, int max)
{
    return 1 + rand() % (max - min + 1);
}

/* ------------------------------------------------------------------------ */
/* The game */
/* ------------------------------------------------------------------------ */

/* Deals n cards and returns the value of them */
int deal_cards(int n)
{
    int return_value; return_value = 0;
    int value; value = 0;
    int i;
 
    for (i = 0; i < n; i = i + 1)
    {      
        value = random(1, 10);
        printf(" ", value);
        return_value = return_value + value;
    }
    return return_value; 
}

/* Asks player if she wants another card (a hit) and return the current player's score */
int hit(int player_score) 
{
    int new_hit; new_hit = TRUE;
    while (new_hit == TRUE) {
        printf("-- Quer outra carta (1 = sim, 0 = nao)? ");
        scanf(new_hit);
        if (new_hit == TRUE) {   
            printf("   Voce pegou a carta");
            player_score = player_score + deal_cards(1); 
            printf(" e tem agora ", player_score, "\n");
        } else 
            printf("   Voce ficou com ", player_score, " pontos\n");

        if (player_score > 21) {   
            /* player got over 21 */
            printf("   Infelizmente, voce passou de 21.\n");
            new_hit = FALSE;
        }
    }
    return player_score;
}

int house_l_human_l_21(int player_score, int house_score)
{
    if (player_score < 21) if (player_score > house_score)
        return TRUE;
    return FALSE;
}

int house_g_21_human_l_21(int player_score, int house_score)
{
    if (house_score>21) if (player_score<=21)
        return TRUE;
    return FALSE;
}

/* Determines the winner and report the game result */
int check_winner(int player_score, int house_score)
{
    int player_wins; player_wins = TRUE; 

    if (player_score == 21)
        printf("   21!! Voce ganhou!\n");
    else if (house_l_human_l_21(player_score, house_score) == TRUE)
        printf("   Voce ganhou pois foi quem chegou mais perto de 21!\n");
    else {
        if (house_g_21_human_l_21(player_score, house_score) == TRUE)
            printf("   Voce ganhou!!!!\n");
        else {
            printf("   Voce perdeu. Tente novamente.\n");
            player_wins = FALSE;
        }
    }
    return player_wins;
}

int computer_should_hit(int house, int person) {
    if (house <= 21) if (person <= 21) if (person >= house)
        return TRUE;
    return FALSE;
}

/* play one hand of blackjack and verify if the player has won the hand */
int blackjack(){
    int person, house; 
    
    /* deal the cards */
    printf("Cartas da Mesa:");
    house = deal_cards(2);
    printf(" = ", house, "\n");
    
    printf("   Suas cartas:");
    person = deal_cards(2);
    printf(" = ", person, "\n");
    
    /* new player's hit? */
    person = hit(person);

    /* computer's hit? */
    while (computer_should_hit(house, person) == TRUE)
        if (person >= house) {
            printf("   A mesa pegou a carta");
            house = house + deal_cards(1);
            printf(" e tem agora ", house, "\n");
        }

    /* and the oscar goes to... */
    return check_winner(person, house);
}

void main() {
    int wanna_play; wanna_play = 1; 
    int player_hands, house_hands, player_wins;

    player_hands = 0;
    house_hands = 0;

    /* start random generator */
    seed(42);

    /* start random generator */
    printf("=======================================================\n");
    printf("Blackjack (simples)\n");
    printf("=======================================================\n");
    while (wanna_play == 1) {
        player_wins = blackjack();
        if (player_wins == TRUE)
            player_hands = player_hands + 1;
        else
            house_hands = house_hands + 1;            
        printf("   Mesa ", house_hands, " x ", player_hands, " Voce!\n");

        printf("-------------------------------------------------------\n");
        printf("Quer outra rodada (1 = sim / 0 = nao)? ");
        scanf(wanna_play);
        printf("-------------------------------------------------------\n");
    } 
}
