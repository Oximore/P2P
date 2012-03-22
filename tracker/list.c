#include "list.h"
#include <string.h>

struct list * list_init()
{
  struct list * l = malloc(sizeof(struct list));
  l->first = NULL;
  l->size = 0;
  return l;
}

void list_delete(struct list * l)
{
  struct peer * p;
  if(l->first==NULL)
    {
      free(l);
      return ;
    }
  while(l->first->next!=NULL)
    {
      p = l->first->next;
      peer_delete(l->first);
      l->first = p;
    }
  peer_delete(l->first);
  free(l);
}

void list_add(struct list * l, char * ip_address,int port)
{
  struct peer * p = peer_init(ip_address,port);
  p->next = l->first;
  l->first = p;
  l->size++;
}

void list_peer_delete(struct list * l,char * ip_address)
{
  if(l->first == NULL)
    return;
  if(l->first->next == NULL)
    {
      if(strcmp(l->first->ip_address,ip_address)!=0) 
	return;
      peer_delete(l->first);
      l->size=0;
      return; 
    }
  
  struct peer * p = l->first;
  if(!strcmp(p->ip_address,ip_address))
    {
      l->first = p->next;
      peer_delete(p);
      l->size--;
      return;
    }
  while(p->next!=NULL )
    {
      if(strcmp(p->next->ip_address,ip_address)==0)
	{
	  struct peer * p2 = peer_delete(p->next);
	  p->next=p2;
	  l->size--;
	  return;
	}
	p = p->next;
    }
}

struct peer * peer_init(char * ip_address, int port)
{
  struct peer * p=malloc(sizeof(struct peer));
  p->next=NULL;
  p->ip_address = ip_address;
  p->port=port;
  p->time = 0;
  return p;
}

struct peer * peer_delete(struct peer * p)
{
  struct peer * p2 = p->next;
  free(p->ip_address);
  free(p);
  return p2;
}


//list_appartient
