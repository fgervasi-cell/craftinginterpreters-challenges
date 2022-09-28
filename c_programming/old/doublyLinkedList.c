// For using the NULL constant (one could also just use 0)
#include <stddef.h>
#include <stdlib.h>

/*
A Doubly Linked List consist of nodes.
Each node stores three values.
The value itself which should be stored by the list.
Two pointers - one to the previous and one to the following node.
*/
typedef struct node
{
    int value;
    node *previous;
    node *next;
} node; 

// Pointers to the beginning and end of the list
node *tail, *head;

// Initializes the list with default values
int init_list()
{
    // Reserve memory dynamically during runtime
    tail = (node *)malloc(sizeof *tail);
    head = (node *)malloc(sizeof *head);

    // Error case - memory could not be allocated
    if (tail == NULL || head == NULL)
        return 1;

    // Adjust the pointers
    tail->next = head;     // The head comes after the tail
    tail->previous = NULL; // There is no element previous to the tail
    head->next = NULL;     // There is no element after the head
    head->previous = tail; // The element before the head is the tail

    // Initialization was successful
    return 0;
}

/*
Inserts 'new_node' after 'after_node' with the value 'new_value'.
Returns the pointer to the newly added node.
*/
node *insert_element_after(node *after_node, int new_value)
{
    // Create the new node that should be added
    node *new_node;

    new_node = (node *)malloc(sizeof *new_node);

    // Error - memory could not be allocated
    if (new_node == NULL)
        return NULL;

    // Assign the desired value to the new node
    new_node->value = new_value;

    // 'Add' the new node to the list by adjusting the pointers
    new_node->previous = after_node;
    new_node->next = after_node->next;
    after_node->next = new_node;

    // New node was added and is being returned
    return new_node;
}

// Deletes the element that comes after 'after_node'
int delete_element_after(node *after_node)
{
    // Error - there is no next element to 'after_node'
    if (after_node->next == NULL)
        return 1;

    /*
    If there is a next element adjust the pointer of 'after_node'.
    It is like jumping over the node we want to delete.
    */
    after_node->next = after_node->next->next;

    // Afterwards the memory for this node can be freed
    free(after_node->next);

    return 0;
}

/* 
This implementation is for a linked list and not for a doubly linked list!
It does not make use of elements pointer to its previous element.
*/
node *find_previous_element(node *element)
{
    // Temporary pointer to the tail (frist element) of the list for iterating over all elements
    node *temp = tail;
    // Pointer to remember the last visited element
    node *prev;

    /* 
    Iterate over the list until 'element' is found and its previous element can be returned
    ('temp == element') or until the end of the list is reached ('temp == NULL').
    */
    while (temp != NULL && temp != element)
    {
        prev = temp;
        temp = temp->next;
    }

    // The element does not exist in the list
    if (temp == NULL && temp != element)
        return NULL;

    // The element is the first element in the list so there is no previous element
    if (temp == element)
        return NULL;

    return prev;
}

// This implementation is also not for a doubly linked list
int delete_element(node *delete)
{
    // Find the element before the one we want to delete
    node *prev = find_previous_element(delete);

    // The element to delete is not contained in the list
    if (prev == NULL && delete != tail)
        return 1;

    if (delete == tail)
        tail = delete->next;
    else
        // "Jump over" the element we want to delete
        prev->next = prev->next->next;

    free(delete);

    return 0;
}

// Search a node by value
node *search_content(int search_value)
{
    node *temp = tail;

    while (temp != head)
    {
        if (temp->value == search_value)
            return temp;
        temp = temp->next;
    }

    return NULL;
}

void delete_list()
{
    node *temp = tail;

    while (temp->next != NULL)
    {
        temp = temp->next;
        free(temp->previous);
    }

    free(temp->next);
}