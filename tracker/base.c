#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "base.h"


struct element * element_init(char * key,char * name, int length, int p_size, struct list * l)
{
  struct element * e = malloc(sizeof(struct element));
  e->next = NULL;
  e->key=key;
  e->name=name;
  e->length=length;
  e->p_size=p_size;
  e->peer_list=l;
  return e;
}

struct element * element_delete(struct element * e)
{
  struct element * e2 = e->next;
  free(e->key);
  free(e->name);
  list_delete(e->peer_list);
  return e2;
}

struct base * base_init()
{
  struct base * b=malloc(sizeof(struct base));
  b->first = NULL;
  b->size = 0;
  return b;
}

void base_delete(struct base * b)
{
  struct element * tmp = b->first;
  while(b->first != NULL)
    {
      b->first = b->first->next;
      element_delete(tmp);
      tmp = b->first;
    }
  free(b);
}

void base_add(struct base * b, struct element * e)
{
  e->next = b->first;
  b->first = e;
}


void base_element_delete(struct base * b,struct element * e)
{
  if(b->first == NULL)
    return;
  if(b->first->next == NULL)
    {
      if(strcmp(b->first->key,e->key)) 
	return;
      element_delete(b->first);
      b->size=0;
      return; 
    }
  
  struct element * p = b->first;
  if(!strcmp(p->key,e->key))
    {
      b->first = p->next;
      element_delete(p);
      b->size--;
      return;
    }
  while(p->next!=NULL )
    {
      if(!strcmp(p->next->key,e->key))
	{
	  struct element * p2 = element_delete(p->next);
	  p->next=p2;
	  b->size--;
	  return;
	}
	p = p->next;
    }
}



struct element * trouve_element(struct base * b,char * key)
{
  if(b==NULL)
    return NULL;
  if(b->first==NULL)
    return NULL;
  struct element * p= b->first;
  while(p!=NULL)
    {
      if(!strcmp(p->key,key))
	return p;
      p=p->next;
    }
  return NULL;
}
