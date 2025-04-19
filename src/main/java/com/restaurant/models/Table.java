package com.restaurant.models;

public class Table {
    private int id;
    private int number;
    private int capacity;
    private int x;          // column
    private int y;          // row
    private boolean available;

    public Table() {}

    public Table(int id,int number,int capacity,int x,int y,boolean available){
        this.id = id;
        this.number = number;
        this.capacity = capacity;
        this.x = x;
        this.y = y;
        this.available = available;
    }

    public int  getId()              { return id; }
    public void setId(int id)        { this.id = id; }

    public int  getNumber()              { return number; }
    public void setNumber(int number)    { this.number = number; }

    public int  getCapacity()            { return capacity; }
    public void setCapacity(int capacity){ this.capacity = capacity; }

    public int  getX()              { return x; }
    public void setX(int x)         { this.x = x; }

    public int  getY()              { return y; }
    public void setY(int y)         { this.y = y; }

    public boolean isAvailable()                 { return available; }
    public void    setAvailable(boolean avail)   { this.available = avail; }
}