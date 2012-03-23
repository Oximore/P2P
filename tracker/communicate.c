#include <stdio.h>
#include <stdlib.h>
#include "communicate.h"
#include <time.h>
#include "base.h"
#include "interface.h"
#include <string.h>

#define RECV_BUF_SIZE 1024
//#define SEND_BUF_SIZE 1024
#define IDLE_TIME // en secondes

int communicate(struct client * client,struct client_tab* tab, struct list* peers, struct base* base)
{
  ulong ip=client->sockaddr->sin_addr.s_addr;

  char* recv_buffer= malloc(sizeof(char)*RECV_BUF_SIZE);
    
  char* s1 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s2 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s3 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s4 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s5 = malloc(RECV_BUF_SIZE*sizeof(char));
  int port;
  while(1)
    {
      
      read_client(client->sock, recv_buffer);
      //write_client(tab[tab_ind].sock, send_buffer)
      
      switch(recv_buffer[0])
	{
	case'a':
	  sscanf(recv_buffer,"%s %s %d %s [%s]",s1, s2, &port, s3, s4);
	  if(strcmp(s1,"announce")==0 && strcmp(s2,"have")==0)
	    {
	      
	      if(trouve_peer(peers, ip)==NULL)
		{
		  list_add(peers , ip , port);
		}
	      remplit_announce(base, s4);// A faire
	      write_client(client->sock, "ok");
	    }
	  break;
	 
	case'u':
	  sscanf(recv_buffer,"%s %s [%s] %s [%s]",s1 ,s2, s3, s4, s5);
	  if(strcmp(s1,"update")==0 && strcmp(s2,"seed")==0 && strcmp(s4,"leech")==0)
	    {
	      if(trouve_peer(peers, ip)==NULL){end(client, tab); return 0;}//ferme la socket
	      else
		{
		  remplit_update(peers,base, s3, ip);
		  write_client(client->sock, "ok");
		}	    
	    }
	  break;
	  
	default:
	  end(client, tab);
	  return 0;
	  
	  //case''
	}
      
  
      
    }
  return EXIT_SUCCESS;
}
  
void end(struct client* client, struct client_tab* tab)
  {
    close(client->sock);
    client_tab_delete_client(tab,client);
  }


void remplit_update(struct list* peers, struct base *base,char* keys, int ip)//met a jour la liste des pairs et previous_update
{
  
struct base* a_ajouter=base_init();
struct base* a_stocker=base_init();
struct peer* p=trouve_peer(peers, ip);


  if(p==NULL){
    printf("trouve_peer");
    return;}
  struct base* a_enlever=p->previous_update;
  //on remplit a_ajouter
  char* s=malloc(sizeof(char)*50);
  int i=0;
  int j=0;
  while(keys[i]!='\0')
    {
      if(keys[i]==' ') 
	{
	  s[j]='\0';
	  struct element* e=element_init(s,NULL,0,0,NULL);	  
	  struct element* f=element_init(s,NULL,0,0,NULL);	  
	  base_add(a_ajouter, e);
	  base_add(a_stocker, f);
	  j=-1;
	}
      else
	{
	  s[j]=keys[i];
	}
      j++;
      i++;
    }
  s[j]='\0';
  struct element* f=element_init(s,NULL,0,0,NULL);	  
  struct element* e=element_init(s,NULL,0,0,NULL);	  
  base_add(a_ajouter, e);
  base_add(a_stocker, f);
// on a considere qu'il n'y a pas de clés en double dans seeds
// on onleve les elements a la fois dans a_ajouter et a_enlever (cf key)
  p->previous_update=a_stocker;
  struct element* elt=a_ajouter->first;
  struct element* aux;
  while(elt!=NULL)
    {
      struct element* elt2=a_enlever->first;
      struct element* aux2;      
      while(elt2!=NULL)
	{
	  if(strcmp(elt->key,elt2->key)==0)
	    {
	      aux=elt;
	      elt=elt->next;
	      aux2=elt2;
	      elt2=elt2->next;
	      base_element_delete(a_ajouter,aux);
	      base_element_delete(a_enlever,aux2);
	    }
	}
    }
  


  struct peer* peer= trouve_peer(peers,ip);
  
  elt=a_ajouter->first;
  while(elt!=NULL)
    {
      struct element* element=trouve_element(base, elt->key);
      list_add(element->peer_list, ip, peer->port);
    }
  
  elt=a_enlever->first;
  while(elt!=NULL)
    {
      struct element* element=trouve_element(base, elt->key);
      list_peer_delete(element->peer_list, ip);
    }
  free(a_ajouter);
  free(a_enlever);
}

void remplit_announce(struct base* base,char* s)
{
return;

}
