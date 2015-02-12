#include <stdio.h>
#include <errno.h>
#include <string.h>
 
#include <wiringPi.h>
#include <softTone.h>
 
#define PIN 3
 
int scale [23] = { 659, 659, 0, 659, 0, 523, 659, 0, 784, 0,0,0, 392, 0,0,0, 523, 0,0, 392, 0,0,330 } ;
 
int main ()
{
  int i ;
  wiringPiSetup () ;
  softToneCreate (PIN) ;
  for (i = 0 ; i < 23 ; ++i)
    {
      printf ("%3d\n", i) ;
      softToneWrite (PIN, scale [i]) ;
      delay (200) ;
    }
}
