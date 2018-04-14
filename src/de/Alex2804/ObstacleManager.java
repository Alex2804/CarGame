package de.Alex2804;

import de.Alex2804.objects.Object;
import de.Alex2804.objects.cars.EnemyCar;
import de.Alex2804.objects.cars.PlayerCar;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Thread;

public class ObstacleManager implements Runnable{
    private EnemyCar sampleEnemey;

    private ArrayList<EnemyCar> enemys;
    private ArrayList<Object> barriers;
    private ArrayList<Object> fuelTanks;
    private StreetManager streetManager;

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    private double nextEnemy = 0;
    private double nextEnemyMin = 0; // in milliseconds
    private double nextEnemyMax = 25000;
    private double enemyTime = 0;
    private double enemyTimeStart = 0;

    private int lastTrack = -1;
    private int freeTrack = -1;
    private double changeFreeTrack = 2000;
    private double freeTrackTime = 0;
    private double freeTrackTimeStart = 0;

    private double changeTrackTime = 0;
    private double changeTrackTimeStart = 0;
    private double changeTrackTimeMin = 8000;
    private double changeTrackTimeMax = 20000;
    private double changeTrack = changeTrackTimeMax;

    private double newFuelTankDistanceMin = PlayerCar.getFuelMaxDistance() * 0.2;
    private double newFuelTankDistance = newFuelTankDistanceMin;
    private double newFuelTankDistanceMax = PlayerCar.getFuelMaxDistance() * 0.95;
    private double newFuelTank = 0;
    private double newFuelTankDistanceAdditive = PlayerCar.getFuelMaxDistance() * 0.01;

    private Thread thread = new Thread(this);
    private int threadSpeed = 100;

    public ObstacleManager(StreetManager streetManager){
        init(streetManager, new ArrayList<EnemyCar>(), new ArrayList<Object>());
    }
    public ObstacleManager(StreetManager streetManager, ArrayList<EnemyCar> enemys, ArrayList<Object> barriers){
        init(streetManager, enemys, barriers);
    }
    private void init(StreetManager streetManager, ArrayList<EnemyCar> enemys, ArrayList<Object> barriers){
        setEnemys(enemys);
        setBarriers(barriers);
        this.streetManager = streetManager;
        sampleEnemey = new EnemyCar(0, 0, streetManager);
        setFuelTanks(new ArrayList<>());
        changeTrackTimeStart = System.currentTimeMillis();
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            checkEnemeyEnemyCollision();
            checkEnemyBarrierCollision();

            try {
                Thread.sleep(threadSpeed);
            } catch (InterruptedException e) {

                String msg = String.format("Thread interrupted: %s", e.getMessage());

                System.out.println(msg);
            }
        }
    }

    public void generateObstacles() {
        newFuelTank += streetManager.getSpeed();
        if(newFuelTank >= newFuelTankDistance){
            if(newFuelTankDistance + newFuelTankDistanceAdditive < newFuelTankDistanceMax){
                newFuelTankDistance += newFuelTankDistanceAdditive;
            }
            newFuelTank = 0;
            addNewFuelTank();
        }

        freeTrackTime += (System.currentTimeMillis() - freeTrackTimeStart);
        freeTrackTimeStart = System.currentTimeMillis();
        if(freeTrackTime >= changeFreeTrack){
            freeTrack = random.nextInt(0, streetManager.getHorizontalStreetCount());
            freeTrackTime = 0;
        }

        enemyTime += (System.currentTimeMillis() - enemyTimeStart);
        enemyTimeStart = System.currentTimeMillis();
        if(enemyTime >= nextEnemy){
            addNewEnemy();
            enemyTime = 0;
            nextEnemy = random.nextDouble(nextEnemyMin / (streetManager.getHorizontalStreetCount() * 1.5),
                    nextEnemyMax / (streetManager.getHorizontalStreetCount() * 1.5));
        }

        changeTrackTime += System.currentTimeMillis() - changeTrackTimeStart;
        changeTrackTimeStart = System.currentTimeMillis();
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
        int track = freeTrack >= 0 ? freeTrack : random.nextInt(0, streetManager.getHorizontalStreetCount());
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

        int x = streetManager.getXAdditive() + StreetManager.getSampleStreet().getWidth() * track +
                random.nextInt(2,
                        StreetManager.getSampleStreet().getWidth() - sampleEnemey.getWidth() - 1);

        int y = -200;
        double horizontalSpeed = 0;
        double verticalSpeed = random.nextDouble(-streetManager.getSpeed() + 1, -streetManager.getSpeed() + 3);

        EnemyCar enemy = new EnemyCar(x, y, streetManager, horizontalSpeed, verticalSpeed, track);
        return enemy;
    }

    public void update(int height){
        move();
        generateObstacles();
        remove(height);
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
