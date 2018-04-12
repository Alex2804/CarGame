package de.Alex2804;

import de.Alex2804.objects.Object;
import de.Alex2804.objects.Street;
import de.Alex2804.objects.cars.Car;
import de.Alex2804.objects.cars.EnemyCar;
import de.Alex2804.objects.cars.PlayerCar;
import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ObstacleManager implements Runnable{
    private EnemyCar sampleEnemey;

    private ArrayList<EnemyCar> enemys;
    private ArrayList<Object> barriers;
    private ArrayList<Object> fuelTanks;
    private StreetManager streetManager;

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    private int timerSpeed = 100;

    private double nextEnemy = 0;
    private double nextEnemyMin = 0000; // in milliseconds
    private double nextEnemyMax = 25000;
    private double enemyTime = 0;
    private double enemyTimeStart = 0;

    int lastTrack = -1;
    int freeTrack = -1;
    double changeFreeTrack = 2000;
    double freeTrackTime = 0;
    double freeTrackTimeStart = 0;

    double changeTrackTime = 0;
    double changeTrackTimeStart = 0;
    double changeTrackTimeMin = 8000;
    double changeTrackTimeMax = 20000;
    double changeTrack = changeTrackTimeMax;

    double newFuelTimeDistanceMin = PlayerCar.getMaxDistance() * 0.20;
    double newFuelTankDistance = newFuelTimeDistanceMin;
    double newFuelTimeDistanceMax = PlayerCar.getMaxDistance() - (PlayerCar.getMaxDistance() * 0.05);
    double newFuelTank = newFuelTimeDistanceMin;
    double newFuelTankDistanceAdditive = PlayerCar.getMaxDistance() * 0.001;

    Thread thread = new Thread(this);

    public ObstacleManager(StreetManager streetManager){
        init(streetManager, new ArrayList<>(), new ArrayList<>());
    }
    public ObstacleManager(StreetManager streetManager, ArrayList<EnemyCar> enemys, ArrayList<Object> barriers){
        init(streetManager, enemys, barriers);
    }
    private void init(StreetManager streetManager, ArrayList<EnemyCar> enemys, ArrayList<Object> barriers){
        this.enemys = enemys;
        this.barriers = barriers;
        this.streetManager = streetManager;
        sampleEnemey = new EnemyCar(0, 0, streetManager);
        fuelTanks = new ArrayList<>();
        changeTrackTimeStart = System.currentTimeMillis();
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            checkEnemeyEnemyCollision();
            checkEnemyBarrierCollision();

            try {
                Thread.sleep(timerSpeed);
            } catch (InterruptedException e) {

                String msg = String.format("Thread interrupted: %s", e.getMessage());

                System.out.println(msg);
            }
        }
    }

    public void test() {
        newFuelTank += streetManager.getSpeed();
        if(newFuelTank >= newFuelTankDistance){
            if(newFuelTankDistance + newFuelTankDistanceAdditive < newFuelTimeDistanceMax){
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
        int track = random.nextInt(0, streetManager.getHorizontalStreetCount());
        double x = streetManager.getXAdditive() + StreetManager.getSampleStreet().getWidth() * track +
                (0.5 * streetManager.getSampleStreet().getWidth() - 0.5 * fuelTank.getWidth());
        double y = - (fuelTank.getHeight() + 200);
        fuelTank.moveTo(x, y);
        addToFuelTanks(fuelTank);
    }

    public void addNewEnemy(){
        EnemyCar enemy = getNewEnemy();
        if(!checkCollisions(enemy)){
            addToEnemys(enemy);
        }
    }

    public EnemyCar getNewEnemy(){
        int track = lastTrack;
        if(streetManager.getHorizontalStreetCount() > 1){
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
        removeObstacles(height);
        move();
    }

    public boolean checkCollisions(Object object){
        return checkObstacleCollision(object);
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

    public void removeObstacles(int height){
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
        synchronized (this.barriers){
            this.barriers = barriers;
        }
    }
    public void addToBarriers(Object barrier){
        synchronized (barriers){
            barriers.add(barrier);
        }
    }
    public void removeFromBarriers(Object barrier){
        synchronized (barriers){
            barriers.add(barrier);
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
        synchronized (this.enemys){
            this.enemys = enemys;
        }
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
        synchronized (this.fuelTanks){
            this.fuelTanks = fuelTanks;
        }
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
