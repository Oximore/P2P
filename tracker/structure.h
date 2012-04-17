#ifndef STRUCTURE_H
#define STRUCTURE_H

#include <stdio.h>
#include <stdlib.h>

struct peer
{
  struct peer * next;
  unsigned long ip_address;
  int port;
  int time;
  struct file_list * file_list;
};

struct peer_list
{
  struct peer * first;
  int size;
};

struct file
{
  struct file * next;
  char * key;
  char * name;
  int length;
  int p_size;
  struct peer_list * peer_list; 
};

struct file_list
{
  struct file * first;
  int size;
};
#endif
