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

ulong ip;
struct peer* p;
struct base* base;

int communicate(struct donnees* donnees)
{
  ip = donnees->client->sockaddr->sin_addr.s_addr;
  p = trouve_peer(donnees->peer_list, ip);
  base = donnees->base;
  
  char* recv_buffer= malloc(sizeof(char)*RECV_BUF_SIZE);
  
  char* s1 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s2 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s3 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s4 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s5 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s6 = malloc(RECV_BUF_SIZE*sizeof(char));
  int port;
  int read;
  while(1)
    {
      
      
      //read_client(client->sock, recv_buffer);
      //write_client(tab[tab_ind].sock, send_buffer)
      // tester si on a reçu avant de parser encore !!!!!!!!
      
      read=read_client(donnees->client->sock, recv_buffer);
      if(read > 0)
	{
	  
	  switch(recv_buffer[0])
	    {
	    case'a':
	      if(recv_buffer[read-1] != ']') printf("le recv a merdé");//gérer les messages en plusieurs paquets
	      else
		{
		  sscanf(recv_buffer,"%s %s %d %s [%s] %s [%s]",s1, s2, &port, s3, s4, s5, s6);
		  if(strcmp(s1,"announce")==0 && strcmp(s2,"listen")==0 && strcmp(s3,"seed")==0 && strcmp(s5,"leech")==0)
		    {
		      if(p == NULL)
			{
			  list_add(donnees->peer_list , ip , port);
			  p=trouve_peer(donnees->peer_list, ip);
			}
		      char* res=fusion_keys_string(s4, s6);
		      //remplit_announce(res);// A faire
		      free(res);
		      write_client(donnees->client->sock, "ok");
		    }
		}
	      break;
	      
	    case'u':// 
	      sscanf(recv_buffer,"%s %s [%s] %s [%s]",s1 ,s2, s3, s4, s5);
	      if(strcmp(s1,"update")==0 && strcmp(s2,"seed")==0 && strcmp(s4,"leech")==0)
		{
		  if(p==NULL){end(donnees->client, donnees->ct); return 0;}//ferme la socket
		  else
		    { 
		      char* res=fusion_keys_string(s3, s5);
		      remplit_update(res);
		      free(res);
		      write_client(donnees->client->sock, "ok");
		    }	    
		}
	      break;
	      
	    default:
	      end(donnees->client,donnees->ct);
	      return 0;
	      
	      //case''
	    }
	  
	}
      
    }
  return EXIT_SUCCESS;
}
  
void end(struct client* client, struct client_tab* tab)
  {
    close(client->sock);
    client_tab_delete_client(tab,client);
  }


void remplit_update(char* keys)//met a jour la liste des pairs et previous_update
{
  
  struct base* a_ajouter=keys_string_to_base(keys);
  struct base* a_stocker=keys_string_to_base(keys);
  struct base* a_enlever=p->previous_update;
    
  // on a considere qu'il n'y a pas de cles en double dans seeds
  
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
  


  elt=a_ajouter->first;
  while(elt!=NULL)
    {
      struct element* element=trouve_element(base, elt->key);
      list_add(element->peer_list, ip, p->port);
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

/*
void remplit_announce(char* s)
{
return;}
*/


char* fusion_keys_string(char* seed, char* leech)
{
  int seed_l=strlen(seed);
  int leech_l=strlen(leech);
  char* res= malloc(sizeof(char)*(seed_l+leech_l+1));
  int i;
  for(i=0;i<seed_l;i++)
    {
      res[i]=seed[i];
    } 
  for(i=0;i<leech_l;i++)
    {
      res[i+seed_l]=leech[i];
    }
 res[seed_l+leech_l]='\0';
 return res;
}

struct base* keys_string_to_base(char* keys)
{
  
  struct base* new_base=base_init();
  char* s=malloc(sizeof(char)*50);//taille de cle<50 ???
  int i=0;
  int j=0;
  while(keys[i]!='\0')
    {
      if(keys[i]==' ') 
	{
	  s[j]='\0';
	  struct element* e=element_init(s,NULL,0,0,NULL);	  
	  base_add(new_base, e);
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
  struct element* e=element_init(s,NULL,0,0,NULL);	  
  base_add(new_base, e);
  return new_base;
  
}
