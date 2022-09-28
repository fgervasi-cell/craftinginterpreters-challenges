#include <stdio.h>
#include <string.h>
#include "doublyLinkedList.h"

int main()
{
    List *list = init();
    for (;;)
    {
        printf("What do you want to do?\n");
        char input[10];
        int argument;
        scanf("%s", input);
        if (strcmp(input, "exit") == 0)
        {
            delete(list);
            printf("Goodbye...");
            return 0;
        }
        else if (strcmp(input, "insert") == 0)
        {
            printf("Provide a value...\n");
            scanf("%i", &argument);
            insert(argument, list);
            print(list);
        }
        else if (strcmp(input, "delete") == 0)
        {
            printf("Provide an index...\n");
            scanf("%i", &argument);
            delete_at_index(argument, list);
            print(list);
        }
        else if (strcmp(input, "contains") == 0)
        {
            printf("Provide a value...\n");
            scanf("%i", &argument);
            if (contains(argument, list) == 0)
            {
                printf("The list contains the value %i\n", argument);
            }
            else
            {
                printf("The list does not contain the value %i\n", argument);
            }
        }
        else if (strcmp(input, "clear") == 0)
        {
            printf("List was cleared\n");
            clear(list);
            print(list);
        }
    }
}