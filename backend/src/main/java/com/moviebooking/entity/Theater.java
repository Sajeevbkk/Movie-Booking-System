package com.moviebooking.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theaters")
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 200)
    private String location;

    @Column(length = 50)
    private String screenType; // e.g. "IMAX Laser 3D", "Dolby Atmos 4K"

    @Column(nullable = false)
    private Integer totalRows = 8;

    @Column(nullable = false)
    private Integer totalCols = 10;

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    public Theater() {}

    public Theater(String name, String city, String location, String screenType, Integer totalRows, Integer totalCols) {
        this.name = name;
        this.city = city;
        this.location = location;
        this.screenType = screenType;
        this.totalRows = totalRows;
        this.totalCols = totalCols;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getScreenType() { return screenType; }
    public void setScreenType(String screenType) { this.screenType = screenType; }

    public Integer getTotalRows() { return totalRows; }
    public void setTotalRows(Integer totalRows) { this.totalRows = totalRows; }

    public Integer getTotalCols() { return totalCols; }
    public void setTotalCols(Integer totalCols) { this.totalCols = totalCols; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
}
