//
// Created by Lin on 2018/1/20.
//
#include "Rc4Util.h"
#include <stdlib.h>
void swap_bytes(u_char *a, u_char *b)
{
    u_char temp;

    temp = *a;
    *a = *b;
    *b = temp;
}

/*
* Initialize an RC4 state buffer using the supplied key,
* which can have arbitrary length.
*/
void
rc4_init(struct rc4_state *const state, const u_char *key, int keylen)
{
    u_char j;
    int i;

    /* Initialize state with identity permutation */
    for (i = 0; i < 256; i++)
        state->perm[i] = (u_char)i;
    state->index1 = 0;
    state->index2 = 0;

    /* Randomize the permutation using key data */
    for (j = i = 0; i < 256; i++) {
        j += state->perm[i] + key[i % keylen];
        swap_bytes(&state->perm[i], &state->perm[j]);
    }
}

/*
* Encrypt some data using the supplied RC4 state buffer.
* The input and output buffers may be the same buffer.
* Since RC4 is a stream cypher, this function is used
* for both encryption and decryption.
*/
void
rc4_crypt(struct rc4_state *const state,
          u_char *inbuf, int buflen)
{
    int i;
    u_char j;

    for (i = 0; i < buflen; i++) {

        /* Update modification indicies */
        state->index1++;
        state->index2 += state->perm[state->index1];

        /* Modify permutation */
        swap_bytes(&state->perm[state->index1],
                   &state->perm[state->index2]);

        /* Encrypt/decrypt next byte */
        j = state->perm[state->index1] + state->perm[state->index2];
        inbuf[i] = inbuf[i] ^ state->perm[j];
    }
}
/**
 *
 * @param inbuf
 * @param buflen
 * @param key
 * @param keylen
 */
void rc4_util(u_char *inbuf, int buflen,const u_char *key, int keylen){
    struct rc4_state * state = (struct rc4_state *)malloc(258);
    rc4_init(state,key, keylen);
    rc4_crypt(state, inbuf, 184);
}

