package com.restaurant.daos.impl;

import com.restaurant.daos.StockDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Stock;
import com.restaurant.models.Supplier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Injectable
public class StockDAOImpl implements StockDAO {
    @Inject
    private EntityManagerFactory emf;

    public StockDAOImpl() {
    }

    public StockDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(Stock stock) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(stock);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Stock getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Stock.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Stock> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Stock> q = em.createQuery("SELECT s FROM Stock s", Stock.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Stock> findBySupplier(Supplier supplier) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Stock> q = em.createQuery(
                    "SELECT s FROM Stock s WHERE s.supplier = :supplier",
                    Stock.class
            );
            q.setParameter("supplier", supplier);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Stock> findLowStock() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Stock> q = em.createQuery(
                    "SELECT s FROM Stock s WHERE s.quantity <= s.minThreshold",
                    Stock.class
            );
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Stock stock) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(stock);
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
            Stock s = em.find(Stock.class, id);
            if (s != null) em.remove(s);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}