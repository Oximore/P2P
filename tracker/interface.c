#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

#include "interface.h"
#include "list.h"
#include "base.h"

int sock;
struct sockaddr_in *sockaddr;
struct client_tab *tab;
struct list *peers;

int communicate(struct client * c)
{
  printf("%d\n",c->sock);
  return 0;
}

void server(void)
{
  init_connection();
  tab = client_tab_init();
  while(42)
    {
      int sinsize = sizeof(struct sockaddr_in);
      struct client * c = client_tab_add(tab);
      c->sock = accept(sock, (struct sockaddr *)c->sockaddr,(socklen_t *) &sinsize);
      printf("coucou");
      if(c->sock == -1)
	{
	  perror("accept()");
	  exit(errno);
	}
      int p = pthread_create(c->t,NULL,(void *(*)(void *))&communicate, (void *)c);
      if(p != 0)
	{
	  perror("pthread_create()");
	  exit(errno);
	}
      
    }
}

int init_connection(void)
{
   sock = socket(AF_INET, SOCK_STREAM, 0);
   sockaddr = malloc(sizeof(struct sockaddr_in));

   if(sock == -1)
   {
      perror("socket()");
      exit(errno);
   }

   sockaddr->sin_addr.s_addr = INADDR_ANY;//htonl(INADDR_ANY);
   sockaddr->sin_port = htons(PORT);
   sockaddr->sin_family = AF_INET;

   if(bind(sock,(struct sockaddr *) sockaddr, sizeof(struct sockaddr_in)) == -1)
   {
      perror("bind()");
      exit(errno);
   }

   if(listen(sock, MAX) == -1)
   {
      perror("listen()");
      exit(errno);
   }

   return sock;
}

void end_connection(int sock)
{
   close(sock);
}

int read_client(int sock, char *buffer)
{
   int n = 0;

   if((n = recv(sock, buffer, BUF_SIZE - 1, 0)) < 0)
   {
      perror("recv()");
      /* if recv error we disonnect the client */
      n = 0;
   }

   buffer[n] = 0;

   return n;
}

void write_client(int sock, const char *buffer)
{
   if(send(sock, buffer, strlen(buffer), 0) < 0)
   {
      perror("send()");
      exit(errno);
   }
}


struct client_tab * client_tab_init()
{
  struct client_tab *c = malloc(sizeof(struct client_tab));
  int i;
  for(i=0;i<MAX;i++)
    {
      c->b[i]=0;
      c->tab[i].sockaddr=malloc(sizeof(struct sockaddr_in));
    }
  return c;
}

struct client * client_tab_add(struct client_tab *t)
{
  int i=0;
  while(i<MAX && t->b[i]!=0)
    i++;
  if(i==MAX)
    return NULL;
  t->b[i]=1;
  return &(t->tab[i]);
}

void client_tab_delete_client(struct client_tab *t,struct client *c)
{
  int i=0;
  while(i<MAX && t->tab[i].sock != (*c).sock)
    i++;
  if(i==MAX)
    return;
  t->b[i]=0;
}
