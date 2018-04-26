package alex2804;

import alex2804.objects.Object;
import alex2804.objects.cars.EnemyCar;
import alex2804.objects.cars.PlayerCar;
import alex2804.objects.cars.PoliceCar;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ObstacleManager{
    private EnemyCar sampleEnemey; //EnemyCar to get dimensions from
    private Object sampleBarrier;
    private PoliceCar samplePolice; //PoliceCar to get dimensions from

    private ArrayList<EnemyCar> enemys; //holds all enemys
    private ArrayList<Object> barriers; //holds all barriers
    private ArrayList<Object> fuelTanks; //holds all fuel tanks
    private ArrayList<PoliceCar> police; //holds all PoliceCar's
    private StreetManager streetManager; //holds reference to streetManager

    private int collisionCounter = 0;

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    private double nextEnemy = 0; //time until next enemy spawns
    public double nextEnemyMin = 0; // in milliseconds, time per lane
    public double nextEnemyMax = 25000; // in milliseconds, time per lane
    private double enemyTime = 0; //time since last enemy spawn

    private int lastTrack = -1; //last track that was a obstacle
    private int freeTrack = -1; //track that is free for an amount of time
    private double changeFreeTrack = 3000; //time until freeTrack changes
    private double freeTrackTime = 0; //time since freeTrack was changed

    private double timeStart = 0; //caches current time to calculate the time since last gameloop repeat

    private double changeTrackTime = 0; //time since last lane change
    private double changeTrackTimeMin = 1000; //minimum time until next lane change
    private double changeTrackTimeMax = 10000; //minimum time untli next lane change
    private double changeTrack = changeTrackTimeMax; //time until EnemyCar changes track

    private double newFuelTankDistanceMin = PlayerCar.getFuelMaxDistance() * 0.2; //minimal distance until fueltank is generated
    private double newFuelTankDistance = newFuelTankDistanceMin; //distance until new fueltank is generated
    private double newFuelTankDistanceMax = PlayerCar.getFuelMaxDistance() * 0.95; //maximum distance until fueltank is generated
    private double newFuelTank = 0; //distance since fueltank was generated
    private double newFuelTankDistanceAdditive = PlayerCar.getFuelMaxDistance() * 0.02; //distance, added to the distance after which a new fueltank gets generated, after every new fueltank

    private int newBarrierDistanceMin = 1000;
    private int newBarrierDistanceMax = 10000;
    private double newBarrierDistance = newBarrierDistanceMax;
    private double newBarrier = 0;

    private double newPoliceCarTimeMax = 10000;
    private double newPoliceCarTime = 0;
    private int maxPoliceCount = 1;
    private double newPoliceCar = newPoliceCarTimeMax / 2;
    public boolean policeCar = true;

    public ObstacleManager(StreetManager streetManager, boolean policeCar){
        init(streetManager, new ArrayList<EnemyCar>(), new ArrayList<Object>()); //new ObstacleManager with no enemys and barriers
        this.policeCar = policeCar;
    }
    public ObstacleManager(StreetManager streetManager, ArrayList<EnemyCar> enemys, ArrayList<Object> barriers){
        init(streetManager, enemys, barriers); //new ObstacleManager with given enemys and barriers
    }
    private void init(StreetManager streetManager, ArrayList<EnemyCar> enemys, ArrayList<Object> barriers){ //initialize params with given values, called by constructors
        setEnemys(enemys);
        setBarriers(barriers);
        setPolice(new ArrayList<PoliceCar>());
        this.streetManager = streetManager;
        sampleEnemey = new EnemyCar(0, 0, streetManager);
        samplePolice = new PoliceCar(0, 0, streetManager, 0, 0, 0);
        sampleBarrier = new Object(0, 0, "res/barrier.png");
        setFuelTanks(new ArrayList<>());
        timeStart = System.currentTimeMillis(); //reset time
    }

    public void generateObstacles(int height) {
        newFuelTank += streetManager.getSpeed(); //add speed of street to distance, until new fueltank is generated
        if(newFuelTank >= newFuelTankDistance / ((int)(streetManager.getHorizontalStreetCount() * 0.1) + 1)){ //if distance since last fueltank is equal or higher as given distance
            if(newFuelTankDistance + newFuelTankDistanceAdditive < newFuelTankDistanceMax){
                newFuelTankDistance += newFuelTankDistanceAdditive; //increase distance until new fueltank
            }
            newFuelTank = 0; //set distance since last fueltank to 0
            addNewFuelTank(); //generate new fueltank
        }

        newBarrier += streetManager.getSpeed();
        if(newBarrier >= newBarrierDistance / ((int)(streetManager.getHorizontalStreetCount() * 0.1) + 1)){
            newBarrierDistance = random.nextInt(newBarrierDistanceMin, newBarrierDistanceMax);
            newBarrier = 0;
            addNewBarrier();
        }

        //increase times since last repeat
        if(policeCar)
            newPoliceCarTime += (System.currentTimeMillis()) - timeStart;
        freeTrackTime += (System.currentTimeMillis() - timeStart);
        enemyTime += (System.currentTimeMillis() - timeStart);
        changeTrackTime += (System.currentTimeMillis() - timeStart);
        timeStart = System.currentTimeMillis();

        if(newPoliceCarTime >= newPoliceCar && getPolice().size() < maxPoliceCount && policeCar){
            newPoliceCarTime = 0;
            newPoliceCar = newPoliceCarTimeMax;
            addNewPolice(height);
        }

        if(freeTrackTime >= changeFreeTrack){ //if time since last free track change is big enough
            freeTrack = random.nextInt(0, streetManager.getHorizontalStreetCount()); //new random free track
            freeTrackTime = 0; //time since free track changed is 0
        }

        if(enemyTime >= nextEnemy){ //if time since last enemy was generated is big enough
            addNewEnemy(); //adds new enemy to enemys
            enemyTime = 0; //time since last generated enemy is 0
            nextEnemy = random.nextDouble(nextEnemyMin / (streetManager.getHorizontalStreetCount() * 1.5),
                    nextEnemyMax / (streetManager.getHorizontalStreetCount() * 1.5)); //time to next enemy
        }

        if(changeTrackTime >= changeTrack && enemys.size() > 0){
            EnemyCar enemy = getEnemy(random.nextInt(getEnemys().size())); //get enemy for lane change
            double speed = random.nextDouble(1, 3); //horizontal speed for lange change
            int track = enemy.getNewRandomTrack(); //new track
            enemy.changeTrack(track, speed); //initialize lane change
            changeTrackTime = 0;
            changeTrack = random.nextDouble(changeTrackTimeMin, changeTrackTimeMax); //time to next lane change
        }
    }

    public void addNewFuelTank(){
        Object fuelTank = new Object(0, 0, "res/fueltank.png"); //new object
        //int track = random.nextInt(0, streetManager.getHorizontalStreetCount());
        int track = random.nextInt(0, streetManager.getHorizontalStreetCount()); //track
        double x = streetManager.getXAdditive() + StreetManager.getSampleStreet().getWidth() * track +
                (0.5 * streetManager.getSampleStreet().getWidth() - 0.5 * fuelTank.getWidth()); //compute x with given track
        double y = -fuelTank.getHeight()-50;
        fuelTank.moveTo(x, y); //move object to x and y
        addToFuelTanks(fuelTank); //add fuel tank object
    }

    public void addNewBarrier(){
        Object barrier = getNewBarrier(); //get new enemy object
        if(!checkObstacleCollision(barrier)){ //only add enemy if don't collide with any other enemy
            addToBarriers(barrier); //add to ArrayList
        }
    }
    public Object getNewBarrier(){
        int track = random.nextInt(0, streetManager.getHorizontalStreetCount()); //get new track

        int xAdditive = random.nextInt(1,StreetManager.getSampleStreet().getWidth() - sampleBarrier.getWidth() - 1); //not centered in lane
        int x = streetManager.getXAdditive() + StreetManager.getSampleStreet().getWidth() * track + xAdditive; //x (track * streetwidth + xadditive, that not centered)

        int y = - sampleBarrier.getHeight()-100; //y out of frame

        Object barrier = new Object(x, y, "res/barrier.png");
        return barrier; //return created barrier
    }

    public void addNewEnemy(){
        EnemyCar enemy = getNewEnemy(); //get new enemy object
        if(!checkObstacleCollision(enemy)){ //only add enemy if don't collide with any other enemy
            addToEnemys(enemy); //add to ArrayList
        }
    }
    public EnemyCar getNewEnemy(){
        int track = random.nextInt(0, streetManager.getHorizontalStreetCount()); //get new track until it isn't free track or the last track
        if(streetManager.getHorizontalStreetCount() > 2){ //if more than 2 tracks
            while(track == lastTrack || track == freeTrack){
                track = random.nextInt(0, streetManager.getHorizontalStreetCount()); //get new track until it isn't free track or the last track
            }
        }
        lastTrack = track; //set last track to new track

        int xAdditive = random.nextInt(1,StreetManager.getSampleStreet().getWidth() - sampleEnemey.getWidth() - 1); //not centered in lane
        int x = streetManager.getXAdditive() + StreetManager.getSampleStreet().getWidth() * track + xAdditive; //x (track * streetwidth + xadditive, that not centered)

        int y = -sampleEnemey.getHeight()-100; //y out of frame
        double horizontalSpeed = 0; //horizontal speed = 0
        double verticalSpeed = random.nextDouble(-4, -2); //random vertical speed

        EnemyCar enemy = new EnemyCar(x, y, streetManager, horizontalSpeed, verticalSpeed, track); //create new enemy object
        return enemy; //return created enemy
    }
    public boolean addNewPolice(int height){
        PoliceCar policeCar = getNewPolice(height);
        if(!checkObstacleCollision(policeCar)){
            addToPolice(policeCar);
            return true;
        }
        return false;
    }
    public PoliceCar getNewPolice(int height){
        int track = random.nextInt(0, streetManager.getHorizontalStreetCount()); //get new track until it isn't free track or the last track

        int xAdditive = random.nextInt(1,StreetManager.getSampleStreet().getWidth() - samplePolice.getWidth() - 1); //not centered in lane
        int x = streetManager.getXAdditive() + StreetManager.getSampleStreet().getWidth() * track + xAdditive; //x (track * streetwidth + xadditive, that not centered)

        int y = height; //y out of panel
        double horizontalSpeed = 0; //horizontal speed = 0
        double verticalSpeed = 0; //vertical speed

        PoliceCar police = new PoliceCar(x, y, streetManager, horizontalSpeed, verticalSpeed, track);
        return police;
    }

    public void update(int height){//update necessary
        move(); //move all obstacles
        generateObstacles(height); //check if new obstacles has to been generated
        remove(height); //remove obstacles, which are out of the given height (panel)

        collisionCounter ++;
        if(collisionCounter == 10){
            collisionCounter = 0;

            checkEnemeyEnemyCollision(); //checks if enemys collide under each other
            checkEnemyBarrierCollision(); //checks if enemys collide with barriers
            if(policeCar)
                checkPoliceObstacleCollision(); //checks if a police car collides with an other obstacle
        }
    }
    public void updatePoliceCars(PlayerCar target){
        for(PoliceCar police : getPolice()){
            police.updateTrack(this, target);
        }
    }


    public boolean checkObstacleCollision(Object object){
        if(object instanceof PlayerCar){ //if object is an player car
            checkFuelTankCollision((PlayerCar) object); //check if colliding with fueltank
        }
        return checkObstacleCollision(object.getHitbox());
    }
    public boolean checkObstacleCollision(Area hitbox){ //checks if hitbox of object intersects with hitbox of any obstacle
        return (checkEnemyCollision(hitbox) || checkBarrierCollision(hitbox)); //return false if not intersecting with anything
    }

    public boolean checkEnemyCollision(Area hitbox){ //check if colliding with any enemy
        for (EnemyCar enemyCar : getEnemys()) {
            if(enemyCar.checkHitboxIntersection(hitbox) == true){
                return true; //if colliding leave method and return true
            }
        }
        return false;
    }
    public boolean checkBarrierCollision(Area hitbox){ //check if colliding with any barrier
        for(Object barrier : getBarriers()){
            if(barrier.checkHitboxIntersection(hitbox))
                return true; //if colliding leave method and return ture
        }
        return false;
    }
    public boolean checkPoliceCollision(Area hitbox){
        for(PoliceCar police : getPolice()){
            if(police.checkHitboxIntersection(hitbox))
                return true;
        }
        return false;
    }
    public boolean checkFuelTankCollision(PlayerCar playerCar){ //check if player car is colliding with any fueltank object
        for(Object fuelTank : getFuelTanks()){
            if(playerCar.checkHitboxIntersection(fuelTank)){ //if colliding
                playerCar.fillFuel(); //fill the fuel of the player car
                removeFromFuelTanks(fuelTank); //destroy the fuel tank
                return true;
            }
        }
        return false;
    }

    public void checkEnemyBarrierCollision(){ //check if enemys colliding with barriers
        for(Object barrier : getBarriers()){
            for(EnemyCar enemyCar : getEnemys()){
                if(enemyCar.checkHitboxIntersection(barrier) == true){
                    enemyCar.setAccident(true);
                }
            }
        }
    }
    public void checkEnemeyEnemyCollision(){ //check if enemys colliding with each other
        ArrayList<EnemyCar> enemysCache = getEnemys();
        for(EnemyCar enemy1: getEnemys()) {
            enemysCache.remove(enemy1);

            for (EnemyCar enemy2 : enemysCache) {
                if (enemy1.checkHitboxIntersection(enemy2)) {
                    enemy1.setAccident(true);
                    enemy2.setAccident(true);
                }
            }
        }
    }
    public void checkPoliceObstacleCollision(){
        for(PoliceCar police : getPolice()){
            for(EnemyCar enemyCar : getEnemys()){
                if(police.checkHitboxIntersection(enemyCar)){
                    police.setAccident(true);
                    enemyCar.setAccident(true);
                }
            }
            for(Object barrier : getBarriers()){
                if(police.checkHitboxIntersection(barrier)){
                    police.setAccident(true);
                }
            }
        }
    }

    public void remove(int height){
        removeEnemys(height);
        removeBarriers(height);
        removeFuelTanks(height);
        removePolice(height);
    }
    public void removeEnemys(int height){
        for(EnemyCar enemy : getEnemys()){
            if(enemy.getY() > height){
                removeFromEnemys(enemy);
            }
        }
    }
    public void removeBarriers(int height){
        for(Object barrier : getBarriers()){
            if(barrier.getY() > height){
                removeFromBarriers(barrier);
            }
        }
    }
    public void removeFuelTanks(int height){
        for(Object fuelTank : getFuelTanks()){
            if(fuelTank.getY() > height){
                removeFromFuelTanks(fuelTank);
            }
        }
    }
    public void removePolice(int height){
        for(PoliceCar police : getPolice()){
            if(police.getY() > 2*height || police.getY() < -police.getHeight()){
                removeFromPolice(police);
            }
        }
    }

    public void move(){ //move all obstacles
        moveEnemys();
        moveBarriers();
        moveFuelTanks();
        movePolice();
    }
    public void moveEnemys(){ //move EnemyCar objects
        for(EnemyCar enemy : getEnemys()){
            enemy.update();
        }
    }
    public void moveBarriers(){ //move Barrier objects
        for(Object barrier : getBarriers()){
            barrier.moveVertical(streetManager.speed());
        }
    }
    public void moveFuelTanks(){ //move fueltank objects
        for(Object fuelTank : getFuelTanks()){
            fuelTank.moveVertical(streetManager.speed());
        }
    }
    public void movePolice(){
        for(PoliceCar police : getPolice()){
            police.update();
        }
    }

    public void draw(Graphics2D g2d){ //draw all obstacles
        drawFuelTanks(g2d);
        drawEnemys(g2d);
        drawBarriers(g2d);
        drawPolice(g2d);
    }
    public void drawEnemys(Graphics2D g2d){ //draw enemys
        for(EnemyCar enemy : getEnemys()){
            g2d.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), null);
        }
    }
    public void drawBarriers(Graphics2D g2d){ //draw barriers
        for(Object barrier : getBarriers()){
            g2d.drawImage(barrier.getImage(), barrier.getX(), barrier.getY(), null);
        }
    }
    public void drawFuelTanks(Graphics2D g2d){ //draw fueltanks
        for(Object fuelTank : getFuelTanks()){
            g2d.drawImage(fuelTank.getImage(), fuelTank.getX(), fuelTank.getY(), null);
        }
    }
    public void drawPolice(Graphics2D g2d){
        for(PoliceCar police : getPolice()){
            g2d.drawImage(police.getImage(), police.getX(), police.getY(), null);
        }
    }

    public ArrayList<Object> getBarriers(){
        synchronized (barriers){
            ArrayList barriers = new ArrayList(this.barriers);
            return barriers;
        }
    }
    public void setBarriers(ArrayList<Object> barriers){
        this.barriers = barriers;
    }
    public void addToBarriers(Object barrier){
        synchronized (barriers){
            barriers.add(barrier);
        }
    }
    public void removeFromBarriers(Object barrier){
        synchronized (barriers){
            barriers.remove(barrier);
        }
    }
    public Object getBarrier(int index){
        synchronized (barriers){
            return barriers.get(index);
        }
    }

    public ArrayList<EnemyCar> getEnemys(){
        synchronized (enemys){
            ArrayList enemys = new ArrayList(this.enemys);
            return enemys;
        }
    }
    public void setEnemys(ArrayList<EnemyCar> enemys){
        this.enemys = enemys;
    }
    public void addToEnemys(EnemyCar enemy){
        synchronized (enemys){
            enemys.add(enemy);
        }
    }
    public void removeFromEnemys(EnemyCar enemy){
        synchronized (enemys){
            enemys.remove(enemy);
        }
    }
    public EnemyCar getEnemy(int index){
        synchronized (enemys){
            return enemys.get(index);
        }
    }

    public ArrayList<PoliceCar> getPolice(){
        synchronized (police){
            ArrayList police = new ArrayList(this.police);
            return police;
        }
    }
    public void setPolice(ArrayList<PoliceCar> police){
        this.police = police;
    }
    public void addToPolice(PoliceCar police){
        synchronized (this.police){
            this.police.add(police);
        }
    }
    public void removeFromPolice(PoliceCar police){
        synchronized (police){
            this.police.remove(police);
        }
    }

    public ArrayList<Object> getFuelTanks(){
        synchronized (fuelTanks){
            ArrayList fuelTanks = new ArrayList(this.fuelTanks);
            return fuelTanks;
        }
    }
    public void setFuelTanks(ArrayList<Object> fuelTanks){
        this.fuelTanks = fuelTanks;
    }
    public void addToFuelTanks(Object fuelTank){
        synchronized (fuelTanks){
            fuelTanks.add(fuelTank);
        }
    }
    public void removeFromFuelTanks(Object fuelTank){
        synchronized (fuelTanks){
            fuelTanks.remove(fuelTank);
        }
    }
    public Object getFuelTank(int index){
        synchronized (fuelTanks){
            return fuelTanks.get(index);
        }
    }
}
