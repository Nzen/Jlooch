
Jlooch is a digital sound synthesis gui implemented with [jsyn](http://www.softsynth.com/jsyn/beta/). I'm involved because the original is an applet, and applets were EoL back when I first encountered this.

Original readme :

To compile and run jlooch, you will need the JSyn Developer SDK available
from http://www.softsynth.com/jsyn/ (it's free -- yay!).  You will also
need java and the java SDK, of course.

Once those are set and installed, you will probably need to modify
the Makefile to reflect your own installation/directory setup.
With any degree of luck you will only have to change the

	JAVA_HOME=/usr/java/jdk1.3.1_01

and the

	JSYNSDKDIR     = /usr/local/src/jsyn142a_linux386_sdk

lines to the proper locations on your own machine.


After this is all set, you should be able type

	make

to build all the proper java class files.  Then to run the app (and
whenever you want to run it in the future), type

	make go

and you should be up and droning!

One thing -- the jlooch.java file needs to be modified slightly
to run as an applet in a browser context (I don't use a layoutManager).
I'll be happy to send you the applet/browser version if you'd like.

Please check out http://music.columbia.edu/~brad/jlooch for more
information.  You can also send me e-mail: brad@music.columbia.edu,
and sometimes I answer it.

Have fun, enjoy them sounds!

Brad Garton
October, 2001
