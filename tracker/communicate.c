#include <stdio.h>
#include <stdlib.h>
#include "communicate.h"
#include <time.h>
#include "structure.h"
#include "interface.h"
#include <string.h>

#define RECV_BUF_SIZE 4096
//#define SEND_BUF_SIZE 4096
#define IDLE_TIME // en secondes
/*
int main()
{
  return 0;
}
*/
int communicate(struct donnees* donnees)
{
  struct client* client=donnees->client;
  ulong ip = client->sockaddr->sin_addr.s_addr;
  struct peer* peer = find_peer(donnees->peer_list, ip);
  struct file_list* file_list = donnees->file_list;
  struct peer_list* peer_list = donnees->peer_list;
  
  char* recv_buffer= malloc(sizeof(char)*RECV_BUF_SIZE);
  
  char* s1 = malloc(RECV_BUF_SIZE);
  char* s2 = malloc(RECV_BUF_SIZE);
  char* s3 = malloc(RECV_BUF_SIZE);
  char* s4 = malloc(RECV_BUF_SIZE);
  char* s5 = malloc(RECV_BUF_SIZE);
  char* s6 = malloc(RECV_BUF_SIZE);
  char* s0 = malloc(RECV_BUF_SIZE);
  char* tab[7];
  tab[0]=s0;
  tab[1]=s1;
  tab[2]=s2;
  tab[3]=s3;
  tab[4]=s4;
  tab[5]=s5;
  tab[6]=s6;
  int port;
  int read;
  int decalage;
  int length;
  int piece_size;
   
  while(1)
    {
      
      
      read=read_client(client->sock, recv_buffer);
      if(read > 0)
	{
	  
	  switch(recv_buffer[0])
	    {
	    case'a':
	      decalage=0;
	      while(compte_crochet_fermant(recv_buffer)<2)
		{
		  decalage+=read;
		  read=read_client(client->sock, recv_buffer + decalage);
		}	  
	      recv_buffer[decalage+read]='\0';// not sure
	      parse(recv_buffer, tab);
	      port = atoi(s2);
	      if(strcmp(s0,"announce")==0 && strcmp(s1,"listen")==0 && strcmp(s3,"seed")==0 && strcmp(s5,"leech")==0)
		{
		  if(peer == NULL)
		    {
		      peer = peer_init(ip, port);
		      peer_list_add(peer_list, peer);
		    }
		  while(strlen(s4) > 0)
		    {
		      if(compte_espace(s4)>3)
			{		 
			  sscanf(s4, "%s %d %d %s %s", s1, &length, &piece_size, s2, s4);
			}
		      else
			{
			  sscanf(s4, "%s %d %d %s", s1, &length, &piece_size, s2);
			  s4[0]='\0';			
			}
		      //si le fichier n'existe pas on le cree
		      struct file* file = find_file(file_list, s1);
		      if(NULL == file)
			{
			  file = remplit_file(s1, length, piece_size, s2);
			  file_list_add(file_list, file);
			}
		      add_link(file, peer); 
		    }
		  struct file_list* f_add = keys_string_to_file_list(s6);
		  //on suppose qu'il ne leech pas un fichier qu'il a declare avoir...
		  update_add(file_list, peer, f_add);
		  file_list_delete(f_add);
		  write_client(client->sock, "ok");
		}
	      break;
	      
	    case'u':
	      decalage=0;
	      while(compte_crochet_fermant(recv_buffer)<2)
		{
		  decalage+=read;
		  read=read_client(client->sock, recv_buffer + decalage);
		}	  
	      recv_buffer[decalage+read]='\0'; //not sure
	      parse(recv_buffer, tab);
	      if(strcmp(s0,"update")==0 && strcmp(s1,"seed")==0 && strcmp(s3,"leech")==0)
		{
		  if(peer==NULL){end(client, donnees->ct); return 0;}//ferme la socket
		  else
		    { 
		      char* res=fusion_keys_string(s2, s4);
		      struct file_list* f = keys_string_to_file_list(res);
		      update_diff(f, peer->file_list, file_list, peer);
		      file_list_delete(f);
		      free(res);
		      write_client(client->sock, "ok");
		    }	    
		}
	      
	      break;
	      
	    default:
	      printf("entree non valide");
	      end(client,donnees->ct);
	      return 0;
	      
	     
	    }
	  
	}
      
    }
  return EXIT_SUCCESS;
}
  
void end(struct client* client, struct client_tab* ct)
  {
    close(client->sock);
    client_tab_delete_client(ct,client);
  }

void update_diff(struct file_list* new, struct file_list* old, struct file_list* file_list, struct peer* peer)
{
  struct file_list* f_add=file_list_copy(new);
  struct file_list* f_delete=file_list_copy(old);
  //le diff
  if(f_add->first == NULL) update_delete(file_list, peer, f_delete);
  else 
    {
      struct file* aux_file=f_add->first;
      struct file* aux_file2=NULL;      
      while(aux_file!=NULL)
	{
	  aux_file2=find_file(f_delete, aux_file->key);
	  if(aux_file2!=NULL)
	    {
	      file_list_file_delete(NULL, f_delete, aux_file->key);
	      aux_file2=aux_file;
	      aux_file=aux_file->next;
	      file_list_file_delete(NULL, f_add, aux_file2->key);
	    }
	  else aux_file=aux_file->next;
	}
      update_add(file_list, peer, f_add);
      update_delete(file_list, peer, f_delete);
    }  
  file_list_delete(f_add);
  file_list_delete(f_delete);
}

char* fusion_keys_string(char* seed, char* leech)
{
  int esp_dec=0;//ajout d'un espace si les 2 sont non vides
  int seed_l=strlen(seed);
  int leech_l=strlen(leech);
  char* res= malloc(sizeof(char)*(seed_l+leech_l+1));
  int i;
  for(i=0;i<seed_l;i++)
    {
      res[i]=seed[i];
    } 
  if(seed_l>0 && leech_l>0)
    {
      res[seed_l]=' ';
      esp_dec=1;
    }
  for(i=0;i<leech_l;i++)
    {
      res[i+seed_l+esp_dec]=leech[i];
    }
 res[seed_l+leech_l+esp_dec]='\0';
 return res;
}

struct file_list* keys_string_to_file_list(char* keys)
//cree une liste temporaire de file pour le update
{
  
  struct file_list* new_file_list=file_list_init();
  char * tab[100];//moins de 100 clés ?
  tab[0]=malloc(sizeof(char)*50);//tailles de clés<50 char ?
  int k=0; 
  int i=0;
  int j=0;
  int l;
  while(keys[i]!='\0')
    {
      if(keys[i]==' ') 
	{
	  tab[k][j]='\0';
	  tab[k+1]=malloc(sizeof(char)*50);
	  k++;
	  j=-1;
	}
      else
	{
	  tab[k][j]=keys[i];
	}
      j++;
      i++;
    }
  tab[k][j]='\0';
  for(l=0;l<k+1;l++)
    {
      file_list_add(new_file_list, file_init(tab[l],NULL,0,0));
    }
  return new_file_list;
}

struct file* remplit_file(char* filename, int length, int piece_size, char* key)
{
  char* new_filename = malloc(sizeof(filename));
  char* new_key = malloc(sizeof(key));
  return file_init(new_key, new_filename, length, piece_size);
}


int compte_crochet_fermant(char* buf)
{
  int i=0;
  int j=0;
  while(buf[j]!='\0')
    {
      if(buf[j]==']') i++;
      j++;
    }
  return i;
}

int compte_espace(char* buf)
{
  int i=0;
  int j=0;
  while(buf[j]!='\0')
    {
      if(buf[j]==' ') i++;
      j++;
    }
  return i;
}

void parse(char* buf, char** tab)
// remplit les char* du tableau tab à ârtir de buf 
// buf doit finir par \0
{
  int crochet_ouvert=0;
  int i=0;
  int k=0;//indice dans tab
  int j=0;
  while(buf[i]!='\0')
    {
      if(crochet_ouvert==1)
	{
	  if(buf[i]==']')
	    {
	      crochet_ouvert=0;
	    }
	  else
	    {
	      tab[k][j]=buf[i];
	      j++;
	    }
	}
      else if(buf[i]=='[') crochet_ouvert=1;
      else if(buf[i]==' ')
	{
	  tab[k][j]='\0';
	  j=0;
	  k++;
	}
      else
	{
	  tab[k][j]=buf[i];
	  j++;
	}
       i++;
    }
  tab[k][j]='\0';
  while(k<6)
    { 
      k++;
      tab[k][0]='\0';
    }
}


