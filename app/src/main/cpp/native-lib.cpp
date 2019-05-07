#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_fieldarea_whon_fieldarea_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++,厉害得嘞";
    return env->NewStringUTF(hello.c_str());
}
