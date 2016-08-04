# Building the '.so' files from bitbucket sources

1.  NOTE: when and if you start over erase the folders `ffmpeg` and `chromaprint` along with `_build` and `_install`.
2.  Execute the `./setup.sh` script. This downloads the sources and builds them. It is assumed that your NDK directory is in the `$NDK` variable and that the build computer is `linux-x86_64`. I used ndk-r12b.
3.  Execute the `./move_files.sh` script. This would copy the '.so' files to the app.
4.  Try the app.
