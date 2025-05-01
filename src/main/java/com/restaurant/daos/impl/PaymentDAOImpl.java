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
    @Inject
    private EntityManagerFactory emf;

    public PaymentDAOImpl() {
        // Default constructor for DI
    }

    public PaymentDAOImpl(EntityManagerFactory emf) {
        // Testing-purpose constructor
        this();
        this.emf = emf;
    }

    @Override
    public void add(Payment payment) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(payment);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public Payment getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Payment.class, id);
        }
    }

    @Override
    public List<Payment> find(GetPaymentDto dto) {
        try (EntityManager em = emf.createEntityManager()) {
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
        } catch (RuntimeException e) {
            throw new RuntimeException("Error fetching payments", e);
        }
    }

    @Override
    public boolean existsByOrder(int orderId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(p) FROM Payment p " +
                            "WHERE p.order.id = :oid AND p.status != :cancelStatus",
                    Long.class
            );
            q.setParameter("oid", orderId);
            q.setParameter("cancelStatus", PaymentStatus.CANCELLED);
            return q.getSingleResult() > 0;
        }
    }
}
