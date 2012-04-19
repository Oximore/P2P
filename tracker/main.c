#include "structure.h"
#include "interface.h"
#include "communicate.h"

int main()
{
  struct peer_list *pl = peer_list_init();
  struct file_list *fl = file_list_init();
  char * s = malloc(5*sizeof(char));
  char * s2 = malloc(5*sizeof(char));
  strcpy(s,"caca");
  strcpy(s,"pipi");
  struct peer * p = peer_init(192,1024);
  struct peer * p2 = peer_init(194,1024);
  struct file * f = file_init(s,s2,12,14);
  peer_list_add(pl,p);
  peer_list_add(pl,p2);
  file_list_add(fl,f);
  add_link(f,p);

  struct file_list *f_add=file_list_init();
  file_list_add(f_add,f);
  
  update_add(fl,p2,f_add);
  print_file_list(fl,1);
  print_peer_list(pl,1);
  file_list_file_delete(pl,fl,f->key);
  //print_file_list(fl,1);
  //print_peer_list(pl,1);
  
  peer_list_peer_delete(pl,fl,p->ip_address);
  return 0;
}
