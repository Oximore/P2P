#include <stdio.h>
#include <stdlib.h>
#include "communicate.h"
#include <time.h>
#include "structure.h"
#include "interface.h"
#include <string.h>
#include <math.h>

#define RECV_BUF_SIZE 4096
#define SEND_BUF_SIZE 4096
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
/*
int main()
{
  return 0;
}
*/
int communicate(struct donnees* donnees)
{
  printf("communicate\n");
  struct client* client=donnees->client;
  ulong ip = client->sockaddr->sin_addr.s_addr;
  struct peer* peer = find_peer(donnees->peer_list, ip);
  struct file_list* file_list = donnees->file_list;
  struct peer_list* peer_list = donnees->peer_list;
  int refresh_time=get_refresh_time();
  printf("refresh time:%d\n",refresh_time);
  char* recv_buffer= malloc(sizeof(char)*RECV_BUF_SIZE);
  char* send_buffer= malloc(sizeof(char)*SEND_BUF_SIZE);  
  char* s1 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s2 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s3 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s4 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s5 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s6 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* s0 = malloc(RECV_BUF_SIZE*sizeof(char));
  char* saux = malloc(RECV_BUF_SIZE*sizeof(char));
  char** tab = malloc(7*sizeof(char *));;
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
      
      recv_buffer[0]='\0';
      send_buffer[0]='\0';
      read=read_client(client->sock, recv_buffer);
      if(read > 0)
	{
	  print_data(file_list, peer_list);	  
	  printf("received:\n");
	  switch(recv_buffer[0])
	    {
	     pthread_mutex_lock(& mutex);
	    case'a':
	      decalage=0;
	      while(compte_crochet_fermant(recv_buffer)<2)
		{
		  decalage+=read;
		  read=read_client(client->sock, recv_buffer + decalage);
		}	  
	      recv_buffer[decalage+read]='\0';
	      printf("%s\n", recv_buffer);
	      parse(recv_buffer, tab);
	      //printf("parse ok\n");
	      port = atoi(s2);
	      if(strcmp(s0,"announce")==0 && strcmp(s1,"listen")==0 && strcmp(s3,"seed")==0 && strcmp(s5,"leech")==0)
		{
		  
		  if(peer == NULL)
		    {
		      peer = peer_init(ip, port);
		      peer_list_add(peer_list, peer);
		    }
		  int i=0;
		  while(strlen(s4+i) > 0)
		    {
		      if(compte_espace(s4+i)>3)
			{		 
			  
			  sscanf(s4+i, "%s %d %d %s", s1, &length, &piece_size, s2);
			  
			  sprintf(saux, "%s %d %d %s", s1, length, piece_size, s2);
			  struct file* file = find_file(file_list, s1);
			  if(NULL == file)
			    {
			      file = remplit_file(s1, length, piece_size, s2);
			      file_list_add(file_list, file);
			    }
			  add_link(file, peer); 
			  i=strlen(saux)+1;
			  
			}
		      else
			{
			  
			  sscanf(s4+i, "%s %d %d %s", s1, &length, &piece_size, s2); 
			  struct file* file = find_file(file_list, s1);
			  if(NULL == file)
			    {
			      file = remplit_file(s1, length, piece_size, s2);
			      file_list_add(file_list, file);
			    }
			  add_link(file, peer); 
			  s4[i]='\0';			
			}
		      //si le fichier n'existe pas on le cree
		    }
		  struct file_list* f_add = keys_string_to_file_list(s6);
		  //on suppose qu'il ne leech pas un fichier qu'il a declare avoir...
		  update_add(file_list, peer, f_add);
		  file_list_delete(f_add);
		  write_client(client->sock, "ok");
		  print_data(file_list, peer_list);
		  printf("replied:ok\n");
		  
		}
	      break;
	      
	    case'u':
	      decalage=0;
	      while(compte_crochet_fermant(recv_buffer)<2)
		{
		  decalage+=read;
		  read=read_client(client->sock, recv_buffer + decalage);
		}	  
	      recv_buffer[decalage+read]='\0'; 
	      printf("%s\n", recv_buffer);	      
	      parse(recv_buffer, tab);
	      if(strcmp(s0,"update")==0 && strcmp(s1,"seed")==0 && strcmp(s3,"leech")==0)
		{
		  if(peer==NULL){
		    end(client, donnees->ct);
		    //printf("le pair n'existe pas...\n");
		    return 0;}//ferme la socket
		  else
		    { 
		      //printf("le pair existe\n");
		      char* res=fusion_keys_string(s2, s4);
		      struct file_list* f = keys_string_to_file_list(res);
		      update_diff(f, peer->file_list, file_list, peer);
		      file_list_delete(f);
		      free(res);
		      write_client(client->sock, "ok");
		      printf("replied:ok\n");
		  
		    }	    
		}
	      break;
	      
	    case'l':
	      decalage=0;
	      while(compte_crochet_fermant(recv_buffer)<1)
		{
		  decalage+=read;
		  read=read_client(client->sock, recv_buffer + decalage);
		}	  
	      recv_buffer[decalage+read]='\0'; 
	      printf("%s\n", recv_buffer);	      
	      parse(recv_buffer, tab);
	      if(strcmp(s0,"look")==0)
		{
		  char* filename="filename";
		  int egal=1;
		  int i;
		  s2[0]='\n';
		  for(i=0;i<8;i++)
		    {
		      if(s1[i]!=filename[i]) egal --;
		    }
		  if(egal==1)
		    {
		      for(i=10;i<((int) strlen(s1))-1;i++)
			{
			  s2[i-10]=s1[i];
			}
		      s2[strlen(s1)-11]='\0';
		      
		      printf("recherche:\nfilename=%s\n",s2);
		      struct file* file=find_file_name(file_list, s2);
		      if(file==NULL) write_client(client->sock, "list []");
		      else
			{
			  sprintf(send_buffer, "list [%s %d %d %s]", s2, file->length, file->p_size, file->key);
			  write_client(client->sock, send_buffer);
			  printf("replied:%s\n", send_buffer);
			}
		    }
		}
	      break;

	    case'g':
	      recv_buffer[read]='\0'; 
	      printf("%s\n", recv_buffer);	      
	      parse(recv_buffer, tab);
	      if(strcmp(s0,"getfile")==0)
		{
		  struct file* f=find_file(file_list, s1);
		  if(f==NULL||f->peer_list->first==NULL)
		    {
		      sprintf(send_buffer, "peers %s []", s1);
		      write_client(client->sock, send_buffer);
		    }
		  else
		    {
		      struct elt_peer* aux=f->peer_list->first;
		      struct in_addr* in_addr1=malloc(sizeof(struct in_addr));
		      in_addr1->s_addr=aux->peer->ip_address;
		      char* d= inet_ntoa(*in_addr1);
		      sprintf(send_buffer, "peers %s [%s:%d", s1, d, aux->peer->port);
		      aux=aux->next;
		      while(aux!=NULL)
			{
			  struct in_addr* in_addr=malloc(sizeof(struct in_addr));
			  in_addr->s_addr=aux->peer->ip_address;
			  char* c= inet_ntoa(*in_addr);
			  sprintf(send_buffer+strlen(send_buffer), " %s:%d", c, aux->peer->port);
			  aux=aux->next;
			}
		      sprintf(send_buffer+strlen(send_buffer), "]");
		      write_client(client->sock, send_buffer);
		      printf("replied:%s\n", send_buffer);
		    }
		}
	      break;

	    default:
	      printf("entree non valide");
	      end(client,donnees->ct);
	      return 0;
	      
	     
	    }
	  pthread_mutex_unlock( &mutex);
	}
      
    }
  return EXIT_SUCCESS;
}

int get_refresh_time()// en seconde
{
  const char r[2]="r\0";
  FILE * f = fopen("./config.txt",r);
  int p;
  int t;
  fscanf(f,"%d %d",&p, &t);
  return t;
}
 
void end(struct client* client, struct client_tab* ct)
  {
    close(client->sock);
    client_tab_delete_client(ct,client);
    pthread_mutex_unlock( &mutex);
  }

void update_diff(struct file_list* new, struct file_list* old, struct file_list* file_list, struct peer* peer)
{
  struct file_list* f_add=file_list_copy(new);
  struct file_list* f_delete=file_list_copy(old);
  //le diff
  if(f_add->first == NULL) update_delete(file_list, peer, f_delete);
  else 
    {
      struct elt_file* aux_elt_file=f_add->first;;
      while(aux_elt_file!=NULL)
	{
	  if(find_file(f_delete, aux_elt_file->file->key)!=NULL)
	    {
	      file_list_file_delete(NULL, f_delete, aux_elt_file->file->key);
	      file_list_file_delete(NULL, f_add, aux_elt_file->file->key);
	      aux_elt_file=f_add->first;
		}
	  else aux_elt_file=aux_elt_file->next;
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
  char* new_filename = malloc((strlen(filename)+1)*sizeof(char));
  char* new_key = malloc((strlen(key)+1)*sizeof(char));
  strcpy(new_key, key);
  strcpy(new_filename, filename);
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
// remplit les char* du tableau tab à partir de buf 
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
      else if(buf[i]==' '||buf[i]=='\0')
	{
	  tab[k][j]='\0';
	  j=0;
	  k++;
	  //printf("s%d:%s\n", k-1, tab[k-1]);
	}
      else
	{
	  tab[k][j]=buf[i];
	  j++;
	}
       i++;
    }
  tab[k][j]='\0';
  //printf("s%d:%s\n", k, tab[k]);
  while(k<6)
    { 
      k++;
      tab[k][0]='\0';
    }
}


