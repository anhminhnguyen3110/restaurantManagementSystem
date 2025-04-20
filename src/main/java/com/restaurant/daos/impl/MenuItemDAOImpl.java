package com.restaurant.daos.impl;

import com.restaurant.daos.MenuItemDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.MenuItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Injectable
public class MenuItemDAOImpl implements MenuItemDAO {
    @Inject
    private EntityManagerFactory emf;

    public MenuItemDAOImpl() {
    }

    public MenuItemDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(MenuItem item) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(item);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public MenuItem getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(MenuItem.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<MenuItem> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<MenuItem> q = em.createQuery("SELECT m FROM MenuItem m", MenuItem.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public MenuItem findByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<MenuItem> q = em.createQuery(
                    "SELECT m FROM MenuItem m WHERE m.name = :name",
                    MenuItem.class
            ).setParameter("name", name).setMaxResults(1);
            return q.getResultList().stream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public List<MenuItem> findByMenu(int menuId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<MenuItem> q = em.createQuery(
                    "SELECT m FROM MenuItem m WHERE m.menu.id = :menuId",
                    MenuItem.class
            );
            q.setParameter("menuId", menuId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(MenuItem item) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(item);
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
            MenuItem m = em.find(MenuItem.class, id);
            if (m != null) em.remove(m);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}
