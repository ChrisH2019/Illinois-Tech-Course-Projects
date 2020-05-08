#include <stdlib.h>
#include <string.h>
#include "hashtable.h"

/* Daniel J. Bernstein's "times 33" string hash function, from comp.lang.C;
   See https://groups.google.com/forum/#!topic/comp.lang.c/lSKWXiuNOAk */
unsigned long hash(char *str) {
  unsigned long hash = 5381;
  int c;

  while ((c = *str++))
    hash = ((hash << 5) + hash) + c; /* hash * 33 + c */

  return hash;
}

hashtable_t *make_hashtable(unsigned long size) {
  hashtable_t *ht = malloc(sizeof(hashtable_t));
  ht->size = size;
  ht->buckets = calloc(sizeof(bucket_t *), size);
  return ht;
}

void ht_put(hashtable_t *ht, char *key, void *val) {
  /* FIXME: the current implementation doesn't update existing entries */
   unsigned int idx = hash(key) % ht->size;
  
  bucket_t *tmp = ht->buckets[idx];
  while (tmp) {
      if (strcmp(tmp->key, key) == 0) {
          free(tmp->key);
          free(tmp->val);
          tmp->key = key;
          tmp->val = val;
          return;
      }
      tmp = tmp->next;
    }
  
  
  bucket_t *b = malloc(sizeof(bucket_t));
  b->key = key;
  b->val = val;
  b->next = ht->buckets[idx];
  ht->buckets[idx] = b;
}

void *ht_get(hashtable_t *ht, char *key) {
  unsigned int idx = hash(key) % ht->size;
  bucket_t *b = ht->buckets[idx];
  while (b) {
    if (strcmp(b->key, key) == 0) {
      return b->val;
    }
    b = b->next;
  }
  return NULL;
}

void ht_iter(hashtable_t *ht, int (*f)(char *, void *)) {
  bucket_t *b;
  unsigned long i;
  for (i=0; i<ht->size; i++) {
    b = ht->buckets[i];
    while (b) {
      if (!f(b->key, b->val)) {
        return ; // abort iteration
      }
      b = b->next;
    }
  }
}

void free_hashtable(hashtable_t *ht) {
  bucket_t *tmp, *b;
  unsigned long i;
  for (i = 0; i < ht->size; i++) {
      b = ht->buckets[i];
      while (b) {
            tmp = b;
            b = b->next;
            free(tmp->key);
            free(tmp->val);
            free(tmp);
      }
  }
  free(ht->buckets);
  free(ht); // FIXME: must free all substructures!
}

/* TODO */
void  ht_del(hashtable_t *ht, char *key) {
    unsigned int idx = hash(key) % ht->size;
    
    bucket_t *prev;
    bucket_t *cur;
    for (cur = ht->buckets[idx], prev = NULL;
         cur->key != NULL && strcmp(cur->key,key); 
         prev = cur, cur = cur->next)
        ;
    if (cur->key == NULL) {
        free(cur);
        free(prev);
        return;
    }
    
    if (prev == NULL)
        ht->buckets[idx] = cur->next;
    else
        prev->next = cur->next;
        
    free(cur->key);
    free(cur->val);
    free(cur);
    return;
}

void  ht_rehash(hashtable_t *ht, unsigned long newsize) {
    hashtable_t *new_ht= make_hashtable(newsize);
    
    unsigned long i;
    for (i = 0; i <ht->size; i++) {
        bucket_t *b = ht->buckets[i];
        while (b) {
            char *k = strdup(b->key);
            void *v = strdup(b->val);
            ht_put(new_ht, k, v);
            
            bucket_t *tmp = b;
            b = b->next;
            free(tmp->key);
            free(tmp->val);
            free(tmp);
        }
        
    }
    
    free(ht->buckets);
    ht->buckets = new_ht->buckets;
    ht->size = newsize;
    free(new_ht);
}
