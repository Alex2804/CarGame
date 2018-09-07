# CarGame

This is a shool project, with the aim, to provide a basic game in Java.  
It was written in Java with Swing.


## Sprites
#### Inheration Tree:
- Object
  - Car
    - PlayerCar
    - EnemyCar
      - PoliceCar

The **Object** class represent Sprites, with a position, image and hitox.
**Car** inhertis from **Object** and adds methods, for vertical- and horizontal movement/speed.  
**PlayerCar** inherits from **Car** and implements additional propertys and methods, to controll the fuel of the **PlayerCar** and for KeyEvents, to controll the direction of the car movement.  
**EnemyCar** also inherits from **Car** and implements additional methods, for automated lane changes and movement updates.  
**PoliceCar** inherits from **EnemyCar** and adds extra hitboxes and methods, to avoid collisions with other obstacles

#### Hitboxes:
The Hitboxes of the **Object** class are by default the bounding recatangle of its image. It is possible, to generate pixel detailed hitboxes. For pixel detailed hitboxes, every pixel of the image is parsed and added to the hitbox (java.awt.geom.Area), if the pixel is not transpartent (alpha of 0). If a path to a hitbox.ser file is given, the **Object** tries to read the hitbox (java.awt.geom.Path2D) and cast it to an Area. If the file doesn't exists, it is created (under res/hitbox).  
There are extra hitbox.ser files for **EnemyCar**, **PlayerCar**, **PoliceCar** which are created by the first startup and read in the following startups. Once they are read, they are saved in a static attribute as Area, to reduce loading times.


## Game
#### Structure:
- panel
  - GameBoard
  - PanelManager
   - Menu
  - LanguageListener
  - Listener
_ ObstacleManager
- StreetManager
- ResourceBundleEx
- MainWindow

In the **GameBoard** class, the gameloop is running. In the gameloop, all positions of the obstacles and cars are updated. Also it is checked, if the **PlayerCar** collides with any other obstacle. Also the repainting is done in the gameloop after every update.

The **PanelManager** manages all menus, declared in **Menu**and show/hide them when necessary.

The **ObstacleManager** manages all the obstacles and move all the stored sprites with only one method call (update(int height). Also it handles the generation of new obstacles (barriers, enemys, policecars, fueltanks).

The **StreetManager** stores multiple **Objects** with images of street lanes in a grid and move them with one speed, as if they are one big road. The **StreetManager** generates a matching count of horizontal and vertical street **Objects**, depending on the size of the panel and the size of the street **Objects**.

The **LanguageListener** and **Listener** are interfaces, which can get implemented in classes, which want's to recive the game state changes / signals. The LanguageListener only gets triggered, If the language was changed and updates all the UI components with text. The Listener interface, is for the game signals, and handle state changes, like "gameStarted" or instructions like "startGame".

The **ResourceBundleEx** give words, by key, in choosen language.
