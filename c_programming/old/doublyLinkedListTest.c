#include <assert.h>
#include <stdio.h>
#include "doublyLinkedList.h"

int main()
{
    test_init_list();
    test_insert_element_after();
}

void test_init_list()
{
    assert(init_list != 0);
}

void test_insert_element_after()
{
    node *new_node = insert_element_after(tail, 5);
    assert(new_node != NULL);
}