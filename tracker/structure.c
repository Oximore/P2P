#include "structure.h"

struct peer_list * peer_list_init()
{
  struct peer_list * l = malloc(sizeof(struct peer_list));
  l->first = NULL;
  l->size = 0;
  return l;
}

void peer_list_delete(struct peer_list * l)
{
  struct peer * p;
  if(l->first==NULL)
    {
      free(l);
      return ;
    }
  while(l->first->next!=NULL)
    {
      p = l->first->next;
      peer_delete(l->first);
      l->first = p;
    }
  peer_delete(l->first);
  free(l);
}

/*
struct peer{
};

struct fichier{};

struct peers{

};

struct peer * get_peer_from_id(int);

struct peer_files{
  int id_peer;
  int * id_files;
  int nb_files;
  int size_files;
};


struct file_peers{
  int id_file;
  int 
}

*/
int peer_list_add(struct peer_list * l,struct file_list *f,struct peer * p)
{
  //  struct peer * p = peer_init(ip_address,port);
  p->next = l->first;
  l->first = p;
  return p;
}

void peer_list_peer_delete(struct peer_list * l,unsigned long ip_address)
{
  if(l->first == NULL)
    return;
  if(l->first->next == NULL)
    {
      if(l->first->ip_address!=ip_address) 
	return;
      peer_delete(l->first);
      l->size=0;
      return; 
    }
  
  struct peer * p = l->first;
  if(p->ip_address==ip_address)
    {
      l->first = p->next;
      peer_delete(p);
      l->size--;
      return;
    }
  while(p->next!=NULL )
    {
      if(p->next->ip_address==ip_address)
	{
	  struct peer * p2 = peer_delete(p->next);
	  p->next=p2;
	  l->size--;
	  return;
	}
	p = p->next;
    }
}

struct peer * peer_init(unsigned long ip_address, int port, struct file_list * f)
{
  struct peer * p=malloc(sizeof(struct peer));
  p->next=NULL;
  p->ip_address = ip_address;
  p->port=port;
  p->time = 0;
  p->file_list = f;
  return p;
}

struct peer * peer_delete(struct peer * p, struct file_list * f,struct peer_list * pl)
{
  struct peer * p2 = p->next;
  delete_peer_pointer(p,f);
  if(p->file_list != NULL)
    free(p->file_list);
  free(p);
  return p2;
}

struct peer * find_peer(struct peer_list * l,unsigned long ip)
{
  if(l==NULL)
    return NULL;
  if(l->first==NULL)
    return NULL;
  struct peer * p= l->first;
  while(p!=NULL)
    {
      if(p->ip_address==ip)
	return p;
      p=p->next;
    }
  return NULL;
}


//file_list

struct file * file_init(char * key,char * name, int length, int p_size, struct peer_list * l)
{
  struct file * e = malloc(sizeof(struct file));
  e->next = NULL;
  e->key=key;
  e->name=name;
  e->length=length;
  e->p_size=p_size;
  e->peer_list=l;
  return e;
}

struct file * file_delete(struct file * e,struct peer_list * p)
{
  struct file * e2 = e->next;
  delete_file_pointer(e,p);
  free(e->key);
  free(e->name);
  // list_delete(e->peer_list);
  if(e->peer_list!=NULL)
    free(e->peer_list);
  return e2;
}

struct file_list * file_list_init()
{
  struct file_list * b=malloc(sizeof(struct file_list));
  b->first = NULL;
  b->size = 0;
  return b;
}

void file_list_delete(struct file_list * b)
{
  struct file * tmp = b->first;
  while(b->first != NULL)
    {
      b->first = b->first->next;
      file_delete(tmp);
      tmp = b->first;
    }
  free(b);
}

void file_list_add(struct file_list * b, struct file * e)
{
  e->next = b->first;
  b->first = e;
}


void file_list_file_delete(struct file_list * b,struct file * e)
{
  if(b->first == NULL)
    return;
  if(b->first->next == NULL)
    {
      if(strcmp(b->first->key,e->key)) 
	return;
      element_delete(b->first);
      b->size=0;
      return; 
    }
  
  struct file * p = b->first;
  if(!strcmp(p->key,e->key))
    {
      b->first = p->next;
      file_delete(p);
      b->size--;
      return;
    }
  while(p->next!=NULL )
    {
      if(!strcmp(p->next->key,e->key))
	{
	  struct file * p2 = file_delete(p->next);
	  p->next=p2;
	  b->size--;
	  return;
	}
	p = p->next;
    }
}



struct file * find_file(struct file_list * b,char * key)
{
  if(b==NULL)
    return NULL;
  if(b->first==NULL)
    return NULL;
  struct file * p= b->first;
  while(p!=NULL)
    {
      if(!strcmp(p->key,key))
	return p;
      p=p->next;
    }
  return NULL;
}

/*
void add_peer_to_file(struct file * f, struct peer *p)
{
  peer_list_add(f->peer_list,p); 
  }*/
struct peer * get_peer(struct peer_list * l, struct peer * f)
{
  if(l->first == NULL)
    return;
  if(l->first->next == NULL)
    {
      if(strcmp(l->first->key,f->key)) 
	return;
      l->first = NULL;
      b->size=0;
      return; 
    }
  
  struct peer * p = l->first;
  if(!strcmp(p->key,f->key))
    {
      l->first = p->next;
      b->size--;
      return;
    }
  while(p->next!=NULL )
    {
      if(!strcmp(p->next->key,f->key))
	{
	  p = p->next->next;
	  b->size--;
	  return;
	}
	p = p->next;
    }
}

struct file * get_file(struct file_list * l, struct file * f)
{
  if(l->first == NULL)
    return;
  if(l->first->next == NULL)
    {
      if(strcmp(l->first->key,f->key)) 
	return;
      l->first = NULL;
      b->size=0;
      return; 
    }
  
  struct file * p = l->first;
  if(!strcmp(p->key,f->key))
    {
      l->first = p->next;
      b->size--;
      return;
    }
  while(p->next!=NULL )
    {
      if(!strcmp(p->next->key,f->key))
	{
	  p = p->next->next;
	  b->size--;
	  return;
	}
	p = p->next;
    }
}


// enleve p de ttes les peer_list de f : utilise get_peer
void delete_peer_pointer(struct peer * p, struct file_list * f)
{
}

void delete_file_pointer(struct file * f, struct peer_list * p);
