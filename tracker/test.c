#include <stdio.h>
#include <stdlib.h>
#include "structure.h"
#include "interface.h"



int main()
{
  /*
  struct list * l = list_init();
  list_add(l,12,1024);
  list_add(l,13,1024);
  list_add(l,14,1024);
  struct peer * p = l->first;
  while(p!=NULL)
    {
      printf("%lu %d\n",p->ip_address,p->port);
      p=p->next;
    }
  list_peer_delete(l,14);
  p=l->first;
  while(p!=NULL)
    {
      printf("%lu %d\n",p->ip_address,p->port);
      p=p->next;
    }
  list_delete(l);
  */
  struct donnees* donnees=donnees_init();
  server(donnees);  
//server(donnees_init());
  return EXIT_SUCCESS;
}
 
