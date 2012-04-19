#include "structure.h"

struct peer_list * peer_list_init()
{
  struct peer_list * l = malloc(sizeof(struct peer_list));
  l->first = NULL;
  return l;
}

struct elt_peer * elt_peer_init(struct peer *p)
{
  struct elt_peer * e = malloc(sizeof(struct elt_peer));
  e->next =NULL;
  e->peer = p;
  return e;
}

struct elt_file * elt_file_init(struct file *f)
{
  struct elt_file * e = malloc(sizeof(struct elt_file));
  e->next =NULL;
  e->file = f;
  return e;
}

struct elt_peer * elt_peer_delete(struct elt_peer *e,struct file_list *fl)
{
  struct elt_peer * e2 = e->next;
  peer_delete(e->peer,fl);
  free(e);
  return e2;
}


struct elt_file * elt_file_delete(struct elt_file *e,struct peer_list * pl)
{
  struct elt_file * e2 = e->next;
  file_delete(e->file,pl);
  free(e);
  return e2;
}
// on fait peer_delete(l->first,NULL) car on ne s'occupe pas de l'autre liste
void peer_list_delete(struct peer_list * l)
{
  struct elt_peer * p;
  if(l->first==NULL)
    {
      free(l);
      return ;
    }
  while(l->first->next!=NULL)
    {
      p = l->first->next;
      elt_peer_delete(l->first,NULL);
      l->first = p;
    }
  elt_peer_delete(l->first,NULL);
  free(l);
}


int peer_list_add(struct peer_list * l,struct peer * p)
{
  struct elt_peer *e = elt_peer_init(p);
  e->next = l->first;
  l->first = e;
  return 0;
}

void peer_list_peer_delete(struct peer_list * l,struct file_list * f,unsigned long ip_address)
{
  if(l->first == NULL)
    return;
  if(l->first->next == NULL)
    {
      if(l->first->peer->ip_address!=ip_address) 
	return;
      elt_peer_delete(l->first,f);
      l->first =NULL;
      return; 
    }
  
  struct elt_peer * p = l->first;
  if(p->peer->ip_address==ip_address)
    {
      l->first = p->next;
      elt_peer_delete(p,f);
      return;
    }
  while(p->next!=NULL )
    {
      if(p->next->peer->ip_address==ip_address)
	{
	  struct elt_peer * p2 = elt_peer_delete(p->next,f);
	  p->next=p2;
	  return;
	}
	p = p->next;
    }
}

struct peer * peer_init(unsigned long ip_address, int port)
{
  struct peer * p=malloc(sizeof(struct peer));
  //p->next=NULL;
  p->ip_address = ip_address;
  p->port=port;
  p->time = 0;
  p->file_list = file_list_init();
  return p;
}

struct peer * peer_delete(struct peer * p, struct file_list * f)
{
  //struct peer * p2 = p->next;
  if(f!=NULL)
    delete_peer_pointer(p,f);
  if(p->file_list != NULL)
    free(p->file_list);//il faut free les elt
  free(p);
  return NULL;//changer les val de ret
}

struct peer * find_peer(struct peer_list * l,unsigned long ip)
{
  if(l==NULL)
    return NULL;
  if(l->first==NULL)
    return NULL;
  struct elt_peer * p= l->first;
  while(p!=NULL)
    {
      if(p->peer->ip_address==ip)
	return p->peer;
      p=p->next;
    }
  return NULL;
}


//file_list

struct file * file_init(char * key,char * name, int length, int p_size)
{
  struct file * e = malloc(sizeof(struct file));
  //e->next = NULL;
  e->key=key;
  e->name=name;
  e->length=length;
  e->p_size=p_size;
  e->peer_list= peer_list_init();
  return e;
}

struct file * file_delete(struct file * e,struct peer_list * p)
{
  //struct file * e2 = e->next;
  if(p!=NULL)
    delete_file_pointer(e,p);//a modifier
  free(e->key);
  free(e->name);
  if(e->peer_list!=NULL)
    free(e->peer_list);// a free proprement
  return NULL;
}

struct file_list * file_list_init()
{
  struct file_list * b=malloc(sizeof(struct file_list));
  b->first = NULL;
  return b;
}

void file_list_delete(struct file_list * b)
{
  struct elt_file * tmp = b->first;
  while(b->first != NULL)
    {
      b->first = b->first->next;
      elt_file_delete(tmp,NULL);
      tmp = b->first;
    }
  free(b);
}

void file_list_add(struct file_list * b, struct file * f)
{
  struct elt_file *e = elt_file_init(f);
  e->next = b->first;
  b->first = e;
}


void file_list_file_delete(struct peer_list * pl,struct file_list * b,char * key)
{
  if(b->first == NULL)
    return;
  if(b->first->next == NULL)
    {
      if(strcmp(b->first->file->key,key)) 
	return;
      elt_file_delete(b->first,pl);
      b->first =NULL;
      return; 
    }
  
  struct elt_file * p = b->first;
  if(!strcmp(p->file->key,key))
    {
      b->first = p->next;
      elt_file_delete(p,pl);
      return;
    }
  while(p->next!=NULL )
    {
      if(!strcmp(p->next->file->key,key))
	{
	  struct elt_file * p2 = elt_file_delete(p->next,pl);
	  p->next=p2;
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
  struct elt_file * p= b->first;
  while(p!=NULL)
    {
      if(!strcmp(p->file->key,key))
	return p->file;
      p=p->next;
    }
  return NULL;
}



// Renvoie 0 si on trouve le peer dans une liste de peer
// 1 sinon
// et enleve le peer de la liste
int get_peer(struct peer_list * l, struct peer * f)
{
  if(l->first == NULL)
    return -1;
  if(l->first->next == NULL)
    {
      if(l->first->peer->ip_address != f->ip_address) 
	return -1;
      free(l->first);
      l->first = NULL;
      return 0; 
    }
  
  struct elt_peer * p = l->first;
  if(p->peer->ip_address == f->ip_address)
    {
      l->first = p->next;
      free(p);
      return 0;
    }
  while(p->next!=NULL )
    {
      if(p->next->peer->ip_address == f->ip_address)
	{
	  struct elt_peer * tmp = p->next;
	  p->next = p->next->next;
	  free(tmp);
	  return 0;
	}
	p = p->next;
    }
  return -1;
}

// Renvoie 0 si on trouve le fichier dans une liste de fichier
// 1 sinon
// et enleve le fichier de la liste
int get_file(struct file_list * l, struct file * f)
{
  if(l->first == NULL)
    return 1;
  if(l->first->next == NULL)
    {
      if(strcmp(l->first->file->key,f->key)) 
	return 1;
      free(l->first);
      l->first = NULL;
      return 0; 
    }
  
  struct elt_file * p = l->first;
  if(!strcmp(p->file->key,f->key))
    {
      l->first = p->next;
      free(p);
      return 0;
    }
  while(p->next!=NULL )
    {
      if(!strcmp(p->next->file->key,f->key))
	{
	  struct elt_file * tmp = p->next;
	  p->next = p->next->next;
	  free(tmp);
	  return 0;
	}
	p = p->next;
    }
  return -1;
}


// enleve p de ttes les peer_list de f : utilise get_peer
void delete_peer_pointer(struct peer * p, struct file_list * f)
{
  if(NULL == f || NULL ==p)
    return; 

  //iteration sur tous les fichiers
  struct elt_file * tmp = f->first;
  while(NULL != tmp)
    {
      get_peer(tmp->file->peer_list, p);
      //si peer_list->first devient null
      //plus de seeders, mais on laisse le fichier dans la base
      tmp = tmp->next;
    }
}

void delete_file_pointer(struct file * f, struct peer_list * p)
{
  if(NULL == p || NULL == f)
    return;

  //iteration sur tous les peers
  struct elt_peer * tmp = p->first;
  while(NULL != tmp)
    {
      get_file(tmp->peer->file_list, f);
      tmp = tmp->next;
    }
}

int add_link(struct file *f, struct peer *p)
{
  if(NULL == f || NULL == p)
    return -1;
  if(p->file_list == NULL)
    p->file_list = file_list_init();
  if(f->peer_list == NULL)
    f->peer_list = peer_list_init();
  file_list_add(p->file_list,f);
  peer_list_add(f->peer_list,p);
  return 0;
}


int delete_link(struct file *f, struct peer *p)
{
  if(NULL == f || NULL == p)
    return -1;
  if(p->file_list == NULL)
    p->file_list = file_list_init();
  if(f->peer_list == NULL)
    f->peer_list = peer_list_init();
  get_file(p->file_list,f);
  get_peer(f->peer_list,p);
  return 0;
}


//update p avec f_add
// f_add "une copie" des file!! chercher le vrai pointeur
int update_add(struct file_list *fl,struct peer *p,struct file_list *f_add)
{
  struct elt_file * tmp = f_add->first;
  struct file * tmp2;
  if(tmp!=NULL)
    tmp2 = find_file(fl,tmp->file->key);
  //printf("coucou %s\n",tmp2->key);
  while(tmp!=NULL)
    {
      if(tmp2!=NULL)
	add_link(tmp2,p);
      tmp = tmp->next;
      if(tmp!=NULL)
	tmp2 = find_file(fl,tmp->file->key);
    }
  return 0;
}

int update_delete(struct file_list *fl, struct peer *p,struct file_list * f_delete)
{
  struct elt_file * tmp = f_delete->first;
  struct file * tmp2;
  if(tmp!=NULL)
    tmp2 = find_file(fl,tmp->file->key);
  while(tmp!=NULL)
    {
      if(tmp2!=NULL)
	delete_link(tmp2,p);
      tmp = tmp->next;
      if(tmp!=NULL)
	tmp2 = find_file(fl,tmp->file->key);
    }
  return 0; 
}


void print_data(struct file_list *fl,struct peer_list * pl)
{
  print_file_list(fl,1);
  print_peer_list(pl,1);
  printf("........\n");
}

void print_file_list(struct file_list * fl, int b)
{
  struct elt_file * tmp = fl->first;
  while(tmp!=NULL)
    {
      printf("file %s\n",tmp->file->key);
      if(b)
	print_peer_list(tmp->file->peer_list,0);
      tmp=tmp->next;
    }
  printf("\n");
}


void print_peer_list(struct peer_list * pl, int b)
{
  struct elt_peer * tmp = pl->first;
  while(tmp!=NULL)
    {
      printf("peer %lu\n",tmp->peer->ip_address);
      if(b)
	print_file_list(tmp->peer->file_list,0);
      tmp=tmp->next;
    }
  printf("\n");
}

struct file_list * file_list_copy(struct file_list * fl)
{
  struct file_list * fl2 = file_list_init();
  struct elt_file * tmp = fl->first;
  struct file * tmp2;
  while(tmp != NULL)
    {
      char * s = malloc((strlen(tmp->file->key)+1)*sizeof(char));
      strcpy(s,tmp->file->key);
      tmp2 = file_init(s,NULL,0,0);
      file_list_add(fl2,tmp2);
      tmp = tmp->next;
    }
  return fl2;
}
