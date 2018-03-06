//
// Created by Lin on 2018/1/20.
//
#include <sys/types.h>
#ifndef ANDROIDSOENHANCE2_RC4UTIL_H
#define ANDROIDSOENHANCE2_RC4UTIL_H
struct rc4_state {
    u_char  perm[256];
    u_char  index1;
    u_char  index2;
};

extern void rc4_init(struct rc4_state *state, const u_char *key, int keylen);
extern void rc4_crypt(struct rc4_state *state,
                      const u_char *inbuf, u_char *outbuf, int buflen);
void rc4_util(u_char *inbuf, int buflen,const u_char *key, int keylen);
#endif //ANDROIDSOENHANCE2_RC4UTIL_H
