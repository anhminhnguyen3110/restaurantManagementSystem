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

    public CustomerDAOImpl() {
        // Default constructor for DI
    }

    @Override
    public void add(Customer customer) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(customer);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Customer> find() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Customer> q = em.createQuery(
                    "SELECT c FROM Customer c", Customer.class);
            return q.getResultList();
        }
    }

    @Override
    public void update(Customer customer) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(customer);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public Customer getByPhoneNumber(String phoneNumber) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Customer> q = em.createQuery(
                    "SELECT c FROM Customer c WHERE c.phoneNumber = :phone",
                    Customer.class);
            q.setParameter("phone", phoneNumber);
            return q.getResultStream().findFirst().orElse(null);
        }
    }

    @Override
    public Customer getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Customer.class, id);
        }
    }
}
