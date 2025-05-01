package com.restaurant.daos.impl;

import com.restaurant.daos.BookingDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.booking.GetBookingsDto;
import com.restaurant.models.Booking;
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

    public BookingDAOImpl() {
        // Default constructor for DI
    }

    @Override
    public void add(Booking booking) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(booking);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public Booking getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Booking.class, id);
        }
    }

    @Override
    public List<Booking> find(GetBookingsDto dto) {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Booking> cq = cb.createQuery(Booking.class);
            Root<Booking> root = cq.from(Booking.class);

            root.fetch("customer", JoinType.LEFT);
            root.fetch("table", JoinType.LEFT);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getCustomerName() != null && !dto.getCustomerName().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("customer").get("name")),
                        "%" + dto.getCustomerName().toLowerCase() + "%"));
            }
            if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("customer").get("phoneNumber")),
                        "%" + dto.getPhoneNumber().toLowerCase() + "%"));
            }
            if (dto.getTableNumber() != null) {
                preds.add(cb.equal(
                        root.get("table").get("number"),
                        dto.getTableNumber()));
            }
            if (dto.getStatus() != null) {
                preds.add(cb.equal(root.get("status"), dto.getStatus()));
            }
            if (dto.getDate() != null) {
                preds.add(cb.equal(root.get("date"), dto.getDate()));
            }
            if (dto.getStartTime() != null) {
                preds.add(cb.equal(root.get("startTime"), dto.getStartTime()));
            }
            if (dto.getEndTime() != null) {
                preds.add(cb.equal(root.get("endTime"), dto.getEndTime()));
            }
            if (!preds.isEmpty()) {
                cq.where(preds.toArray(new Predicate[0]));
            }

            Path<?> sortPath;
            switch (dto.getSortBy()) {
                case "date" -> sortPath = root.get("date");
                case "startTime" -> sortPath = root.get("startTime");
                case "endTime" -> sortPath = root.get("endTime");
                case "customer" -> sortPath = root.get("customer").get("name");
                case "phone" -> sortPath = root.get("customer").get("phoneNumber");
                case "restaurant" -> sortPath = root.get("table").get("restaurant").get("name");
                case "table" -> sortPath = root.get("table").get("number");
                case "status" -> sortPath = root.get("status");
                default -> sortPath = root.get("id");
            }

            cq.orderBy("desc".equalsIgnoreCase(dto.getSortDir())
                    ? cb.desc(sortPath)
                    : cb.asc(sortPath));

            TypedQuery<Booking> query = em.createQuery(cq);
            query.setFirstResult(dto.getPage() * dto.getSize());
            query.setMaxResults(dto.getSize());
            return query.getResultList();
        }
    }

    @Override
    public void update(Booking booking) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(booking);
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
                Booking b = em.find(Booking.class, id);
                if (b != null) {
                    em.remove(b);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}
