#include <stdlib.h>
#include <openssl/evp.h>
#include "io_gomint_server_jni_hash_HashNative.h"

typedef unsigned char byte;

void initializeDigest(EVP_MD_CTX *mdCtx) {
    EVP_DigestInit_ex(mdCtx, EVP_sha256(), NULL);
}

jlong JNICALL Java_io_gomint_server_jni_hash_HashNative_init(JNIEnv *env, jclass clz) {
    EVP_MD_CTX *mdCtx = EVP_MD_CTX_create();
    initializeDigest(mdCtx);
    return (jlong) mdCtx;
}

void JNICALL Java_io_gomint_server_jni_hash_HashNative_update(JNIEnv *env, jclass clz, jlong ctx, jlong in, jint bytes) {
    EVP_DigestUpdate((EVP_MD_CTX*) ctx, (byte*) in, bytes);
}

jbyteArray JNICALL Java_io_gomint_server_jni_hash_HashNative_digest(JNIEnv *env, jclass clz, jlong ctx) {
    // SHA-256 produces 32 bytes (256 bits)
    unsigned char output[32];

    // Hash the final output.
    EVP_MD_CTX *mdCtx = (EVP_MD_CTX*) ctx;
    EVP_DigestFinal_ex(mdCtx, (unsigned char*) output, NULL);

    // Reinitialize the context for further handling.
    initializeDigest(mdCtx);

    // Copy array into Java byte array.
    jbyteArray array = env->NewByteArray(32);
    env->SetByteArrayRegion(array, 0, 32, (jbyte*) output);
    return array;
}

JNIEXPORT void JNICALL Java_io_gomint_server_jni_hash_HashNative_free(JNIEnv *env, jclass clz, jlong ctx) {
    EVP_MD_CTX_destroy((EVP_MD_CTX*) ctx);
}
