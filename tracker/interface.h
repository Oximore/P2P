#ifndef INTERFACE_H
#define INTERFACE_H

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h> /* close */
#include <netdb.h> /* gethostbyname */
#include <pthread.h>

#define MAX 20
//#define PORT 1564
#define BUF_SIZE 1024

struct client
{
  int sock;
  struct sockaddr_in *sockaddr;
  pthread_t *t; 
};

struct client_tab
{
  struct client tab[MAX];
  int b[MAX];
};

struct donnees
{
  struct client_tab *ct;
  struct client * client;// ne sert a rien?
  struct list * peer_list;
  struct base * base;
  int sock;
  struct sockaddr_in * sockaddr;
};

struct donnees_function{
  struct donnees * _ref;
  struct client * current;
};


struct client_tab * client_tab_init();
struct client * client_tab_add(struct client_tab *);
void client_tab_delete_client(struct client_tab *,struct client *);
void server(struct donnees *);
int init_connection(struct donnees *);
void end_connection(int sock);
int read_client(int sock, char *buffer);
void write_client(int sock, const char *buffer);
struct donnees * donnees_init();
int get_port();



#endif
