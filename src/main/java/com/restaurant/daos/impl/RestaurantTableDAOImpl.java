package com.restaurant.daos.impl;

import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableForBookingDto;
import com.restaurant.models.RestaurantTable;
import jakarta.persistence.EntityManagerFactory;

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
        var em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(restaurantTable);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public List<RestaurantTable> find(GetRestaurantTableDto dto) {
        var em = emf.createEntityManager();
        System.out.println("Finding tables for restaurant ID: " + dto.getRestaurantId());
        try {
            var q = em.createQuery(
                    "SELECT t FROM RestaurantTable t WHERE t.restaurant.id = :rid",
                    RestaurantTable.class
            );
            q.setParameter("rid", dto.getRestaurantId());
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<RestaurantTable> findForBooking(GetRestaurantTableForBookingDto dto) {
        var em = emf.createEntityManager();
        try {
            var jpql = """
                    SELECT t
                      FROM RestaurantTable t
                     WHERE t.restaurant.id = :rid
                       AND t.id NOT IN (
                         SELECT b.table.id
                           FROM Booking b
                          WHERE b.date = :date
                            AND b.startTime < :endTime
                            AND b.endTime > :startTime
                       )
                    """;
            var q = em.createQuery(jpql, RestaurantTable.class);
            q.setParameter("rid", dto.getRestaurantId());
            q.setParameter("date", dto.getDate());
            q.setParameter("startTime", dto.getStartTime());
            q.setParameter("endTime", dto.getEndTime());
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public RestaurantTable getById(int id) {
        var em = emf.createEntityManager();
        try {
            return em.find(RestaurantTable.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(RestaurantTable restaurantTable) {
        var em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(restaurantTable);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public void delete(int id) {
        var em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var t = em.find(RestaurantTable.class, id);
            if (t != null) em.remove(t);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public boolean existsByRestaurantIdAndStartPosition(int restaurantId, int startX, int startY, Integer excludeId) {
        var em = emf.createEntityManager();
        try {
            String jpql = "SELECT COUNT(t) FROM RestaurantTable t WHERE t.restaurant.id = :rid AND t.startX = :startX AND t.startY = :startY"
                    + (excludeId != null ? " AND t.id <> :eid" : "");
            var q = em.createQuery(jpql, Long.class)
                    .setParameter("rid", restaurantId)
                    .setParameter("startX", startX)
                    .setParameter("startY", startY);
            if (excludeId != null) {
                q.setParameter("eid", excludeId);
            }
            return q.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsByRestaurantIdAndEndPosition(int restaurantId, int endX, int endY, Integer excludeId) {
        var em = emf.createEntityManager();
        try {
            String jpql = "SELECT COUNT(t) FROM RestaurantTable t WHERE t.restaurant.id = :rid AND t.endX = :endX AND t.endY = :endY"
                    + (excludeId != null ? " AND t.id <> :eid" : "");
            var q = em.createQuery(jpql, Long.class)
                    .setParameter("rid", restaurantId)
                    .setParameter("endX", endX)
                    .setParameter("endY", endY);
            if (excludeId != null) {
                q.setParameter("eid", excludeId);
            }
            return q.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsByRestaurantIdAndNumber(int restaurantId, int number, Integer excludeId) {
        var em = emf.createEntityManager();
        try {
            String jpql = "SELECT COUNT(t) FROM RestaurantTable t WHERE t.restaurant.id = :rid AND t.number = :num"
                    + (excludeId != null ? " AND t.id <> :eid" : "");
            var q = em.createQuery(jpql, Long.class)
                    .setParameter("rid", restaurantId)
                    .setParameter("num", number);
            if (excludeId != null) {
                q.setParameter("eid", excludeId);
            }
            return q.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}