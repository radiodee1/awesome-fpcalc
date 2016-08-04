# awesome-fpcalc-android
A personal repository for android fpcalc test app development

# notes
I used the following github project for a necessary file picker.

https://github.com/spacecowboy/NoNonsense-FilePicker

The author of this library is Jonas Kalderstam.

# Project Organization :

The central project folder is called 'awesome-fpcalc-android'. In this folder are two other folders and this README file. One folder is called 'fpcalc-testapp', and it contains the code for the android studio app. The other folder is called 'fpcalc-build' and this folder holds an exact copy of the phorton1 ffmpeg and chromaprint source code, checked out to 'multi-2.7'. The original version control has been removed because I had to modify the multi-configure and multi-make scripts. In any case the 2.7 ffmpeg code works for me. I had the choice of including the original code or setting up a git submodule, but since I had to change the files I decided to include the code. I have retained all the licensing document material.

# Project Development :

The object of this project is to test out the chromaprint fpcalc program on the average android device. Much of the work is done for us as chromaprint and ffmpeg have been compiled for arm and arm-v7 already. (chromaprint requires ffmpeg to work.)

For chromaprint to work, ffmpeg must be compiled. I didn't know how to compile a module and have it depend on another module, so finding sources that did just that was very helpful. On top of that, jni code had to be written. J.N.I. stands for Java Native Interface, and it is the bridge between java and C or C++. Here again the work was done for us. The sources even include a few handy build scripts.

The author of the sources I found for compiling chromaprint for android is 'Patrick Horton', going by the name 'phorton1' on 'bitbucket.org' . The link to his profile is below.

https://bitbucket.org/phorton1/

The author provides fully compiled binaries, but they didn't work for me. I had to download the sources and compile them myself. Thanks go to Horton for what must have been a great amount of work.

The last time phorton1 updated his project was August of 2015. Since then a number of things have changed. In a few places the build scripts needed to be edited. The NDK has been updated. I used r12b. The r12b NDK uses gcc 4.9 whereas his NDK used gcc 4.8 . Also, I am using 64 bit linux. I believe that he was using 32 bit Windows. Below is a link to the page where the library source can be found and a description of what I did to produce libraries I could use.

https://bitbucket.org/phorton1/chromaprint

First off I cloned the  ffmpeg and chromaprint repositories. Then I did a 'git checkout multi-2.7' in both directories. Then I edited the multi-configure and multi-make scripts. I changed the host to x86_64 and the gcc version number to 4.9 wherever it occurred. Finally, I disabled the 'win' build so that it is never used  (I use linux and the mingw stuff will crash). My test app uses the libraries produced.

After this I started working on my android test application. In the application I call fpcalc and pass it the URL of an audio file. Then I send the finger print to the acoustid lookup service. I parse the Json that is returned and display the name of the audio track on the screen. When I first started I compiled the 0.9 version of ffmpeg, but this didn't work on both of my devices.  Later I tried the 2.7 ffmpeg sources, and this worked. My java code actually gzips the body of the post request being sent to the acoustid service  (as recommended in the documentation). The link to the acoustid instructions is below.

https://acoustid.org/webservice

# Thanks :

Thanks go to Patrick Horton, Jonas Kalderstam, and Lukas Lalinsky.

