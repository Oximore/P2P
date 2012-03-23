#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h> /* close */
#include <netdb.h> /* gethostbyname */
#include <pthread.h>
#define PORT 1564


int main()
{
  int sock = socket(AF_INET, SOCK_STREAM, 0);
  if(sock == -1)
    {
      perror("socket()");
      exit(errno);
    }
  struct sockaddr_in myaddr;
  myaddr.sin_port = htons(PORT); /* on utilise htons pour le port */
  myaddr.sin_family = AF_INET;
  inet_aton("127.0.0.1", &myaddr.sin_addr);
  
  if(connect(sock,(struct sockaddr *) &myaddr, sizeof(struct sockaddr)) == -1)
    {
      perror("connect()");
      exit(errno);
    }
  return EXIT_SUCCESS;
}
