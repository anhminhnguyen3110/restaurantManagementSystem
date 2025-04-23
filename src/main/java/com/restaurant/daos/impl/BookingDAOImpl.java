package com.restaurant.daos.impl;

import com.restaurant.daos.BookingDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.models.Booking;
import com.restaurant.dtos.booking.GetBookingsDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Injectable
public class BookingDAOImpl implements BookingDAO {
    @Inject
    private EntityManagerFactory emf;

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
    public List<Booking> findAll(GetBookingsDto dto) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Booking> cq = cb.createQuery(Booking.class);
            Root<Booking> root = cq.from(Booking.class);
            root.fetch("customer", JoinType.LEFT);
            Fetch<?,?> tableFetch = root.fetch("table", JoinType.LEFT);
            tableFetch.fetch("restaurant", JoinType.LEFT);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getCustomerName()!=null && !dto.getCustomerName().isBlank()) {
                preds.add(cb.like(cb.lower(root.get("customer").get("name")), "%" + dto.getCustomerName().toLowerCase() + "%"));
            }
            if (dto.getPhoneNumber()!=null && !dto.getPhoneNumber().isBlank()) {
                preds.add(cb.like(cb.lower(root.get("customer").get("phoneNumber")), "%" + dto.getPhoneNumber().toLowerCase() + "%"));
            }
            if (dto.getTableNumber()!=null) {
                preds.add(cb.equal(root.get("table").get("number"), dto.getTableNumber()));
            }
            if (dto.getStatus()!=null) {
                preds.add(cb.equal(root.get("status"), dto.getStatus()));
            }
            if (dto.getFrom()!=null) {
                preds.add(cb.greaterThanOrEqualTo(root.get("start"), dto.getFrom()));
            }
            if (dto.getTo()!=null) {
                preds.add(cb.lessThanOrEqualTo(root.get("start"), dto.getTo()));
            }
            if (!preds.isEmpty()) {
                cq.where(preds.toArray(new Predicate[0]));
            }

            String sortBy = dto.getSortBy();
            String dir = dto.getSortDir();
            Path<?> sortPath;
            switch (sortBy) {
                case "customer"   -> sortPath = root.get("customer").get("name");
                case "phone"      -> sortPath = root.get("customer").get("phoneNumber");
                case "restaurant" -> sortPath = root.get("table").get("restaurant").get("name");
                case "table"      -> sortPath = root.get("table").get("number");
                case "seats"      -> sortPath = root.get("table").get("capacity");
                case "duration"   -> sortPath = root.get("duration");
                case "start"      -> sortPath = root.get("start");
                case "end"        -> sortPath = root.get("end");
                default           -> sortPath = root.get("createdAt");
            }
            cq.orderBy("desc".equalsIgnoreCase(dir) ? cb.desc(sortPath) : cb.asc(sortPath));

            TypedQuery<Booking> query = em.createQuery(cq);
            query.setFirstResult(dto.getPage() * dto.getSize());
            query.setMaxResults(dto.getSize());
            return query.getResultList();
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
            if (b!=null) em.remove(b);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}
