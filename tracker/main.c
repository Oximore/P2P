#include "structure.h"
#include "interface.h"
#include "communicate.h"

int main()
{
  struct peer_list *pl = peer_list_init();
  struct file_list *fl = file_list_init();
  int i;
  char ** t = malloc(5*sizeof(char *));
  char ** t2 = malloc(5*sizeof(char *));
  struct peer **p = malloc(5*sizeof(struct peer *));
  struct file **f = malloc(5*sizeof(struct file *));
  for(i=0;i<5;i++)
    {
      t[i] = malloc(3*sizeof(char));
      t2[i] = malloc(3*sizeof(char));
      sprintf(t[i],"%d",i);
      sprintf(t2[i],"%d",i);
      f[i] = file_init(t[i],NULL,0,0);
      p[i] = peer_init(i,0);
      file_list_add(fl,f[i]);
      peer_list_add(pl,p[i]);
    }
 
  add_link(f[1],p[4]);
  add_link(f[4],p[4]);
  add_link(f[3],p[3]);
  add_link(f[1],p[2]);

  struct file_list * f_add = file_list_init();  
  file_list_add(f_add,file_init(t2[2],NULL,0,0));
  file_list_add(f_add,file_init(t2[3],NULL,0,0));
  file_list_add(f_add,file_init(t2[4],NULL,0,0));
  
  struct file_list * f_del = file_list_init();  
  file_list_add(f_del,file_init(t2[4],NULL,0,0));
  file_list_add(f_del,file_init(t2[1],NULL,0,0));
  
  print_data(fl,pl);
  update_add(fl,p[0],f_add);
  print_data(fl,pl);
  update_delete(fl,p[4],f_del);
  print_data(fl,pl);

  file_list_delete(fl);
  peer_list_delete(pl);
  free(t);
  free(p);
  free(f);
  return 0;
}
