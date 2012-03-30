#ifndef COMMUNICATE_H
#define COMMUNICATE_H
#include "interface.h"
#include "base.h"
#include "list.h"



int communicate(struct donnees* donnees);
void end(struct client* client, struct client_tab* tab);
void remplit_update(char* keys);
void remplit_announce(char* s);
char* fusion_keys_string(char* seed, char* leech);
struct base* keys_string_to_base(char* keys);





#endif
