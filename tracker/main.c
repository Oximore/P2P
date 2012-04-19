#include "structure.h"
#include "interface.h"
#include "communicate.h"

int main()
{
  struct peer_list *pl = peer_list_init();
  struct file_list *fl = file_list_init();
  int i;
  char ** t = malloc(5*sizeof(char *));
  struct peer **p = malloc(5*sizeof(struct peer *));
  struct file **f = malloc(5*sizeof(struct file *));
  for(i=0;i<5;i++)
    {
      t[i] = malloc(3*sizeof(char));
      sprintf(t[i],"%d",i);
      f[i] = file_init(t[i],NULL,0,0);
      p[i] = peer_init(i,0);
      file_list_add(fl,f[i]);
      peer_list_add(pl,p[i]);
    }
  /*  char * s = malloc(5*sizeof(char));
  char * s2 = malloc(5*sizeof(char));
  char * s3 = malloc(5*sizeof(char));
  strcpy(s,"caca");
  strcpy(s2,"pipi");
  strcpy(s3,"pipi");
  struct peer * p = peer_init(192,1024);
  struct peer * p2 = peer_init(194,1024);
  struct file * f = file_init(s,NULL,12,14);
  struct file * f2 = file_init(s2,NULL,12,14);
  peer_list_add(pl,p);
  peer_list_add(pl,p2);
  file_list_add(fl,f);
  file_list_add(fl,f2);*/
  add_link(f[1],p[4]);
  add_link(f[4],p[4]);
  add_link(f[3],p[3]);
  add_link(f[1],p[2]);

  print_data(fl,pl);

  /* struct file_list *f_add=file_list_init();
  file_list_add(f_add,file_init(s3,NULL,12,14));
 
  update_add(fl,p2,f_add);
  print_data(fl,pl);
  update_delete(fl,p2,f_add);
  print_data(fl,pl);
  file_list_file_delete(pl,fl,f->key);
  peer_list_peer_delete(pl,fl,p->ip_address);
  print_data(fl,pl);
  file_list_delete(f_add);*/
  file_list_delete(fl);
  peer_list_delete(pl);
  free(t);
  free(p);
  free(f);
  return 0;
}
