#ifndef LIST_H
#define LIST_H

#define MAX 20
#define PORT 1564
#define BUF_SIZE 1024

struct client
{
  int sock;
  struct sockaddr_in *sockaddr;
  pthread_t *t; //utile?
};

struct client_tab
{
  struct client tab[MAX];
  int b[MAX];
};

struct client_tab * client_tab_init()
{
  struct client_tab *c = malloc(sizeof(struct client_tab));
  int i;
  for(i=0;i<MAX;i++)
    c->b[i];
  return c;
}

void client_tab_add(struct client_tab *t,struct client *c)
{
  int i=0;
  while(i<MAX && t->b[i]!=0)
    i++;
  if(i==MAX)
    printf("il n'y a plus de place");
  else
    {
      t->tab[i]=*c;
      t->b[i]=1;
    }
}

void client_tab_delete_client(struct client_tab *t,struct client *c)
{
  int i=0;
  while(i<MAX && t->tab != *c)
}

void server(void);
int init_connection(void);
void end_connection(int sock);
int read_client(int sock, char *buffer);
void write_client(int sock, const char *buffer);





#endif
