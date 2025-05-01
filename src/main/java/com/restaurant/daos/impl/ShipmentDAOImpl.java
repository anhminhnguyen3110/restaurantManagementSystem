package com.restaurant.daos.impl;

import com.restaurant.constants.ShipmentStatus;
import com.restaurant.daos.ShipmentDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.shipment.GetShipmentDto;
import com.restaurant.models.Shipment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Injectable
public class ShipmentDAOImpl implements ShipmentDAO {
    @Inject
    private EntityManagerFactory emf;

    public ShipmentDAOImpl() {
        // Default constructor for DI
    }

    public ShipmentDAOImpl(EntityManagerFactory emf) {
        // Testing-purpose constructor
        this();
        this.emf = emf;
    }

    @Override
    public void add(Shipment shipment) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(shipment);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public Shipment getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Shipment.class, id);
        }
    }

    @Override
    public List<Shipment> find(GetShipmentDto dto) {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Shipment> cq = cb.createQuery(Shipment.class);
            Root<Shipment> root = cq.from(Shipment.class);
            root.fetch("order", JoinType.LEFT);
            root.fetch("shipper", JoinType.LEFT);
            root.fetch("customer", JoinType.LEFT);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getServiceType() != null) {
                preds.add(cb.equal(root.get("serviceType"), dto.getServiceType()));
            }
            if (dto.getOrderId() > 0) {
                preds.add(cb.equal(root.get("order").get("id"), dto.getOrderId()));
            }
            if (dto.getShipperName() != null && !dto.getShipperName().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("shipper").get("name")),
                        "%" + dto.getShipperName().toLowerCase() + "%"
                ));
            }
            if (dto.getCustomerName() != null && !dto.getCustomerName().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("customer").get("name")),
                        "%" + dto.getCustomerName().toLowerCase() + "%"
                ));
            }
            if (dto.getStatus() != null) {
                preds.add(cb.equal(root.get("status"), dto.getStatus()));
            }
            if (dto.getTrackingNumber() != null && !dto.getTrackingNumber().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("trackingNumber")),
                        "%" + dto.getTrackingNumber().toLowerCase() + "%"
                ));
            }
            if (!preds.isEmpty()) {
                cq.where(preds.toArray(new Predicate[0]));
            }

            TypedQuery<Shipment> q = em.createQuery(cq);
            q.setFirstResult(dto.getPage() * dto.getSize());
            q.setMaxResults(dto.getSize());
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error fetching shipments", e);
        }
    }

    @Override
    public void update(Shipment shipment) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(shipment);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public void delete(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Shipment s = em.find(Shipment.class, id);
                if (s != null) em.remove(s);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean existsPendingByOrder(int orderId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(s) FROM Shipment s WHERE s.order.id = :oid AND s.status = :st",
                    Long.class
            );
            q.setParameter("oid", orderId);
            q.setParameter("st", ShipmentStatus.SHIPPING);
            return q.getSingleResult() > 0;
        }
    }
}
