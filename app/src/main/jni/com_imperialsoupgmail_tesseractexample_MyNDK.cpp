#include "com_imperialsoupgmail_tesseractexample_MyNDK.h"

JNIEXPORT jint JNICALL Java_com_imperialsoupgmail_tesseractexample_MyNDK_getMyString
  (JNIEnv *, jclass, jlong addrRgba, jlong addrGray){
  Mat & mRgb=*(Mat *)addrRgba;
  Mat & mGray=*(Mat *)addrGray;

    int conv;
    jint retVal;
    conv=toGray(mRgb,mGray);
    retVal=(jint)conv;
    return retVal;
}

int toGray(Mat img, Mat& gray){
    cvtColor(img,gray,CV_RGB2GRAY);
    if (gray.rows==img.rows && gray.cols==img.cols) return 1;
    return 0;
}