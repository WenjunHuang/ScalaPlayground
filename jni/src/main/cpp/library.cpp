#include <jni.h>
#include <iostream>
#include "myjni_HelloJNI.h"

JNIEXPORT void JNICALL Java_myjni_HelloJNI_hello(JNIEnv *env, jobject thisObj) {
  std::cout << "Hello World" << std::endl;
  return;
}

JNIEXPORT jdouble JNICALL Java_myjni_HelloJNI_average(JNIEnv *env, jobject thisObj, jint a, jint b) {
  jdouble result = (a + b) / 2.0;
  return result;
}
