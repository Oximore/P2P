#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

#include "interface.h"

int sock;
sockaddr_in sin;
struct client tab[MAX];


void server(void)
{
  init_connection();
  while(42)
    {
      int sinsize = sizeof sin;
      
      csock = accept(sock, (struct sockaddr *)&csin, &sinsize);
      
      if(csock == -1)
	{
	  perror("accept()");
	  exit(errno);
	}
    }
}

int init_connection(void)
{
   sock = socket(AF_INET, SOCK_STREAM, 0);
   sin = { 0 };

   if(sock == -1)
   {
      perror("socket()");
      exit(errno);
   }

   sin.sin_addr.s_addr = htonl(INADDR_ANY);
   sin.sin_port = htons(PORT);
   sin.sin_family = AF_INET;

   if(bind(sock,(sockaddr *) &sin, sizeof sin) == -1)
   {
      perror("bind()");
      exit(errno);
   }

   if(listen(sock, MAX_CLIENTS) == -1)
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
