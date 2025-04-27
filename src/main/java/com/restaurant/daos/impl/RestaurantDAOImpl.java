package com.restaurant.daos.impl;

import com.restaurant.daos.RestaurantDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.restaurant.GetRestaurantDto;
import com.restaurant.models.Restaurant;
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
public class RestaurantDAOImpl implements RestaurantDAO {
    @Inject
    private EntityManagerFactory emf;

    public RestaurantDAOImpl() {
        // Default constructor for DI
    }

    @Override
    public void add(Restaurant restaurant) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(restaurant);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Restaurant getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Restaurant.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Restaurant> find(GetRestaurantDto dto) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Restaurant> cq = cb.createQuery(Restaurant.class);
            Root<Restaurant> root = cq.from(Restaurant.class);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getName() != null && !dto.getName().isBlank()) {
                preds.add(cb.like(cb.lower(root.get("name")),
                        "%" + dto.getName().toLowerCase() + "%"));
            }
            if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
                preds.add(cb.like(cb.lower(root.get("address")),
                        "%" + dto.getAddress().toLowerCase() + "%"));
            }
            if (dto.getStatus() != null) {
                preds.add(cb.equal(root.get("status"), dto.getStatus()));
            }
            if (!preds.isEmpty()) {
                cq.where(preds.toArray(new Predicate[0]));
            }

            if (dto.getSortBy() != null && !dto.getSortBy().isBlank()) {
                if (dto.getSortDir() != null && dto.getSortDir().equalsIgnoreCase("desc")) {
                    cq.orderBy(cb.desc(root.get(dto.getSortBy())));
                } else {
                    cq.orderBy(cb.asc(root.get(dto.getSortBy())));
                }
            } else {
                cq.orderBy(cb.asc(root.get("id")));
            }

            TypedQuery<Restaurant> q = em.createQuery(cq);
            q.setFirstResult(dto.getPage() * dto.getSize());
            q.setMaxResults(dto.getSize());
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Restaurant restaurant) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(restaurant);
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
            Restaurant r = em.find(Restaurant.class, id);
            if (r != null) em.remove(r);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public boolean existsByNameAndAddress(String name, String address) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(r) FROM Restaurant r " +
                                    "WHERE lower(r.name)=:nm AND lower(r.address)=:addr",
                            Long.class)
                    .setParameter("nm", name.toLowerCase())
                    .setParameter("addr", address.toLowerCase())
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Restaurant> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Restaurant r", Restaurant.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}