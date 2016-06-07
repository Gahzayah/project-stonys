/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mahi.manager;

import java.util.List;
import org.mahi.persistence.Stats;
import org.mahi.persistence.StonyArtMain;
import org.mahi.persistence.StonyImages;
import org.mahi.persistence.StonyImgCat;
import org.mahi.persistence.StonyImgMain;

/**
 *
 * @author mahi
 */
public interface ActionDAO {

    public void persist(Object obj);

    public void save(Object obj);

    public void update(Object obj);

    public void remove(Object obj);

    public StonyImgMain findImgMainById(Long valueOf);

    public StonyArtMain findArtMainById(Long valueOf);

    public List<Object> list(Class clazz);

    public List<StonyArtMain> getArtMain();

    public List<StonyImgMain> getImgMainNotHidden();

    public List<StonyImgMain> getImgMainAll();

    public StonyImages findImageById(Long valueOf);

    public StonyImgCat findImgCatById(Long valueOf);

    public long cntVisitor();
    
    public long cntVisitorAll();
    
    public List<Stats> lastTenVisitor();

    public void saveVisitor(Stats stats);

}
