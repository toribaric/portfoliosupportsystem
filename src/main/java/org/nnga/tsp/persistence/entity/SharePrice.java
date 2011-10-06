package org.nnga.tsp.persistence.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SharePrice")
public class SharePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
    @Column(name = "Date")
    private Date date;
    @Column(name = "Price")
    private double price;
    @Column(name = "ShareId")
    private int shareId;

    public SharePrice() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

}