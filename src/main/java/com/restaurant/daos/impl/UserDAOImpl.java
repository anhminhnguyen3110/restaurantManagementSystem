package com.restaurant.daos.impl;

import com.restaurant.constants.UserRole;
import com.restaurant.daos.UserDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.user.GetUserDto;
import com.restaurant.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

@Injectable
public class UserDAOImpl implements UserDAO {
    @Inject
    private EntityManagerFactory emf;

    public UserDAOImpl() {
        // Default constructor for DI
    }

    @Override
    public void add(User user) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(user);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public User getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(User.class, id);
        }
    }

    @Override
    public List<User> find(GetUserDto dto) {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> root = cq.from(User.class);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("username")),
                        "%" + dto.getUsername().toLowerCase() + "%"
                ));
            }
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("email")),
                        "%" + dto.getEmail().toLowerCase() + "%"
                ));
            }
            if (dto.getRole() != null) {
                preds.add(cb.equal(root.get("role"), dto.getRole()));
            }
            if (dto.getName() != null && !dto.getName().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + dto.getName().toLowerCase() + "%"
                ));
            }
            if (!preds.isEmpty()) {
                cq.where(preds.toArray(new Predicate[0]));
            }

            TypedQuery<User> q = em.createQuery(cq);
            q.setFirstResult(dto.getPage() * dto.getSize());
            q.setMaxResults(dto.getSize());
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error fetching users", e);
        }
    }

    @Override
    public User findByUsername(String username) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<User> q = em.createQuery(
                    "SELECT u FROM User u WHERE lower(u.username) = :un", User.class
            );
            q.setParameter("un", username.toLowerCase());
            q.setMaxResults(1);
            return q.getResultStream().findFirst().orElse(null);
        }
    }

    @Override
    public void update(User user) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(user);
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
                User u = em.find(User.class, id);
                if (u != null) {
                    em.remove(u);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        try (EntityManager em = emf.createEntityManager()) {
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE lower(u.username) = :un", Long.class
                    )
                    .setParameter("un", username.toLowerCase())
                    .getSingleResult();
            return count > 0;
        }
    }

    @Override
    public List<User> findAllShippers() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<User> q = em.createQuery(
                    "SELECT u FROM User u WHERE u.role = :role", User.class
            );
            q.setParameter("role", UserRole.SHIPPER);
            return q.getResultList();
        }
    }
}
