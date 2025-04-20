package com.restaurant.daos.impl;

import com.restaurant.daos.ShipmentDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Shipment;
import com.restaurant.constants.ShipmentStatus;
import com.restaurant.constants.ShipmentService;
import com.restaurant.models.Order;
import com.restaurant.models.User;
import com.restaurant.models.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Injectable
public class ShipmentDAOImpl implements ShipmentDAO {
    @Inject
    private EntityManagerFactory emf;

    public ShipmentDAOImpl() {
    }

    public ShipmentDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(Shipment shipment) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(shipment);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Shipment getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Shipment.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Shipment> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Shipment> q = em.createQuery("SELECT s FROM Shipment s", Shipment.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Shipment> findByStatus(ShipmentStatus status) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Shipment> q = em.createQuery(
                    "SELECT s FROM Shipment s WHERE s.status = :status",
                    Shipment.class
            );
            q.setParameter("status", status);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Shipment> findByServiceType(ShipmentService serviceType) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Shipment> q = em.createQuery(
                    "SELECT s FROM Shipment s WHERE s.serviceType = :serviceType",
                    Shipment.class
            );
            q.setParameter("serviceType", serviceType);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Shipment> findByOrder(Order order) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Shipment> q = em.createQuery(
                    "SELECT s FROM Shipment s WHERE s.order = :orderParam",
                    Shipment.class
            );
            q.setParameter("orderParam", order);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Shipment> findByShipper(User shipper) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Shipment> q = em.createQuery(
                    "SELECT s FROM Shipment s WHERE s.shipper = :shipper",
                    Shipment.class
            );
            q.setParameter("shipper", shipper);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Shipment> findByCustomer(Customer customer) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Shipment> q = em.createQuery(
                    "SELECT s FROM Shipment s WHERE s.customer = :customer",
                    Shipment.class
            );
            q.setParameter("customer", customer);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Shipment shipment) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(shipment);
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
            Shipment s = em.find(Shipment.class, id);
            if (s != null) em.remove(s);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}