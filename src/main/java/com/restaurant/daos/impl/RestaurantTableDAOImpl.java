package com.restaurant.daos.impl;

import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableForBookingDto;
import com.restaurant.models.RestaurantTable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Injectable
public class RestaurantTableDAOImpl implements RestaurantTableDAO {
    @Inject
    private EntityManagerFactory emf;

    public RestaurantTableDAOImpl() {
        // Default constructor for DI
    }

    @Override
    public void add(RestaurantTable restaurantTable) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(restaurantTable);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<RestaurantTable> find(GetRestaurantTableDto dto) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<RestaurantTable> q = em.createQuery(
                    "SELECT t FROM RestaurantTable t WHERE t.restaurant.id = :rid",
                    RestaurantTable.class
            );
            q.setParameter("rid", dto.getRestaurantId());
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error finding restaurant tables", e);
        }
    }

    @Override
    public List<RestaurantTable> findForBooking(GetRestaurantTableForBookingDto dto) {
        if (dto.getTime() != null) {
            try (EntityManager em = emf.createEntityManager()) {
                String jpql = """
                        SELECT t
                          FROM RestaurantTable t
                         WHERE t.restaurant.id = :rid
                           AND t.id NOT IN (
                             SELECT b.table.id
                               FROM Booking b
                              WHERE b.date = :date
                                AND b.startTime <= :time
                                AND b.endTime > :time
                           )
                        """;
                TypedQuery<RestaurantTable> q = em.createQuery(jpql, RestaurantTable.class)
                        .setParameter("rid", dto.getRestaurantId())
                        .setParameter("date", dto.getDate())
                        .setParameter("time", dto.getTime());

                return q.getResultList();
            } catch (RuntimeException e) {
                throw new RuntimeException("Error finding tables for booking", e);
            }
        } else {
            try (EntityManager em = emf.createEntityManager()) {
                String jpql = """
                        SELECT t
                          FROM RestaurantTable t
                         WHERE t.restaurant.id = :rid
                           AND t.id NOT IN (
                             SELECT b.table.id
                               FROM Booking b
                              WHERE b.date = :date
                                AND (
                                    (b.startTime <= :endTime AND b.endTime > :startTime)
                                )
                           )
                        """;

                TypedQuery<RestaurantTable> q = em.createQuery(jpql, RestaurantTable.class)
                        .setParameter("rid", dto.getRestaurantId())
                        .setParameter("date", dto.getDate())
                        .setParameter("startTime", dto.getStartTime())
                        .setParameter("endTime", dto.getEndTime());

                return q.getResultList();
            } catch (RuntimeException e) {
                throw new RuntimeException("Error finding tables for booking", e);
            }
        }
    }

    @Override
    public RestaurantTable getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(RestaurantTable.class, id);
        }
    }

    @Override
    public void update(RestaurantTable restaurantTable) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(restaurantTable);
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
                RestaurantTable t = em.find(RestaurantTable.class, id);
                if (t != null) em.remove(t);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean existsByRestaurantIdAndStartPosition(int restaurantId, int startX, int startY, Integer excludeId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT COUNT(t) FROM RestaurantTable t " +
                    "WHERE t.restaurant.id = :rid AND t.startX = :startX AND t.startY = :startY" +
                    (excludeId != null ? " AND t.id <> :eid" : "");
            TypedQuery<Long> q = em.createQuery(jpql, Long.class)
                    .setParameter("rid", restaurantId)
                    .setParameter("startX", startX)
                    .setParameter("startY", startY);
            if (excludeId != null) {
                q.setParameter("eid", excludeId);
            }
            return q.getSingleResult() > 0;
        }
    }

    @Override
    public boolean existsByRestaurantIdAndEndPosition(int restaurantId, int endX, int endY, Integer excludeId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT COUNT(t) FROM RestaurantTable t " +
                    "WHERE t.restaurant.id = :rid AND t.endX = :endX AND t.endY = :endY" +
                    (excludeId != null ? " AND t.id <> :eid" : "");
            TypedQuery<Long> q = em.createQuery(jpql, Long.class)
                    .setParameter("rid", restaurantId)
                    .setParameter("endX", endX)
                    .setParameter("endY", endY);
            if (excludeId != null) {
                q.setParameter("eid", excludeId);
            }
            return q.getSingleResult() > 0;
        }
    }

    @Override
    public boolean existsByRestaurantIdAndNumber(int restaurantId, int number, Integer excludeId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT COUNT(t) FROM RestaurantTable t " +
                    "WHERE t.restaurant.id = :rid AND t.number = :num" +
                    (excludeId != null ? " AND t.id <> :eid" : "");
            TypedQuery<Long> q = em.createQuery(jpql, Long.class)
                    .setParameter("rid", restaurantId)
                    .setParameter("num", number);
            if (excludeId != null) {
                q.setParameter("eid", excludeId);
            }
            return q.getSingleResult() > 0;
        }
    }

    @Override
    public List<RestaurantTable> findTablesForOrder(int restaurantId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT t FROM RestaurantTable t WHERE t.restaurant.id = :rid AND t.available = true";
            TypedQuery<RestaurantTable> q = em.createQuery(jpql, RestaurantTable.class)
                    .setParameter("rid", restaurantId);
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error finding available tables for order", e);
        }
    }
}
