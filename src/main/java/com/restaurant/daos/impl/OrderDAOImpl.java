package com.restaurant.daos.impl;

import com.restaurant.daos.OrderDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Order;
import com.restaurant.constants.OrderType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Injectable
public class OrderDAOImpl implements OrderDAO {
    @Inject
    private EntityManagerFactory emf;

    public OrderDAOImpl() {}

    public OrderDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(Order order) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(order);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Order getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Order.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Order> q = em.createQuery("SELECT o FROM Order o", Order.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findByStatus(String status) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Order> q = em.createQuery(
                    "SELECT o FROM Order o WHERE o.status = :status",
                    Order.class
            );
            q.setParameter("status", status);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findByType(OrderType type) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Order> q = em.createQuery(
                    "SELECT o FROM Order o WHERE o.orderType = :type",
                    Order.class
            );
            q.setParameter("type", type);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findByTable(int tableId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Order> q = em.createQuery(
                    "SELECT o FROM Order o WHERE o.restaurantTable.id = :tid",
                    Order.class
            );
            q.setParameter("tid", tableId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Order order) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(order);
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
            Order o = em.find(Order.class, id);
            if (o != null) em.remove(o);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}