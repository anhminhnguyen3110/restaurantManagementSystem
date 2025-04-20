package com.restaurant.daos.impl;

import com.restaurant.daos.MenuDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Menu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Injectable
public class MenuDAOImpl implements MenuDAO {
    @Inject
    private EntityManagerFactory emf;

    public MenuDAOImpl() {
    }

    public MenuDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(Menu menu) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(menu);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Menu getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Menu.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Menu> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Menu> q = em.createQuery("SELECT m FROM Menu m", Menu.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Menu> findByRestaurant(int restaurantId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Menu> q = em.createQuery(
                    "SELECT m FROM Menu m WHERE m.restaurant.id = :rid",
                    Menu.class
            );
            q.setParameter("rid", restaurantId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Menu menu) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(menu);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Menu m = em.find(Menu.class, id);
            if (m != null) em.remove(m);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}