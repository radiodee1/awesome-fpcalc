some guidance on attempting a build


```
    ./setup.sh
    cd ffmpeg
    ./multi-configure-ffmpeg all 
    ./multi-make-ffmpeg all install
    cd ../chromaprint
    ./multi-configure-chromaprint all
    ./multi-make-chromaprint all install
    cd ..
    ./move_files.sh
```

if you want to start over `rm -fr _install _build ffmpeg chromaprint`.  

Try fpcalc-build-complete if the resulting binaries don't work on your device.
