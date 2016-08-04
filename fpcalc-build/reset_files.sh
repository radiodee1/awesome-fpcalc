

LIBNAME=libfpcalc.so

if [ -f ../fpcalc-testapp/app/src/main/jniLibs/armeabi-v7a/$LIBNAME.old ] ; then
    mv -v ../fpcalc-testapp/app/src/main/jniLibs/armeabi-v7a/$LIBNAME.old ../fpcalc-testapp/app/src/main/jniLibs/armeabi-v7a/$LIBNAME
fi

if [ -f ../fpcalc-testapp/app/src/main/jniLibs/armeabi/$LIBNAME.old ] ; then
    mv -v ../fpcalc-testapp/app/src/main/jniLibs/armeabi/$LIBNAME.old ../fpcalc-testapp/app/src/main/jniLibs/armeabi/$LIBNAME
fi

if [ -f ../fpcalc-testapp/app/src/main/jniLibs/x86/$LIBNAME.old ] ; then
    mv -v ../fpcalc-testapp/app/src/main/jniLibs/x86/$LIBNAME.old ../fpcalc-testapp/app/src/main/jniLibs/x86/$LIBNAME
fi

ls -hal ../fpcalc-testapp/app/src/main/jniLibs/*
