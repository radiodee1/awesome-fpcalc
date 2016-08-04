#rm -fr ffmpeg chromaprint

if [ ! -d "ffmpeg" ] ; then
    echo "nothing!!"
    git clone https://git.ffmpeg.org/ffmpeg.git ffmpeg
fi

if [ ! -d "kissfft" ] ; then
    hg clone http://hg.code.sf.net/p/kissfft/code kissfft
fi

if [ ! -d "chromaprint" ] ; then
    git clone https://bitbucket.org/acoustid/chromaprint.git
fi

### COPY MAKEFILES, ETC. ###

if [ -d "ffmpeg" ] ; then
    cp -f makefiles/*ffmpeg ffmpeg/.

fi

if [ -d "chromaprint" ] ; then
    cp -f makefiles/*chromaprint chromaprint/.
    cp -f kissfft/*.h kissfft/*.c kissfft/*.hh chromaprint/src/.
    mkdir -p chromaprint/src/tools
    cp -f kissfft/tools/* chromaprint/src/tools/.
fi

#exit 0

DIRSTART=`pwd`

echo $DIRSTART

#cd ffmpeg
#git checkout n2.7
#cd $DIRSTART
#cd chromaprint
#git checkout v1.3.2
#cd $DIRSTART


cd chromaprint/examples
cp -f $DIRSTART/patch/*.c .
patch -p1 fpcalc.c < $DIRSTART/patch/patch_examples.patch
#cd $DIRSTART/ffmpeg
#patch -p1 < $DIRSTART/patch/patch_2.7.patch

