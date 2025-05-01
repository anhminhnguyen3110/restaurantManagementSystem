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

    public MenuItemDAOImpl(EntityManagerFactory emf) {
        // Testing-purpose constructor
        this();
        this.emf = emf;
    }

    @Override
    public void add(MenuItem item) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(item);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public MenuItem getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(MenuItem.class, id);
        }
    }

    @Override
    public List<MenuItem> find(GetMenuItemsDto dto) {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MenuItem> cq = cb.createQuery(MenuItem.class);
            Root<MenuItem> root = cq.from(MenuItem.class);

            // fetch associations
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

            cq.select(root).distinct(true);
            if (!preds.isEmpty()) {
                cq.where(cb.and(preds.toArray(new Predicate[0])));
            }

            // determine sort path (default to "id")
            Path<?> sortPath = root.get(
                    dto.getSortBy() != null && !dto.getSortBy().isBlank()
                            ? dto.getSortBy()
                            : "id"
            );
            cq.orderBy("desc".equalsIgnoreCase(dto.getSortDir())
                    ? cb.desc(sortPath)
                    : cb.asc(sortPath));

            TypedQuery<MenuItem> q = em.createQuery(cq);
            q.setFirstResult(dto.getPage() * dto.getSize());
            q.setMaxResults(dto.getSize());
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error fetching menu items", e);
        }
    }

    @Override
    public void update(MenuItem item) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(item);
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
                MenuItem m = em.find(MenuItem.class, id);
                if (m != null) {
                    em.remove(m);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean existsByName(String name) {
        return existsByName(name, null);
    }

    @Override
    public boolean existsByName(String name, Integer excludeId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT COUNT(m) FROM MenuItem m WHERE m.name = :name"
                    + (excludeId != null ? " AND m.id <> :eid" : "");
            TypedQuery<Long> q = em.createQuery(jpql, Long.class)
                    .setParameter("name", name);
            if (excludeId != null) {
                q.setParameter("eid", excludeId);
            }
            return q.getSingleResult() > 0;
        }
    }

    @Override
    public List<MenuItem> findByRestaurantId(int restaurantId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT m FROM MenuItem m JOIN m.menu r WHERE r.restaurant.id = :restaurantId";
            TypedQuery<MenuItem> q = em.createQuery(jpql, MenuItem.class)
                    .setParameter("restaurantId", restaurantId);
            return q.getResultList();
        }
    }
}
