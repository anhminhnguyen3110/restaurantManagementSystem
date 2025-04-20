package com.restaurant.daos.impl;

import com.restaurant.daos.OrderItemDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.OrderItem;
import com.restaurant.constants.OrderItemStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Injectable
public class OrderItemDAOImpl implements OrderItemDAO {
    @Inject
    private EntityManagerFactory emf;

    public OrderItemDAOImpl() {}

    public OrderItemDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(OrderItem item) {
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
    public OrderItem getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(OrderItem.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<OrderItem> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderItem> q = em.createQuery("SELECT oi FROM OrderItem oi", OrderItem.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<OrderItem> findByOrder(int orderId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderItem> q = em.createQuery(
                    "SELECT oi FROM OrderItem oi WHERE oi.order.id = :oid",
                    OrderItem.class
            );
            q.setParameter("oid", orderId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<OrderItem> findByMenuItem(int menuItemId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderItem> q = em.createQuery(
                    "SELECT oi FROM OrderItem oi WHERE oi.menuItem.id = :mid",
                    OrderItem.class
            );
            q.setParameter("mid", menuItemId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<OrderItem> findByStatus(OrderItemStatus status) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderItem> q = em.createQuery(
                    "SELECT oi FROM OrderItem oi WHERE oi.status = :st",
                    OrderItem.class
            );
            q.setParameter("st", status);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(OrderItem item) {
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
            OrderItem oi = em.find(OrderItem.class, id);
            if (oi != null) em.remove(oi);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}