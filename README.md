# CarGame

This is a shool project, with the aim, to provide a basic Game in Java.  
It was written in Java and Swing (a rewrite with OpenGL is planed).


## Sprites
#### Structure
- Object
  - Car
    - PlayerCar
    - EnemyCar
      - PoliceCar

The **Object** class represent Sprites, whith a position and image. The hitbox of the **Object** is by default the bounding rectangle of the image. It is possible, to generate a pixel detailed hitbox, where every Pixel of the image is parsed and added to an awt Area, if the pixel is not transparent.  
**Car** inhertis from **Object** and adds methods, for vertical- and horizontal movement/speed.  
**PlayerCar** inherits from **Car** and implements additional propertys and methods, to controll the fuel of the **PlayerCar** and for KeyEvents, to controll the direction of the car movement.  
**EnemyCar** also inherits from **Car** and implements additional methods, for automated lane changes and movement updates.  
**PoliceCar** inherits from **EnemyCar** and adds extra hitboxes and methods, to avoid collisions with other obstacles
