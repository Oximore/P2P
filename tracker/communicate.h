#ifndef COMMUNICATE_H
#define COMMUNICATE_H
#include "interface.h"
#include "base.h"
#include "list.h"



int communicate(struct client * client, struct client_tab* tab, struct list* peers, struct base* base);
void end(struct client* client, struct client_tab* tab);
void remplit_update(struct list* peers, struct base *base,char* keys, int ip);
void remplit_announce(struct base* base,char* s);
 






#endif
