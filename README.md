# CarGame

This is a shool project, with the aim, to provide a basic Game in Java.  
It was written in Java and Swing (a rewrite with OpenGL is planed).

The uses swing timer as game loop and uses the Object class as sprites. The hitbox of the sprites is the bounding rectangle of the image, or pixelgenerated, by adding all none trasparent pixels to an java awt Area. This hitbox Area, created by pixel parsing, gets written to *.ser file, if a file path is given (as constructor parameter). If a path to hitbox.ser file is given, It first tries to read the hitbox from file.
