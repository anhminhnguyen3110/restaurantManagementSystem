package com.restaurant.daos.impl;

import com.restaurant.daos.MenuDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.models.Menu;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Injectable
public class MenuDAOImpl implements MenuDAO {
    @Inject
    private EntityManagerFactory emf;

    public MenuDAOImpl() {
        // Default constructor for DI
    }

    @Override
    public void add(Menu menu) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(menu);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw new RuntimeException("Error while persisting menu", e);
            }
        }
    }

    @Override
    public List<Menu> find(GetMenuDto dto) {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Menu> cq = cb.createQuery(Menu.class);
            Root<Menu> root = cq.from(Menu.class);
            root.fetch("restaurant", JoinType.LEFT);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getName() != null && !dto.getName().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + dto.getName().toLowerCase() + "%"));
            }
            if (dto.getRestaurantName() != null && !dto.getRestaurantName().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("restaurant").get("name")),
                        "%" + dto.getRestaurantName().toLowerCase() + "%"));
            }
            if (!preds.isEmpty()) {
                cq.where(preds.toArray(new Predicate[0]));
            }

            Path<?> sortPath = root.get(
                    dto.getSortBy() != null && !dto.getSortBy().isBlank()
                            ? dto.getSortBy()
                            : "id"
            );
            cq.orderBy("desc".equalsIgnoreCase(dto.getSortDir())
                    ? cb.desc(sortPath)
                    : cb.asc(sortPath));

            TypedQuery<Menu> q = em.createQuery(cq);
            q.setFirstResult(dto.getPage() * dto.getSize());
            q.setMaxResults(dto.getSize());
            return q.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching menus", e);
        }
    }

    @Override
    public void update(Menu menu) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(menu);
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
                Menu m = em.find(Menu.class, id);
                if (m != null) em.remove(m);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public Menu getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Menu menu = em.createQuery(
                            "SELECT m FROM Menu m " +
                                    "LEFT JOIN FETCH m.restaurant " +
                                    "LEFT JOIN FETCH m.items i " +
                                    "WHERE m.id = :id", Menu.class)
                    .setParameter("id", id)
                    .getSingleResult();

            menu.getItems().forEach(item -> item.getOrderItems().size());

            return menu;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean existsByNameAndRestaurant(String name, int restaurantId) {
        return existsByNameAndRestaurant(name, restaurantId, null);
    }

    @Override
    public boolean existsByNameAndRestaurant(String name, int restaurantId, Integer excludeId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT COUNT(m) FROM Menu m "
                    + "WHERE m.name = :name AND m.restaurant.id = :rid"
                    + (excludeId != null ? " AND m.id <> :eid" : "");
            TypedQuery<Long> q = em.createQuery(jpql, Long.class)
                    .setParameter("name", name)
                    .setParameter("rid", restaurantId);
            if (excludeId != null) {
                q.setParameter("eid", excludeId);
            }
            return q.getSingleResult() > 0;
        }
    }
}
