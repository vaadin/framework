package com.vaadin.tests.components.grid.basics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DataObject {

    private static final int ROWS = 1000;

    private Integer rowNumber;
    private String coordinates;
    private String htmlString;
    private Integer smallRandom;
    private Integer bigRandom;
    private Date date;

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getHtmlString() {
        return htmlString;
    }

    public void setHtmlString(String htmlString) {
        this.htmlString = htmlString;
    }

    public Integer getSmallRandom() {
        return smallRandom;
    }

    public void setSmallRandom(Integer smallRandom) {
        this.smallRandom = smallRandom;
    }

    public Integer getBigRandom() {
        return bigRandom;
    }

    public void setBigRandom(Integer bigRandom) {
        this.bigRandom = bigRandom;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DataObject[" + rowNumber + "]";
    }

    public static List<DataObject> generateObjects() {
        List<DataObject> data = new ArrayList<>();

        {
            Random rand = new Random();
            rand.setSeed(13334);
            long timestamp = 0;
            for (int row = 0; row < ROWS; row++) {
                DataObject obj = new DataObject();
                obj.setRowNumber(row);
                obj.setCoordinates("(" + row + ", " + 0 + ")");
                obj.setHtmlString("<b>" + row + "</b>");
                // Random numbers
                obj.setBigRandom(rand.nextInt());
                obj.setSmallRandom(rand.nextInt(5));

                obj.setDate(new Date(timestamp));
                timestamp += 91250000; // a bit over a day, just to get
                                       // variation

                data.add(obj);
            }
        }
        return data;
    }

}
