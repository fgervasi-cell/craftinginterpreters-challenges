#include <assert.h>
#include <stddef.h>
#include <stdlib.h>
#include <stdio.h>
#include "doublyLinkedList.h"

List *list;

void test_init()
{
    printf("Testing initialization...\n");
    list = init();
    assert(list != NULL);
    printf("List was not null\n");
    assert(list->len == 0);
    printf("List length was 0\n");
}

void test_insert()
{
    printf("Testing insert operation...\n");
    insert(42, list);
    insert(43, list);
    insert(4711, list);
    assert(list->len == 3);
    printf("List length was 3\n");
    print(list);
}

void test_contains()
{
    printf("Testing contains function...\n");
    assert(contains(42, list) == 0);
    assert(contains(43, list) == 0);
    assert(contains(4711, list) == 0);
    assert(contains(44, list) == 1);
}

void test_delete_at_index()
{
    printf("Testing deleting at index...\n");
    delete_at_index(0, list);
    assert(contains(42, list) == 1);
    assert(list->len == 2);
    print(list);
}

void test_clear()
{
    printf("Testing clear function...\n");
    clear(list);
    assert(list->len == 0);
    assert(list->head->prev == list->tail);
    assert(list->tail->next == list->head);
    print(list);
}

int main()
{
    printf("Begin test...\n");
    test_init();
    test_insert();
    test_contains();
    test_delete_at_index();
    test_clear();
    delete(list);
    printf("The test has finished.");
    return 0;
}