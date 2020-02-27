package org.acme;

import java.util.List;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Person extends PanacheEntity {
    public String name;
    public String surname;
    public int age;

    public Person(){}

    public Person(String name, String surname, int age){
        this.name = name;
        this.surname = surname;
        this.age = age;
    }
    public static Person findByName(String name){
        return find("name", name).firstResult();
    }
    
    public static void remove(String name){
        delete("name", name);
    }
}