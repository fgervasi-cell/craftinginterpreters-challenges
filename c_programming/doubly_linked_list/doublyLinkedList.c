#include <stdlib.h>
#include <stdio.h>
#include <stddef.h>
#include "doublyLinkedList.h"

List *init()
{
    List *list = (List *)malloc(sizeof list);
    list->tail = (Node *)malloc(sizeof list->tail);
    list->head = (Node *)malloc(sizeof list->head);
    list->len = 0;
    list->tail->next = list->head;
    list->head->prev = list->tail;
    return list;
}

void insert(int value, List *list)
{
    Node *node = (Node *)malloc(sizeof node);
    node->value = value;
    node->next = list->head;
    node->prev = list->head->prev;
    list->head->prev->next = node;
    list->head->prev = node;
    list->len++;
}

int contains(int value, List *list)
{
    Node *temp = list->tail;
    while(temp->next != NULL)
    {
        temp = temp->next;
        if (temp->value == value)
            return 0;
    }
    return 1;
}

void delete(List *list)
{
    Node *temp = list->tail;
    Node *prev;
    while (temp->next != NULL)
    {
        prev = temp;
        temp = temp->next;
        free(prev);
    }
    free(temp);
    free(list);
}

void print(List *list)
{
    Node *temp = list->tail;
    printf("[");
    while (temp->next != NULL)
    {
        temp = temp->next;
        if (temp->next != NULL)
            printf("%i,", temp->value);
    }
    printf("]\n");
}

void delete_at_index(int index, List *list)
{
    if (index >= list->len || index < 0)
        return;
    Node *delete = list->tail->next;
    for (int i = 0; i < index; i++)
    {
        delete = delete->next;
    }
    delete->prev->next = delete->next;
    delete->next->prev = delete->prev;
    free(delete);
    list->len--;
}

void clear(List *list)
{
    Node *temp = list->tail->next;
    Node* prev;
    while (temp->next != NULL)
    {
        prev = temp;
        temp = temp->next;
        free(prev);
    }
    list->tail->next = list->head;
    list->head->prev = list->tail;
    list->len = 0;
}