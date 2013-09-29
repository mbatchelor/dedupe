dedupe
======

Dedupe is a Java project that consolidates all distinct files into a "dedupe" directory with a manifest detailing where they came from.  It is able to "redupe" the original folder by processing this directory in reverse.  This aims to decrease the size of CI downloads without changing existing build logic or structure of the project.
