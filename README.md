# CarGame

This is a shool project, with the aim, to provide a basic Game in Java.  
It was written in Java and Swing (a rewrite with OpenGL is planed).


## Sprites
#### Structure:
- Object
  - Car
    - PlayerCar
    - EnemyCar
      - PoliceCar

The **Object** class represent Sprites, whith a position, image and hitox.
**Car** inhertis from **Object** and adds methods, for vertical- and horizontal movement/speed.  
**PlayerCar** inherits from **Car** and implements additional propertys and methods, to controll the fuel of the **PlayerCar** and for KeyEvents, to controll the direction of the car movement.  
**EnemyCar** also inherits from **Car** and implements additional methods, for automated lane changes and movement updates.  
**PoliceCar** inherits from **EnemyCar** and adds extra hitboxes and methods, to avoid collisions with other obstacles

#### Hitboxes:
The Hitboxes of the **Object** class are by default the bounding recatangle of its image. It is possible, to generate pixel detailed hitboxes. For pixel detailed hitboxes, every pixel of the image is parsed and added to the hitbox (java.awt.geom.Area), if the pixel is not transpartent (alpha of 0). If a path to a hitbox.ser file is given, the **Object** tries to read the hitbox (java.awt.geom.Path2D) and cast it to an Area. If the file doesn't exists, it is created (under res/hitbox).  
There are extra hitbox.ser files for **EnemyCar**, **PlayerCar**, **PoliceCar** which are created by the first startup and read in the following startups. Once they are read, they are saved in a static attribute as Area, to reduce loading times.
