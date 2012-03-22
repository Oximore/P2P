#include "stdlib.h"
#include "stdio.h"
#include "client.h"

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <errno.h>

#define PORT 1234
#define NB_CONNECTION 5
#define BUF_SIZE 1000


int main(int argc, char * argv[])
{
 

  struct socket csock;
  struct sockaddr_in csin;
  int sinsize = sizeof(csin);
  csock = accept(sock, (struct sockaddr *)&csin, &sonsize);
  if(csock == -1)
    {
      perror("accept()");
      exit(errno);
    }
//int bind(int sockfd, struct sockaddr *my_addr, socklen_t addrlen);
//int recv(int s, void *buf, int len, unsigned int flags);
//int send(int socket, const void *msg, size_t len, int flags);
//int listen(int s, int backlog);
//int accept(int sock, struct sockaddr *adresse, socklen_t *longueur);
  close(sock);
  close(csock);
  return EXIT_SUCCESS;
}

    


void end_connection(int sock)
{
  close(sock);
}



int init_connection(void)
{
  int sock = socket(AF_INET, SOCK_STREAM, 0);
  if(sock == -1)
    {
      perror("invalid socket");
      exit(errno);
    }
  struct sockaddr_in sin;
  
  sin.sin_family=AF_INET;
  sin.sin_addr.s_addr=htonl(INADDR_ANY);
  sin.sin_port=htons(PORT);
  
  if(bind(sock, (struct sockaddr *)&sin, sizeof(sin))==-1)
    {
      perror("bond");
      exit(errno);
    }
  
  if(listen(sock, NB_CONNECTION)==-1)
    {
      perror("listen()");
      exit(errno);
    }
  return sock;

}

void app()
{
  socket sock = init_connection();
  char buffer[BUF_SIZE];
  int actual=0;
  //int max=sock;

}
