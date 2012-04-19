#ifndef STRUCTURE_H
#define STRUCTURE_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct peer
{
  //struct peer * next;
  unsigned long ip_address;
  int port;
  int time;
  struct file_list * file_list;
};

struct elt_peer
{
  struct elt_peer * next;
  struct peer * peer;
};

struct peer_list
{
  struct elt_peer * first;
};

struct elt_file
{
  struct elt_file * next;
  struct file * file;
};

struct file
{
  //struct file * next;
  char * key;
  char * name;
  int length;
  int p_size;
  struct peer_list * peer_list; 
};

struct file_list
{
  struct elt_file * first;
};

struct elt_peer * elt_peer_init(struct peer *);
struct elt_file * elt_file_init(struct file *);
struct elt_peer * elt_peer_delete(struct elt_peer *e,struct file_list *);
struct elt_file * elt_file_delete(struct elt_file *e,struct peer_list * );
struct peer_list * peer_list_init();
void peer_list_delete(struct peer_list * ); //delete la peer_list
int peer_list_add(struct peer_list *,struct peer *); //add un peer à la peer_list
void peer_list_peer_delete(struct peer_list *,struct file_list *,unsigned long); //delete le peer désigné par son adresse
struct peer * peer_init(unsigned long , int); 
struct peer * peer_delete(struct peer *, struct file_list *);//enleve les pointers peer de la file_list et free le peer
struct peer * find_peer(struct peer_list * ,unsigned long);//trouve le peer avec son adresse ip
struct file * file_init(char *,char *, int, int);//init un file
struct file * file_delete(struct file *,struct peer_list *);//enleve les pointers file de la peer_list et free le file
struct file_list * file_list_init();//init une file_list
void file_list_delete(struct file_list *);//delete une file_list
void file_list_add(struct file_list *, struct file *);//add un file à la file_list
void file_list_file_delete(struct peer_list *,struct file_list *,char *);//delete le file désigné par son ip
struct file * find_file(struct file_list *,char *);//trouve le file avec sa clé
int get_peer(struct peer_list *, struct peer *);//enleve un peer d'une peer_list (ne touche pas à la file_list!)
int get_file(struct file_list *, struct file *);//enleve un file d'une file_list (ne touche pas à la peer_list!)
void delete_peer_pointer(struct peer *, struct file_list *);// enleve p de ttes les peer_list de f : utilise get_peer
void delete_file_pointer(struct file *, struct peer_list *);// enleve f de ttes les file_list de p : utilise get_file
int add_link(struct file *,struct peer *);//Ajoute le fichier f dans la liste de peer qui correspont a ceux equivalent a la liste de peer pointee par le fichier

int update_add(struct file_list *,struct peer *,struct file_list *);
int update_delete(struct file_list *, struct peer *,struct file_list *);
int delete_link(struct file *, struct peer *);
struct file_list * file_list_copy(struct file_list *);
void print_file_list(struct file_list * , int);
void print_peer_list(struct peer_list * , int);
void print_data(struct file_list *,struct peer_list *);
#endif
