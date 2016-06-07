/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mahi.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.mahi.persistence.Stats;
import org.mahi.persistence.StonyArtMain;
import org.mahi.persistence.StonyImages;
import org.mahi.persistence.StonyImgCat;
import org.mahi.persistence.StonyImgMain;

/**
 *
 * @author mahi
 */
@Stateless
public class ActionImpl implements ActionDAO {

    @PersistenceContext(name = "objectDB")
    private EntityManager em;

    @Override
    public void save(Object obj) {
        em.persist(obj);
    }

    @Override
    public void update(Object obj) {
        em.merge(obj);
    }

    @Override
    public void persist(Object obj) {
        em.persist(obj);
    }

    @Override
    public void remove(Object obj) {
        em.remove(em.merge(obj));
    }

    @Override
    public List<StonyArtMain> getArtMain() {

        TypedQuery<StonyArtMain> query = em.createQuery("Select q from StonyArtMain q Order by q.name", StonyArtMain.class);
        List<StonyArtMain> list = query.getResultList();

        return list;
    }

    @Override
    public List<StonyImgMain> getImgMainNotHidden() {

        TypedQuery<StonyImgMain> query = em.createQuery("Select q from StonyImgMain q where q.hiddenGallery = false Order by q.name", StonyImgMain.class);
        List<StonyImgMain> list = query.getResultList();

        return list;
    }

    @Override
    public List<StonyImgMain> getImgMainAll() {

        TypedQuery<StonyImgMain> query = em.createQuery("Select q from StonyImgMain q Order by q.name", StonyImgMain.class);
        List<StonyImgMain> list = query.getResultList();

        return list;
    }

    @Override
    public List<Object> list(Class clazz) {
        TypedQuery<Object> query = em.createQuery(
                " SELECT q FROM " + clazz.getName() + " q ", clazz);
        return query.getResultList();
    }

    @Override
    public StonyImages findImageById(Long valueOf) {
        return em.find(StonyImages.class, valueOf);
    }

    @Override
    public StonyImgCat findImgCatById(Long valueOf) {
        return em.find(StonyImgCat.class, valueOf);
    }

    @Override
    public StonyImgMain findImgMainById(Long valueOf) {
        return em.find(StonyImgMain.class, valueOf);
    }

    @Override
    public StonyArtMain findArtMainById(Long valueOf) {
        return em.find(StonyArtMain.class, valueOf);
    }

    @Override
    public long cntVisitor() {
        TypedQuery<Object> query = null;
        String JPQLQuery = "SELECT count(q) FROM Stats q WHERE q.bot = false";
        query = em.createQuery(JPQLQuery, Object.class);

        return (long) query.getSingleResult();
    }
    
    @Override
    public long cntVisitorAll() {
        TypedQuery<Object> query = null;
        String JPQLQuery = "SELECT count(q) FROM Stats q";
        query = em.createQuery(JPQLQuery, Object.class);

        return (long) query.getSingleResult();
    }

    @Override
    public List<Stats> lastTenVisitor() {
        TypedQuery<Stats> query = em.createQuery(
                " SELECT q FROM Stats q", Stats.class).setMaxResults(10);
        
        return query.getResultList();
    }
    
    @Override
    public void saveVisitor(Stats stats){
        em.persist(stats);
    }
}
