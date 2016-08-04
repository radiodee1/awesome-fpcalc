#rm -fr ffmpeg chromaprint

if [ ! -d "ffmpeg" ] ; then
    #echo "nothing!!"
    git clone https://bitbucket.org/phorton1/ffmpeg.git
fi

if [ ! -d "chromaprint" ] ; then
    git clone https://bitbucket.org/phorton1/chromaprint.git
fi

### COPY MAKEFILES, ETC. ###

if [ -d "ffmpeg" ] ; then
    cd ffmpeg
    git checkout multi-2.7
    chmod a+x multi-*
    cd ..
fi

if [ -d "chromaprint" ] ; then
    cd chromaprint
    git checkout multi-2.7
    chmod a+x multi-*
    cd ..
fi


DIRSTART=`pwd`

echo $DIRSTART

cd $DIRSTART
patch -p1 < $DIRSTART/patch/patch_2.7.patch

#exit 0

cd ffmpeg
./multi-configure all
./multi-make all install
cd ..

cd chromaprint
./multi-configure all
./multi-make all install
cd ..


