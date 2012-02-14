#include <stdio.h>
#include <stdlib.h>
//#include "config.h"

void get_port()
{
  const char r[2]="r\0";
  FILE * f = fopen("./config.txt",r);
  int p;
  fscanf(f,"%d",&p);
  printf("%d",p);
}

int main()
{
  get_port();
  return EXIT_SUCCESS;
}
