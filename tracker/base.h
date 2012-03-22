#ifndef BASE_H
#define BASE_H

#include <stdio.h>
#include <stdlib.h>
#include "list.h"

struct element
{
  struct element * next;
  char * key;
  char * name;
  int length;
  int p_size;
  struct list * peer_list; 
};

struct base
{
  struct element * first;
  int size;
};

#endif
