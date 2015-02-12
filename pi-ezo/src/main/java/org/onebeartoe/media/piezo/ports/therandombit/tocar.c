//Definiçao dos periodos para cada nota (*0.0000001)
#define C    1911
#define C1    1804
#define D    1703
#define Eb    1607
#define E    1517
#define F    1432
#define F1    1352
#define G    1276
#define Ab    1204
#define A    1136
#define Bb    1073
#define B    1012
#define c       955
#define c1      902
#define d       851
#define eb      803
#define e       758
#define f       716
#define f1      676
#define g       638
#define ab      602
#define a       568
#define bb      536
#define b       506
 
#define p       0  //pausa
 
int speaker = 6;    //porta do arduino
long vel = 20000;
 
void setup() {
    pinMode(speaker, OUTPUT);
    delay(2000);
}

int peergynt_m[] = {G, E, D, C, D, E, G, E, D, C, D, E, D, E,G, E, G, A, E, A, G, E, D, C};
int peergynt_r[] = {8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 4, 4, 4, 4, 8, 8, 8, 8, 8, 8, 8, 8, 8, 16};
 
int smoke_m[] = {E, G, A, E, G, Bb, A, E, G, A, G, E};
int smoke_r[] = {12, 12, 18, 12, 12, 6, 18, 12, 12, 18, 12, 24};
 
int natal_m[] = {G, A, G, E, G, A, G, E, c, c, A, B, B, G, A, G, A, c, B, A, G, A, G, E};
int natal_r[] = {12, 4, 8, 16, 12, 4, 8, 16, 12, 4, 16, 12, 4, 16, 12, 4, 8, 8, 8, 8, 12, 4, 8, 16};
 
int LTS_m[] = {Bb, G, G, Bb, G, G, Bb, ;G, G, Bb, G, G, Bb, G, C, G, Bb, G, G, Bb, G, G, Bb, G, G, Bb, G, G, Bb, G, F, D, F, D, G, F, D, C, Bb, G, Bb, C, C1, C, Bb, F, D, Bb, G, F, D, C, Bb, D, C, Bb, G} ;
int LTS_r[] = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
 
int melod[] = {e, e, e, c, e, g, G, c, G, E, A, B, Bb, A, G, e, g, a, f, g, e, c, d, B, c};
int ritmo[] = {6, 12, 12, 6, 12, 24, 24, 18, 18, 18, 12, 12, 6, 12, 8, 8, 8, 12, 6, 12, 12, 6, 6, 6, 12};
 
void loop() {
    for (int i=0; i<42; i++) {
        int tom = melod[i];
        int tempo = ritmo[i];
 
        long tvalue = tempo * vel;
 
        tocar(tom, tvalue);
 
        delayMicroseconds(1000); //pausa entre notas!
    }
    delay(1000);
}
 
void tocar(int tom, long tempo_value) {
     long tempo_gasto = 0;
     while (tempo_gasto < tempo_value) {
        digitalWrite(speaker, HIGH);
        delayMicroseconds(tom / 2);
 
        digitalWrite(speaker, LOW);
        delayMicroseconds(tom/2);
 
        tempo_gasto += tom;
    }
}
