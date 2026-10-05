package com.moviebooking.dto;

public class TheaterDto {
    private Long id;
    private String name;
    private String city;
    private String location;
    private String screenType;
    private Integer totalRows;
    private Integer totalCols;
    private Integer totalSeats;

    public TheaterDto() {}

    public TheaterDto(Long id, String name, String city, String location, String screenType,
                      Integer totalRows, Integer totalCols, Integer totalSeats) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.location = location;
        this.screenType = screenType;
        this.totalRows = totalRows;
        this.totalCols = totalCols;
        this.totalSeats = totalSeats;
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

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
}
