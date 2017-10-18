//
// Created by RoJongHyok on 8/4/2017.
//

#include "com_imperialsoupgmail_tesseractexample_MainActivity.h"
int toGray(Mat img, Mat& gray ){
    cvtColor(img,gray,CV_RGBA2GRAY);

    if (gray.rows==img.rows && gray.cols==img.cols) return 1;
    return 0;
}

JNIEXPORT jint JNICALL Java_com_imperialsoupgmail_tesseractexample_MainActivity_convertGray
        (JNIEnv *, jclass, jlong addRgba, jlong addGray){

        Mat & mRgba=*(Mat*)addRgba;
        Mat & mGray=*(Mat *)addGray;

       int conv;
       jint retVal;

       conv=toGray(mRgba,mGray);

        retVal=(jint)conv;
     return retVal;
}


