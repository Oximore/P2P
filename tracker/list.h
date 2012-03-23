#ifndef LIST_H
#define LIST_H

#include <stdio.h>
#include <stdlib.h>

struct peer
{
  struct peer * next;
  unsigned long ip_address;
  int port;
  int time;
  struct base * previous_update;
};

struct list
{
  struct peer * first;
  int size;
};

struct list * list_init();
void list_delete(struct list * );
void list_add(struct list * , unsigned long ,int );
void list_peer_delete(struct list *,unsigned long );
struct peer * peer_init(unsigned long , int);
struct peer * peer_delete(struct peer * );
struct peer * trouve_peer(struct list * ,unsigned long);


#endif
