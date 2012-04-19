#ifndef STRUCTURE_H
#define STRUCTURE_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct peer
{
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

//fonctions sur les elt
struct elt_peer * elt_peer_init(struct peer *);
struct elt_file * elt_file_init(struct file *);
struct elt_peer * elt_peer_delete(struct elt_peer *e,struct file_list *);
struct elt_file * elt_file_delete(struct elt_file *e,struct peer_list * );

//fonctions sur les peer_list
struct peer_list * peer_list_init();
void peer_list_delete(struct peer_list * );
int peer_list_add(struct peer_list *,struct peer *);
void peer_list_peer_delete(struct peer_list *,struct file_list *,unsigned long); //delete le peer désigné par son adresse

//fonctions sur les peer
struct peer * peer_init(unsigned long , int); 
int peer_delete(struct peer *, struct file_list *);//enleve les pointeurs peer de la file_list et free le peer
struct peer * find_peer(struct peer_list * ,unsigned long);//trouve le peer avec son adresse ip
int get_peer(struct peer_list *, struct peer *);//enlève un peer d'une peer_list (ne touche pas à la file_list!)
void delete_peer_pointer(struct peer *, struct file_list *);// enlève le peer de toutes les peer_list contenues dans la file_list

//fonctions sur les file_list
struct file_list * file_list_init();
void file_list_delete(struct file_list *);
void file_list_add(struct file_list *, struct file *);
void file_list_file_delete(struct peer_list *,struct file_list *,char *);//delete le file désigné par son ip
struct file_list * file_list_copy(struct file_list *);//copie une file_list

//fonctions sur les file
struct file * file_init(char *,char *, int, int);
int file_delete(struct file *,struct peer_list *);//enleve les pointeurs file de la peer_list et free le file
struct file * find_file(struct file_list *,char *);//trouve le file avec sa clé
struct file * find_file_name(struct file_list * ,char * );//trouve le file avec son nom
int get_file(struct file_list *, struct file *);//enlève un file d'une file_list (ne touche pas à la peer_list!)
void delete_file_pointer(struct file *, struct peer_list *);// enlève le file de toutes les file_list contenues dans la peer_list

//fonctions de gestion des données
int add_link(struct file *,struct peer *);//Ajoute un lien entre un file et un peer
int update_add(struct file_list *,struct peer *,struct file_list *);//relie le peer aux file contenus dans la file_list
int update_delete(struct file_list *, struct peer *,struct file_list *);//enlève le lien entre le peer et les file contenus dans la file_list
int delete_link(struct file *, struct peer *);//Enlève un lien entre un file et un peer

//fonctions de print
void print_file_list(struct file_list * , int);
void print_peer_list(struct peer_list * , int);
void print_data(struct file_list *,struct peer_list *);

#endif
