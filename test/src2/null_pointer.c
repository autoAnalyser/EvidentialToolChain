/********Software Analysis - FY2013*************/
/*
* File Name: null_pointer.c
* Defect Classification
* ---------------------
* Defect Type: Pointer related defects
* Defect Sub-type: Dereferencing a NULL pointer
*/

#include "HeaderFile.h"
#include <assert.h>
/*
* Types of defects: NULL pointer dereference (access)
 *  Memory allocated in a function and Memory used in another function
 */
# define INDEX 'a'
static unsigned char a =INDEX;

char * null_pointer_015_gbl_ptr;
void null_pointer_015_func_001 (int len)
{
    if(a == INDEX) null_pointer_015_gbl_ptr= malloc(sizeof(char) * (len+1));
}

void null_pointer_015 ()
{
	char *str = "This is a string";
	null_pointer_015_func_001(strlen(str));
	strcpy(null_pointer_015_gbl_ptr,str); /* Tool should NOT detect error here */
	free(null_pointer_015_gbl_ptr);
	null_pointer_015_gbl_ptr = NULL;
}

char * null_pointer_016_gbl_ptr;
void null_pointer_016_func_001 (int len)
{
    null_pointer_016_gbl_ptr=NULL;
}

void null_pointer_016 ()
{
    char *str = "This is a string";
    null_pointer_016_func_001(strlen(str));
    strcpy(null_pointer_016_gbl_ptr,str); /* Tool should detect error here */
    free(null_pointer_016_gbl_ptr);
    null_pointer_016_gbl_ptr = NULL;
}