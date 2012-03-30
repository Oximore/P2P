#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include "communicate.h"
#include "interface.h"
#include "list.h"
#include "base.h"

int sock;
struct sockaddr_in *sockaddr;
struct client_tab *tab;
struct list *peers;

int com(struct donnees * d)
{
  printf("%d\n",d->client->sock);
  while(42)
    {}
  return 0;
}

void server(struct donnees * d)
{
  init_connection(d);
  while(42)
    {
      int sinsize = sizeof(struct sockaddr_in);
      struct client * c = client_tab_add(d->ct);
      if(c==NULL)
	printf("Tableau plein\n");
      else
	{
	  c->sock = accept(d->sock, (struct sockaddr *)c->sockaddr,(socklen_t *) &sinsize);
	  if(c->sock == -1)
	    {
	      perror("accept()");
	      exit(errno);
	    }
	  d->client = c;
	  int p = pthread_create(c->t,NULL,(void *(*)(void *))&com, (void *)d);
	  if(p != 0)
	    {
	      perror("pthread_create()");
	      exit(errno);
	    }
	  
	}
    }
}

int init_connection(struct donnees * d)
{
   d->sock = socket(AF_INET, SOCK_STREAM, 0);
   d->sockaddr = malloc(sizeof(struct sockaddr_in));

   if(d->sock == -1)
   {
      perror("socket()");
      exit(errno);
   }

   d->sockaddr->sin_addr.s_addr = htonl(INADDR_ANY);
   d->sockaddr->sin_port = htons(PORT);
   d->sockaddr->sin_family = AF_INET;

   if(bind(d->sock,(struct sockaddr *) d->sockaddr, sizeof(struct sockaddr_in)) == -1)
   {
      perror("bind()");
      exit(errno);
   }

   if(listen(d->sock, MAX) == -1)
   {
      perror("listen()");
      exit(errno);
   }

   return d->sock;
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
      c->tab[i].t=malloc(sizeof(pthread_t));
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


struct donnees * donnees_init()
{
  struct donnees * d = malloc(sizeof(struct donnees));
  d->ct = client_tab_init();
  d->client = NULL;
  d->peer_list = list_init();
  d->base = base_init();
  d->sock=0;
  d->sockaddr=NULL;
  return d;
}
