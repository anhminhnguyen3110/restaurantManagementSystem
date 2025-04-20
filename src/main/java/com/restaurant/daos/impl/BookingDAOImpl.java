package com.restaurant.daos.impl;

import com.restaurant.daos.BookingDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Booking;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Injectable
public class BookingDAOImpl implements BookingDAO {
    @Inject
    private EntityManagerFactory emf;

    public BookingDAOImpl() {}

    @Override
    public void add(Booking booking) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(booking);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Booking getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Booking.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Booking> findByCustomerPhone(String phone) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Booking> q = em.createQuery(
                    "SELECT b FROM Booking b JOIN b.customer c WHERE c.phoneNumber = :phone",
                    Booking.class
            );
            q.setParameter("phone", phone);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Booking> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Booking> q = em.createQuery("SELECT b FROM Booking b", Booking.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Booking> findInRange(LocalDateTime from, LocalDateTime to) {
        String sql =
                "SELECT * FROM bookings b " +
                        "WHERE b.start_time < :to " +
                        "  AND TIMESTAMPADD(MINUTE, " +
                        "      CASE b.duration " +
                        "        WHEN 'HALF_HOUR' THEN 30 " +
                        "        WHEN 'ONE_HOUR' THEN 60 " +
                        "        WHEN 'ONE_AND_HALF_HOUR' THEN 90 " +
                        "        WHEN 'TWO_HOURS' THEN 120 " +
                        "        WHEN 'TWO_AND_HALF_HOUR' THEN 150 " +
                        "        WHEN 'THREE_HOURS' THEN 180 " +
                        "        ELSE 210 " +
                        "      END, b.start_time) > :from";
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNativeQuery(sql, Booking.class)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Booking booking) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(booking);
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
            Booking b = em.find(Booking.class, id);
            if (b != null) em.remove(b);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}