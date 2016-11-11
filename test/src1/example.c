/*
 * Copyright (c) 2015 - present Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

#include <stdlib.h>
#include <errno.h>
#include <fcntl.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/select.h>
#include <unistd.h>

struct Person {
    int age;
    int height;
    int weight;
};

struct Person *Person_create(int age, int height, int weight) {
    struct Person pp;
    pp.age = 22;
    pp.height = 176;
    pp.weight = 75;
    struct Person *who = 0;
    return who;
}

int null_pointer_interproc() {
    struct Person *joe = Person_create(32, 64, 140);
    return joe->age;
}

void fileNotClosed()
{
    null_pointer_interproc();
    int fd = open("hi.txt", O_WRONLY | O_CREAT | O_TRUNC, 0600);
    if (fd != -1) {
        char buffer[256];
        write(fd, buffer, strlen(buffer));
    }
}

void common_realloc_leak() {
    int *p, *q;
    p = (int*) malloc(sizeof(int));
    q = (int*) realloc(p, sizeof(int) * 42);
    // if realloc fails, then p becomes unreachable
    if (q != NULL) free(q);
    fileNotClosed();
}
