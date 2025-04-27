package com.restaurant.daos.impl;

import com.restaurant.constants.PaymentStatus;
import com.restaurant.daos.PaymentDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.payment.GetPaymentDto;
import com.restaurant.models.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Injectable
public class PaymentDAOImpl implements PaymentDAO {
    @Inject private EntityManagerFactory emf;

    public PaymentDAOImpl() {
        // Default constructor for DI
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
    public List<Payment> find(GetPaymentDto dto) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Payment> cq = cb.createQuery(Payment.class);
            Root<Payment> root = cq.from(Payment.class);
            root.fetch("order", JoinType.LEFT);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getOrderId() > 0) {
                preds.add(cb.equal(root.get("order").get("id"), dto.getOrderId()));
            }
            if (dto.getMethod() != null) {
                preds.add(cb.equal(root.get("method"), dto.getMethod()));
            }
            if (dto.getStatus() != null) {
                preds.add(cb.equal(root.get("status"), dto.getStatus()));
            }
            if (!preds.isEmpty()) {
                cq.where(preds.toArray(new Predicate[0]));
            }

            TypedQuery<Payment> q = em.createQuery(cq);
            q.setFirstResult(dto.getPage() * dto.getSize());
            q.setMaxResults(dto.getSize());
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsByOrder(int orderId) {
        EntityManager em = emf.createEntityManager();
        try {
            Long cnt = em.createQuery(
                            "SELECT COUNT(p) FROM Payment p WHERE p.order.id = :oid AND p.status != :cancelStatus",
                            Long.class)
                    .setParameter("oid", orderId)
                    .setParameter("cancelStatus", PaymentStatus.CANCELLED)
                    .getSingleResult();
            return cnt > 0;
        } finally {
            em.close();
        }
    }
}