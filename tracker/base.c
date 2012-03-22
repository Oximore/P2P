#include <stdio.h>
#include <stdlib.h>
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

void element_delete(struct element * e)
{
  free(e->key);
  free(e->name);
  list_delete(e->peer_list);
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

void base_element_delete()
{
}

int main()
{
  struct list * l = list_init();
  char * s = malloc(3*sizeof(char));
  char * t = malloc(2*sizeof(char));
  t[0]='b';
  t[1]='\0';
  s[0]='a';
  s[1]='a';
  s[2]='\0';
  list_add(l,t,1024);
  list_add(l,s,1024);
  list_add(l,t,1024);
  struct peer * p = l->first;
  while(p!=NULL)
    {
      printf("%s %d\n",p->ip_address,p->port);
      p=p->next;
    }
  list_peer_delete(l,"b");
  p=l->first;
  while(p!=NULL)
    {
      printf("%s %d\n",p->ip_address,p->port);
      p=p->next;
    }
  list_delete(l);
  return EXIT_SUCCESS;
}
