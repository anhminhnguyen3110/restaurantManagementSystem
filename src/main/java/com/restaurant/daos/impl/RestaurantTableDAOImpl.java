package com.restaurant.daos.impl;

import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.RestaurantTable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Injectable
public class RestaurantTableDAOImpl implements RestaurantTableDAO {
    @Inject
    private EntityManagerFactory emf;

    public RestaurantTableDAOImpl() {
    }

    public RestaurantTableDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(RestaurantTable restaurantTable) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(restaurantTable);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public RestaurantTable getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(RestaurantTable.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<RestaurantTable> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<RestaurantTable> q = em.createQuery("SELECT t FROM RestaurantTable t", RestaurantTable.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public RestaurantTable findByNumber(int number) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<RestaurantTable> q = em.createQuery(
                    "SELECT t FROM RestaurantTable t WHERE t.number = :number",
                    RestaurantTable.class
            ).setParameter("number", number).setMaxResults(1);
            return q.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public List<RestaurantTable> findByCapacity(int capacity) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<RestaurantTable> q = em.createQuery(
                    "SELECT t FROM RestaurantTable t WHERE t.capacity = :capacity",
                    RestaurantTable.class
            ).setParameter("capacity", capacity);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(RestaurantTable restaurantTable) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(restaurantTable);
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
            RestaurantTable t = em.find(RestaurantTable.class, id);
            if (t != null) em.remove(t);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}
