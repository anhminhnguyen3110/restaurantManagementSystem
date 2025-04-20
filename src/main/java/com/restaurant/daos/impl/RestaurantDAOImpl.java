package com.restaurant.daos.impl;

import com.restaurant.daos.RestaurantDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Restaurant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Injectable
public class RestaurantDAOImpl implements RestaurantDAO {
    @Inject
    private EntityManagerFactory emf;

    public RestaurantDAOImpl() {}

    public RestaurantDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(Restaurant restaurant) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(restaurant);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Restaurant getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Restaurant.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Restaurant> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Restaurant> q = em.createQuery("SELECT r FROM Restaurant r", Restaurant.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Restaurant findByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Restaurant> q = em.createQuery(
                    "SELECT r FROM Restaurant r WHERE r.name = :name",
                    Restaurant.class
            ).setParameter("name", name).setMaxResults(1);
            return q.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Restaurant> findByAddress(String address) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Restaurant> q = em.createQuery(
                    "SELECT r FROM Restaurant r WHERE r.address = :address",
                    Restaurant.class
            );
            q.setParameter("address", address);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Restaurant restaurant) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(restaurant);
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
            Restaurant r = em.find(Restaurant.class, id);
            if (r != null) em.remove(r);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}