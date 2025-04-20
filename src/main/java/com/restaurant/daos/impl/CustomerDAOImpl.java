package com.restaurant.daos.impl;

import com.restaurant.daos.CustomerDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Injectable
public class CustomerDAOImpl implements CustomerDAO {
    @Inject
    private EntityManagerFactory emf;

    public CustomerDAOImpl() {}

    public CustomerDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void add(Customer customer) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(customer);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Customer getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Customer.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Customer> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Customer> q = em.createQuery("SELECT c FROM Customer c", Customer.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Customer findByPhoneNumber(String phoneNumber) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Customer> q = em.createQuery(
                    "SELECT c FROM Customer c WHERE c.phoneNumber = :phone",
                    Customer.class
            );
            q.setParameter("phone", phoneNumber);
            return q.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Customer customer) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(customer);
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
            Customer c = em.find(Customer.class, id);
            if (c != null) em.remove(c);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}