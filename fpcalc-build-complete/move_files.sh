#!/bin/bash

echo $#
echo $1

NUM=$1

if [ "$#" -eq "0" ] ; then
    NUM=2.7
fi

echo $NUM

LIBNAME=libfpcalc.so

if [ ! -f ../fpcalc-testapp/app/src/main/jniLibs/armeabi-v7a/$LIBNAME.old ] ; then
    mv -v ../fpcalc-testapp/app/src/main/jniLibs/armeabi-v7a/$LIBNAME ../fpcalc-testapp/app/src/main/jniLibs/armeabi-v7a/$LIBNAME.old
fi

if [ ! -f ../fpcalc-testapp/app/src/main/jniLibs/armeabi/$LIBNAME.old ] ; then
    mv -v ../fpcalc-testapp/app/src/main/jniLibs/armeabi/$LIBNAME ../fpcalc-testapp/app/src/main/jniLibs/armeabi/$LIBNAME.old
fi

if [ ! -f ../fpcalc-testapp/app/src/main/jniLibs/x86/$LIBNAME.old ] ; then
    mv -v ../fpcalc-testapp/app/src/main/jniLibs/x86/$LIBNAME ../fpcalc-testapp/app/src/main/jniLibs/x86/$LIBNAME.old
fi

cp -v _install/$NUM/_linux/arm7s/libfpcalc.so ../fpcalc-testapp/app/src/main/jniLibs/armeabi-v7a/.
cp -v _install/$NUM/_linux/arms/libfpcalc.so ../fpcalc-testapp/app/src/main/jniLibs/armeabi/.
cp -v _install/$NUM/_linux/x86s/libfpcalc.so ../fpcalc-testapp/app/src/main/jniLibs/x86/.

ls -hal ../fpcalc-testapp/app/src/main/jniLibs/*
