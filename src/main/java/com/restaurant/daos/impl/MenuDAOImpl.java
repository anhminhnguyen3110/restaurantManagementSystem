package com.restaurant.daos.impl;

import com.restaurant.daos.MenuDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.models.Menu;
import com.restaurant.models.MenuItem;
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
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(menu);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error while persisting menu", e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Menu> find(GetMenuDto dto) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Menu> cq = cb.createQuery(Menu.class);
            Root<Menu> root = cq.from(Menu.class);
            Fetch<?, ?> restFetch = root.fetch("restaurant", JoinType.LEFT);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getName() != null && !dto.getName().isBlank()) {
                preds.add(cb.like(cb.lower(root.get("name")),
                        "%" + dto.getName().toLowerCase() + "%"));
            }
            if (dto.getRestaurantName() != null && !dto.getRestaurantName().isBlank()) {
                preds.add(cb.like(cb.lower(root.get("restaurant").get("name")),
                        "%" + dto.getRestaurantName().toLowerCase() + "%"));
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

            TypedQuery<Menu> q = em.createQuery(cq);
            q.setFirstResult(dto.getPage() * dto.getSize());
            q.setMaxResults(dto.getSize());
            return q.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching menus", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Menu menu) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(menu);
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
            Menu m = em.find(Menu.class, id);
            if (m != null) em.remove(m);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Menu getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            Menu menu = em.createQuery(
                            "SELECT m FROM Menu m " +
                                    "LEFT JOIN FETCH m.restaurant " +
                                    "LEFT JOIN FETCH m.items i " +
                                    "WHERE m.id = :id", Menu.class)
                    .setParameter("id", id)
                    .getSingleResult();

            if (!menu.getItems().isEmpty()) {
                em.createQuery(
                                "SELECT DISTINCT i FROM MenuItem i " +
                                        "LEFT JOIN FETCH i.orderItems " +
                                        "WHERE i IN :items", MenuItem.class)
                        .setParameter("items", menu.getItems())
                        .getResultList();
            }

            return menu;
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }


    @Override
    public boolean existsByNameAndRestaurant(String name, int restaurantId) {
        return existsByNameAndRestaurant(name, restaurantId, null);
    }

    @Override
    public boolean existsByNameAndRestaurant(String name, int restaurantId, Integer excludeId) {
        var em = emf.createEntityManager();
        try {
            String jpql = "SELECT COUNT(m) FROM Menu m "
                    + "WHERE m.name = :name AND m.restaurant.id = :rid"
                    + (excludeId != null ? " AND m.id <> :eid" : "");
            var q = em.createQuery(jpql, Long.class)
                    .setParameter("name", name)
                    .setParameter("rid", restaurantId);
            if (excludeId != null) {
                q.setParameter("eid", excludeId);
            }
            return q.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}