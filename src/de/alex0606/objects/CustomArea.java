package de.alex0606.objects;

import java.awt.geom.Area;
import java.io.Serializable;

public class CustomArea implements Serializable {
    private static final long serialVersionUID = 1L;
    private Area area;

    public CustomArea(Area area){
        this.area = area;
    }
    public Area getArea(){
        return area;
    }
}