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

struct element * element_init(char * key,char * name, int length, int p_size, struct list * l);
struct element * element_delete(struct element * e);
struct base * base_init();
void base_delete(struct base * b);
void base_add(struct base * b, struct element * e);
void base_element_delete(struct base *,struct element *);
struct element * trouve_element(struct base * ,char * );
#endif
