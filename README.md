dedupe
======

Dedupe is a Java project that consolidates all distinct files into a "dedupe" directory with a manifest detailing where they came from.  It is able to "redupe" the original folder by processing this directory in reverse.  This aims to decrease the size of CI downloads without changing existing build logic or structure of the project.

In order to maximize utility, it also aims to be a self contained jar with no external dependencies (besides Java).

Building
========

The build has been tested with Gradle 1.7 and Oracle's JDK 1.6 but should work fine with an older Gradle version (very simple build logic) and any JDK 1.6 or newer.

Run 

    gradle build

in the root directory.  The resulting jar will show up in build/libs.

Usage
=====

Please run 

    java -jar dedupe.jar -h 

for usage help.

Roadmap
=======

Next on the feature list will be the ability to use symbolic links in the "redupe" process if the operating system supports them.
