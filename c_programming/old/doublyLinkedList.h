#ifndef DOUBLYLINKEDLIST_H
#define DOUBLYLINKEDLIST_H

typedef struct
{
    int value;
    struct node *previous;
    struct node *next;
} node; 
extern int init_list();
extern node *insert_element_after(node *, int);
extern int delete_element_after(node *);
extern node *search_content(int);
extern void delete_list();
extern node *tail, *head;

#endif