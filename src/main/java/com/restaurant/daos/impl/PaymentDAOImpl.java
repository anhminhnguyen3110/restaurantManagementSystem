package com.restaurant.daos.impl;

import com.restaurant.daos.PaymentDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Payment;
import com.restaurant.constants.PaymentMethod;
import com.restaurant.constants.PaymentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Injectable
public class PaymentDAOImpl implements PaymentDAO {
    @Inject
    private EntityManagerFactory emf;

    public PaymentDAOImpl() {
    }

    public PaymentDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(Payment payment) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(payment);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Payment getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Payment.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Payment> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Payment> q = em.createQuery("SELECT p FROM Payment p", Payment.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Payment findByOrderId(int orderId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Payment> q = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.order.id = :oid",
                    Payment.class
            ).setParameter("oid", orderId).setMaxResults(1);
            return q.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Payment> q = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.status = :st",
                    Payment.class
            );
            q.setParameter("st", status);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Payment> findByMethod(PaymentMethod method) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Payment> q = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.method = :mth",
                    Payment.class
            );
            q.setParameter("mth", method);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Payment payment) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(payment);
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
            Payment p = em.find(Payment.class, id);
            if (p != null) em.remove(p);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}
