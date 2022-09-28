#ifndef DOUBLYLINKEDLIST_H
#define DOUBLYLINKEDLIST_H

typedef struct node
{
    int value;
    struct node *prev;
    struct node *next;
} Node;

typedef struct list
{
    int len;
    struct node *head;
    struct node *tail;
} List;

List *init();
void insert(int, List *);
int contains(int, List *);
void print(List *);
void delete(List *);
void delete_at_index(int, List *);
void clear(List *);

#endif