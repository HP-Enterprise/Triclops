package com.hp.triclops;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity @Table(name="t_account")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String name;
    private String nick;

    protected User() {}

    public User(String name, String nick) {
        this.name = name;
        this.nick = nick;
    }

    public String getName(){
        return this.name;
    }

    public String getNick(){
        return this.nick;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, name='%s', nick='%s']",
                id, name, nick);
    }

}