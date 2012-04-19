#ifndef COMMUNICATE_H
#define COMMUNICATE_H
#include "interface.h"
#include "base.h"
#include "list.h"

void update_diff(struct file_list* new, struct file_list* old,struct file_list* file_list, struct peer* peer);

int communicate(struct donnees* donnees);
void end(struct client* client, struct client_tab* tab);
char* fusion_keys_string(char* seed, char* leech);
struct file_list* keys_string_to_file_list(char* keys);
struct file* remplit_file(char* filename, int length, int piece_size, char* key);
int compte_crochet_fermant(char* buf);
int compte_espace(char* buf);
void parse(char* buf, char** tab);

#endif
