package com.driver.model;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ServiceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setConnectionList(List<Connection> connectionList) {
        this.connectionList = connectionList;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Admin getAdmin() {
        return admin;
    }

    public List<Country> getCountryList() {
        return countryList;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Connection> getConnectionList() {
        return connectionList;
    }

    public ServiceProvider(int id, String name, Admin admin, List<Country> countryList, List<User> users, List<Connection> connectionList) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.countryList = countryList;
        this.users = users;
        this.connectionList = connectionList;
    }

    public ServiceProvider() {
    }

    @ManyToOne
    @JoinColumn
    private Admin admin;

    @OneToMany(mappedBy = "serviceProvider",cascade = CascadeType.ALL)
    private List<Country> countryList=new ArrayList<>();

    @ManyToMany(mappedBy = "serviceProviderList",cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();


    @OneToMany(mappedBy = "serviceProvider",cascade = CascadeType.ALL)
    private List<Connection> connectionList = new ArrayList<>();




}
