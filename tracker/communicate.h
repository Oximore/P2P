#ifndef COMMUNICATE_H
#define COMMUNICATE_H
#include "interface.h"
#include "base.h"
#include "list.h"



int communicate(struct donnees* donnees);
void end(struct client* client, struct client_tab* tab);
void remplit_update(struct list* peers, struct base *base,char* keys, int ip);
void remplit_announce(struct base* base,char* s);
 




void remplit_keys(char* s);
char* fusion_keys_string(char* seed, char* leech);
struct base* keys_string_to_base(char* keys);
void remplit_file(char* filename, int length, int piece_size, char* key);
int compte_crochet_fermant(char* buf);




#endif
