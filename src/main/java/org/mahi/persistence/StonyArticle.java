/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mahi.persistence;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author MaHi
 */
@Entity
public class StonyArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private StonyArtMain artMain;

    private String titel;
    @Column(columnDefinition = "TEXT")
    private String text;
    private boolean onlyStartPage;
    private boolean startArticle;
    private String startHexRGB;

    @Temporal(TemporalType.DATE)
    private Date startTime;
    @Temporal(TemporalType.DATE)
    private Date endTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestmp;

    public StonyArticle() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StonyArtMain getArtMain() {
        return artMain;
    }

    public void setArtMain(StonyArtMain artMain) {
        this.artMain = artMain;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isOnlyStartPage() {
        return onlyStartPage;
    }

    public void setOnlyStartPage(boolean onlyStartPage) {
        this.onlyStartPage = onlyStartPage;
    }

    public boolean isStartArticle() {
        return startArticle;
    }

    public void setStartArticle(boolean startArticle) {
        this.startArticle = startArticle;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(Date timestmp) {
        this.timestmp = timestmp;
    }

    public String getStartHexRGB() {
        return startHexRGB;
    }

    public void setStartHexRGB(String startHexRGB) {
        this.startHexRGB = startHexRGB;
    }

}
