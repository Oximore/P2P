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
#define PORT 4012
#define BUF_SIZE 40
void write_client(int sock, const char *buffer)
{
   if(send(sock, buffer, strlen(buffer), 0) < 0)
   {
      perror("send()");
      exit(errno);
   }
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

   buffer[n] = '\0';

   return n;
}


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
  write_client(sock,"je suis le pair");
  char c[BUF_SIZE];
  int r;
  while(1)
    {
      r = read_client(sock,c);
      if(r!=0)
	printf("%s\n",c);
    }
  return EXIT_SUCCESS;
}
