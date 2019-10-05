package com.proj.agnus.comun;

import java.io.Serializable;

/**
 * Created by Arturo on 3/9/2018.
 */

public class Person implements Serializable {
    private String id;
    private String name;

    public Person(String n, String id) {
        this.id = id;
        name = n;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    @Override
    public String toString() { return name; }
}
