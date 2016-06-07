/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mahi.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author techadmin
 */
@Entity
public class StonyImgMain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StonyImgCat> imgCatList = new ArrayList<>();

    private String name;
    private String description;

    private boolean startImgMain;
    private String startHexRGB;

    private boolean hiddenGallery;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestmp;

    public StonyImgMain() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<StonyImgCat> getImgCatList() {
        return imgCatList;
    }

    public void setImgCatList(List<StonyImgCat> imgCatList) {
        this.imgCatList = imgCatList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStartImgMain() {
        return startImgMain;
    }

    public void setStartImgMain(boolean startImgMain) {
        this.startImgMain = startImgMain;
    }

    public String getStartHexRGB() {
        return startHexRGB;
    }

    public void setStartHexRGB(String startHexRGB) {
        this.startHexRGB = startHexRGB;
    }

    public boolean isHiddenGallery() {
        return hiddenGallery;
    }

    public void setHiddenGallery(boolean hiddenGallery) {
        this.hiddenGallery = hiddenGallery;
    }

    public Date getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(Date timestmp) {
        this.timestmp = timestmp;
    }

}
