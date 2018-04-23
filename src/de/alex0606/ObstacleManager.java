package de.alex0606;

import de.alex0606.objects.Object;
import de.alex0606.objects.cars.EnemyCar;
import de.alex0606.objects.cars.PlayerCar;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Thread;

public class ObstacleManager implements Runnable{
    private EnemyCar sampleEnemey; //EnemyCar to get dimensions

    private ArrayList<EnemyCar> enemys; //holds all enemys
    private ArrayList<Object> barriers; //holds all barriers
    private ArrayList<Object> fuelTanks; //holds all fuel tanks
    private StreetManager streetManager; //holds reference to streetManager

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    private double nextEnemy = 0; //time until next enemy spawns
    public double nextEnemyMin = 0; // in milliseconds, time per lane
    public double nextEnemyMax = 25000; // in milliseconds, time per lane
    private double enemyTime = 0; //time since last enemy spawn

    private int lastTrack = -1; //last track that was a obstacle
    private int freeTrack = -1; //track that is free for an amount of time
    private double changeFreeTrack = 2000; //time until freeTrack changes
    private double freeTrackTime = 0; //time since freeTrack was changed

    private double timeStart = 0; //caches current time to calculate the time since last gameloop repeat

    private double changeTrackTime = 0; //time since last lane change
    private double changeTrackTimeMin = 8000; //minimum time until next lane change
    private double changeTrackTimeMax = 20000; //minimum time untli next lane change
    private double changeTrack = changeTrackTimeMax; //time until EnemyCar changes track

    private double newFuelTankDistanceMin = PlayerCar.getFuelMaxDistance() * 0.2; //minimal distance until fueltank is generated
    private double newFuelTankDistance = newFuelTankDistanceMin; //distance until new fueltank is generated
    private double newFuelTankDistanceMax = PlayerCar.getFuelMaxDistance() * 0.95; //maximum distance until fueltank is generated
    private double newFuelTank = 0; //distance since fueltank was generated
    private double newFuelTankDistanceAdditive = PlayerCar.getFuelMaxDistance() * 0.02; //distance, added to the distance after which a new fueltank gets generated, after every new fueltank

    public Thread thread = new Thread(this); //thread in which hitbox intersection of obstacles under each other gets checked
    private int threadSpeed = 200; //delay of the thread

    public ObstacleManager(StreetManager streetManager){
        init(streetManager, new ArrayList<EnemyCar>(), new ArrayList<Object>()); //new ObstacleManager with no enemys and barriers
    }
    public ObstacleManager(StreetManager streetManager, ArrayList<EnemyCar> enemys, ArrayList<Object> barriers){
        init(streetManager, enemys, barriers); //new ObstacleManager with given enemys and barriers
    }
    private void init(StreetManager streetManager, ArrayList<EnemyCar> enemys, ArrayList<Object> barriers){ //initialize params with given values, called by constructors
        setEnemys(enemys);
        setBarriers(barriers);
        this.streetManager = streetManager;
        sampleEnemey = new EnemyCar(0, 0, streetManager);
        setFuelTanks(new ArrayList<>());
        timeStart = System.currentTimeMillis(); //reset time
    }

    @Override
    public void run() {
        while (true) { //run as long as thread is not interrupted and ObstacleManager is alive
            try {
                checkEnemeyEnemyCollision(); //checks if enemys collide under each other
                checkEnemyBarrierCollision(); //checks if enemys collide with barriers

                Thread.sleep(threadSpeed); //sleeps, for delay
            } catch (InterruptedException e) {

                String msg = String.format("Thread interrupted: %s", e.getMessage());
                System.out.println(msg);
            }
        }
    }

    public void generateObstacles() {
        //increase times since last repeat
        freeTrackTime += (System.currentTimeMillis() - timeStart);
        enemyTime += (System.currentTimeMillis() - timeStart);
        changeTrackTime += (System.currentTimeMillis() - timeStart);
        timeStart = System.currentTimeMillis();

        if(freeTrackTime >= changeFreeTrack){
            freeTrack = random.nextInt(0, streetManager.getHorizontalStreetCount());
            freeTrackTime = 0;
        }

        if(enemyTime >= nextEnemy){
            addNewEnemy();
            enemyTime = 0;
            nextEnemy = random.nextDouble(nextEnemyMin / (streetManager.getHorizontalStreetCount() * 1.5),
                    nextEnemyMax / (streetManager.getHorizontalStreetCount() * 1.5));
        }

        if(changeTrackTime >= changeTrack && enemys.size() > 0){
            EnemyCar enemy = getEnemy(random.nextInt(getEnemys().size()));
            double speed = random.nextDouble(1, 3);
            int track = enemy.getNewRandomTrack();
            enemy.changeTrack(track, speed);
            changeTrackTime = 0;
            changeTrack = random.nextDouble(changeTrackTimeMin, changeTrackTimeMax);
        }
    }

    public void addNewFuelTank(){
        Object fuelTank = new Object(0, 0, "res/fueltank.png");
        //int track = random.nextInt(0, streetManager.getHorizontalStreetCount());
        int track = random.nextInt(0, streetManager.getHorizontalStreetCount());
        double x = streetManager.getXAdditive() + StreetManager.getSampleStreet().getWidth() * track +
                (0.5 * streetManager.getSampleStreet().getWidth() - 0.5 * fuelTank.getWidth());
        double y = - (fuelTank.getHeight() + 200);
        fuelTank.moveTo(x, y);
        addToFuelTanks(fuelTank);
    }

    public void addNewEnemy(){
        EnemyCar enemy = getNewEnemy();
        if(!checkObstacleCollision(enemy)){
            addToEnemys(enemy);
        }
    }
    public EnemyCar getNewEnemy(){
        int track = lastTrack;
        if(streetManager.getHorizontalStreetCount() > 2){
            while(track == lastTrack || track == freeTrack){
                track = random.nextInt(0, streetManager.getHorizontalStreetCount());
            }
        }
        lastTrack = track;

        int xAdditive = random.nextInt(1,StreetManager.getSampleStreet().getWidth() - sampleEnemey.getWidth() - 1);
        int x = streetManager.getXAdditive() + StreetManager.getSampleStreet().getWidth() * track + xAdditive;

        int y = -sampleEnemey.getHeight()-100;
        double horizontalSpeed = 0;
        double verticalSpeed = random.nextDouble(-4, -2);

        EnemyCar enemy = new EnemyCar(x, y, streetManager, horizontalSpeed, verticalSpeed, track);
        return enemy;
    }

    public void update(int height){
        move();
        generateObstacles();
        remove(height);

        newFuelTank += streetManager.getSpeed(); //add speed of street to distance, until new fueltank is generated
        if(newFuelTank >= newFuelTankDistance / ((int)(streetManager.getHorizontalStreetCount() * 0.1) + 1)){ //if distance since last fueltank is equal or higher as given distance
            if(newFuelTankDistance + newFuelTankDistanceAdditive < newFuelTankDistanceMax){
                newFuelTankDistance += newFuelTankDistanceAdditive; //increase distance until new fueltank
            }
            newFuelTank = 0; //set distance since last fueltank to 0
            addNewFuelTank(); //generate new fueltank
        }
    }

    public boolean checkObstacleCollision(Object object){
        if(object instanceof PlayerCar){
            checkFuelTankCollision((PlayerCar) object);
        }
        if(checkEnemyCollision(object) == true || checkBarrierCollision(object) == true)
            return true;

        return false;
    }

    public boolean checkEnemyCollision(Object object){
        for (EnemyCar enemyCar : getEnemys()) {
            if(object.checkHitboxIntersection(enemyCar) == true){
                return true;
            }
        }

        return false;
    }
    public boolean checkBarrierCollision(Object object){
        for(Object barrier : getBarriers()){
            if(object.checkHitboxIntersection(barrier))
                return true;
        }
        return false;
    }
    public boolean checkFuelTankCollision(PlayerCar playerCar){
        for(Object fuelTank : getFuelTanks()){
            if(playerCar.checkHitboxIntersection(fuelTank)){
                playerCar.fillFuel();
                removeFromFuelTanks(fuelTank);
                return true;
            }
        }
        return false;
    }

    public void checkEnemyBarrierCollision(){
        for(Object barrier : getBarriers()){
            for(EnemyCar enemyCar : getEnemys()){
                if(enemyCar.checkHitboxIntersection(barrier) == true){
                    enemyCar.setSpeed(0, 0);
                }
            }
        }
    }
    public void checkEnemeyEnemyCollision(){
        ArrayList<EnemyCar> enemysCache = getEnemys();
        for(EnemyCar enemy1: getEnemys()) {
            enemysCache.remove(enemy1);

            for (EnemyCar enemy2 : enemysCache) {
                if (enemy1.checkHitboxIntersection(enemy2)) {
                    enemy1.setSpeed(0, 0);
                    enemy2.setSpeed(0, 0);
                }
            }
        }
    }

    public void remove(int height){
        removeEnemys(height);
        removeBarriers(height);
        removeFuelTanks(height);
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

    public void move(){
        moveEnemys();
        moveBarriers();
        moveFuelTanks();
    }
    public void moveEnemys(){
        for(EnemyCar enemy : getEnemys()){
            enemy.update();
        }
    }
    public void moveBarriers(){
        for(Object barrier : getBarriers()){
            barrier.moveVertical(streetManager.getSpeed());
        }
    }
    public void moveFuelTanks(){
        for(Object fuelTank : getFuelTanks()){
            fuelTank.moveVertical(streetManager.getSpeed());
        }
    }

    public void draw(Graphics2D g2d, JPanel observer){
        drawFuelTanks(g2d, observer);
        drawEnemys(g2d, observer);
        drawBarriers(g2d, observer);
    }
    public void drawEnemys(Graphics2D g2d, JPanel observer){
        for(EnemyCar enemy : getEnemys()){
            g2d.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), observer);
        }
    }
    public void drawBarriers(Graphics2D g2d, JPanel observer){
        for(Object barrier : getBarriers()){
            g2d.drawImage(barrier.getImage(), barrier.getX(), barrier.getY(), observer);
        }
    }
    public void drawFuelTanks(Graphics2D g2d, JPanel observer){
        for(Object fuelTank : getFuelTanks()){
            g2d.drawImage(fuelTank.getImage(), fuelTank.getX(), fuelTank.getY(), observer);
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

    public void addObstacle(EnemyCar enemyCar){
        enemys.add(enemyCar);
    }
    public void addObstacle(Object barrier){
        barriers.add(barrier);
    }
}
