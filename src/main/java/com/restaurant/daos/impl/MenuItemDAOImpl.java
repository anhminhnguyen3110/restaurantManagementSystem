package com.restaurant.daos.impl;

import com.restaurant.daos.MenuItemDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.menuItem.GetMenuItemsDto;
import com.restaurant.models.MenuItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Injectable
public class MenuItemDAOImpl implements MenuItemDAO {
    @Inject
    private EntityManagerFactory emf;

    public MenuItemDAOImpl() {
        // Default constructor for DI
    }

    @Override
    public void add(MenuItem item) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(item);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public MenuItem getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(MenuItem.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<MenuItem> find(GetMenuItemsDto dto) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MenuItem> cq = cb.createQuery(MenuItem.class);
            Root<MenuItem> root = cq.from(MenuItem.class);

            root.fetch("menu", JoinType.LEFT)
                    .fetch("restaurant", JoinType.LEFT);
            root.fetch("orderItems", JoinType.LEFT);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getName() != null && !dto.getName().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + dto.getName().toLowerCase() + "%"
                ));
            }
            if (dto.getMoreThanPrice() > 0) {
                preds.add(cb.ge(root.get("price"), dto.getMoreThanPrice()));
            }
            if (dto.getLessThanPrice() < Double.MAX_VALUE) {
                preds.add(cb.le(root.get("price"), dto.getLessThanPrice()));
            }
            if (dto.getMenuId() > 0) {
                preds.add(cb.equal(
                        root.get("menu").get("id"),
                        dto.getMenuId()
                ));
            }
            if (dto.getRestaurantId() > 0) {
                preds.add(cb.equal(
                        root.get("menu").get("restaurant").get("id"),
                        dto.getRestaurantId()
                ));
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

            cq.select(root)
                    .distinct(true);
            if (!preds.isEmpty()) {
                cq.where(cb.and(preds.toArray(new Predicate[0])));
            }

            TypedQuery<MenuItem> q = em.createQuery(cq);
            q.setFirstResult(dto.getPage() * dto.getSize());
            q.setMaxResults(dto.getSize());
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(MenuItem item) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(item);
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
            MenuItem m = em.find(MenuItem.class, id);
            if (m != null) em.remove(m);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public boolean existsByName(String name) {
        return existsByName(name, null);
    }

    @Override
    public boolean existsByName(String name, Integer excludeId) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT COUNT(m) FROM MenuItem m WHERE m.name = :name"
                    + (excludeId != null ? " AND m.id <> :eid" : "");
            TypedQuery<Long> q = em.createQuery(jpql, Long.class)
                    .setParameter("name", name);
            if (excludeId != null) {
                q.setParameter("eid", excludeId);
            }
            return q.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}