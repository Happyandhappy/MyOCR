//
// Created by RoJongHyok on 8/4/2017.
//

#include "com_imperialsoupgmail_tesseractexample_MainActivity.h"
int toGray(Mat img, Mat& gray ){

    cvtColor(img,gray,CV_RGBA2GRAY);
    GaussianBlur(gray,gray,Size(3,3),0);
    threshold(gray,gray,100,255,THRESH_BINARY);


 //Cutting above and bottom contents of image
    int deltaX,deltaY;
    deltaX=(gray.cols*80)/2592;
    deltaY=(gray.rows*300)/4608;
    for (int i=0;i<deltaY;i++){
        for (int j=0; j<gray.rows;j++){
            gray.at<uchar>(i,j)=255;
            gray.at<uchar>(gray.rows-i,j)=255;
        }
    }

    for (int i=0;i<deltaX;i++){
        for (int j=0;j<gray.rows;j++)
            gray.at<uchar>(j,i)=255;
    }
    return 1;
}

JNIEXPORT jint JNICALL Java_com_imperialsoupgmail_tesseractexample_MainActivity_convertGray
        (JNIEnv *, jclass, jlong addRgba, jlong addGray){

        Mat & mRgba=*(Mat*)addRgba;
        Mat & mGray=*(Mat *)addGray;
       int conv;
       jint retVal;
       conv=toGray(mRgba,mGray);
       mRgba.release();
        retVal=(jint)conv;
     return retVal;
}


