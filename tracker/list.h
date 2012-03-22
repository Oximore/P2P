#ifndef LIST_H
#define LIST_H

#include <stdio.h>
#include <stdlib.h>

struct peer
{
  struct peer * next;
  char * ip_address;
  int port;
  int time;
};

struct list
{
  struct peer * first;
  int size;
};

struct list * list_init();
void list_delete(struct list * );
void list_add(struct list * , char * ,int );
void list_peer_delete(struct list *,char * );
struct peer * peer_init(char * , int);
struct peer * peer_delete(struct peer * );


#endif
