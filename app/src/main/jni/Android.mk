LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# opencv
OPENCVROOT:=C:\OpenCV-android-sdk
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include $(OPENCVROOT)/sdk/native/jni/OpenCV.mk
LOCAL_SRC_FILES := com_imperialsoupgmail_tesseractexample_MyNDK.cpp

LOCAL_LDLIBS += -llog
LOCAL_MODULE    := MyOpencvLibs

include $(BUILD_SHARED_LIBRARY)