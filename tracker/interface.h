#ifndef INTERFACE_H
#define INTERFACE_H

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h> /* close */
#include <netdb.h> /* gethostbyname */
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include "structure.h"
//#include "communicate.h"


#define MAX 20

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
  struct client * client;
  struct peer_list * peer_list;
  struct file_list * file_list;
  int sock;
  struct sockaddr_in * sockaddr;
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
