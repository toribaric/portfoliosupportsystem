package org.nnga.tsp.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "Share")
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
    @Column(name = "Symbol")
    private String symbol;
    @Column(name = "Description")
    private String description;
    @Column(name = "Sector")
    private String sector;

    public Share() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

}
