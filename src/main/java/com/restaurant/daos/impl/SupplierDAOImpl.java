package com.restaurant.daos.impl;

import com.restaurant.daos.SupplierDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Supplier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Injectable
public class SupplierDAOImpl implements SupplierDAO {
    @Inject
    private EntityManagerFactory emf;

    public SupplierDAOImpl() {
    }

    public SupplierDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(Supplier supplier) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(supplier);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Supplier getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Supplier.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Supplier> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Supplier> q = em.createQuery("SELECT s FROM Supplier s", Supplier.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Supplier findByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Supplier> q = em.createQuery(
                    "SELECT s FROM Supplier s WHERE s.name = :name",
                    Supplier.class
            ).setParameter("name", name).setMaxResults(1);
            return q.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Supplier> findByAddress(String address) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Supplier> q = em.createQuery(
                    "SELECT s FROM Supplier s WHERE s.address = :address",
                    Supplier.class
            );
            q.setParameter("address", address);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Supplier> findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Supplier> q = em.createQuery(
                    "SELECT s FROM Supplier s WHERE s.email = :email",
                    Supplier.class
            );
            q.setParameter("email", email);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Supplier> findByPhone(String phone) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Supplier> q = em.createQuery(
                    "SELECT s FROM Supplier s WHERE s.phone = :phone",
                    Supplier.class
            );
            q.setParameter("phone", phone);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Supplier supplier) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(supplier);
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
            Supplier s = em.find(Supplier.class, id);
            if (s != null) em.remove(s);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}
