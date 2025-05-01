package com.restaurant.daos.impl;

import com.restaurant.constants.OrderStatus;
import com.restaurant.constants.OrderType;
import com.restaurant.daos.OrderDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.order.GetOrderDto;
import com.restaurant.models.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Injectable
public class OrderDAOImpl implements OrderDAO {
    @Inject
    private EntityManagerFactory emf;

    public OrderDAOImpl() {
        // Default constructor for DI
    }

    public OrderDAOImpl(EntityManagerFactory emf) {
        // Testing-purpose constructor
        this();
        this.emf = emf;
    }

    @Override
    public Order add(Order order) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                if (order.getRestaurant() != null) {
                    order.setRestaurant(em.merge(order.getRestaurant()));
                }
                if (order.getRestaurantTable() != null) {
                    order.setRestaurantTable(em.merge(order.getRestaurantTable()));
                }
                Order managed = em.merge(order);
                em.flush();
                tx.commit();
                return managed;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public Order getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Order.class, id);
        }
    }

    @Override
    public List<Order> find(GetOrderDto dto) {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Order> cq = cb.createQuery(Order.class);
            Root<Order> root = cq.from(Order.class);
            root.fetch("restaurantTable", JoinType.LEFT);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getRestaurantTable() != null) {
                preds.add(cb.equal(
                        root.get("restaurantTable").get("id"),
                        dto.getRestaurantTable().getId()
                ));
            }
            if (dto.getOrderType() != null) {
                preds.add(cb.equal(root.get("orderType"), dto.getOrderType()));
            }
            if (dto.getTotalPrice() > 0) {
                preds.add(cb.equal(root.get("totalPrice"), dto.getTotalPrice()));
            }
            if (dto.getDate() != null) {
                preds.add(cb.equal(
                        cb.function("DATE", dto.getDate().getClass(), root.get("createdAt")),
                        dto.getDate()
                ));
            }
            if (dto.getStatus() != null) {
                preds.add(cb.equal(root.get("status"), dto.getStatus()));
            }
            if (dto.getRestaurantId() > 0) {
                preds.add(cb.equal(
                        root.get("restaurant").get("id"),
                        dto.getRestaurantId()
                ));
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
                    : cb.asc(sortPath)
            );

            TypedQuery<Order> q = em.createQuery(cq);
            int pageSize = dto.getSize();
            int page = dto.getPage();
            q.setFirstResult(page * pageSize);
            q.setMaxResults(pageSize + 1);

            List<Order> fetched = q.getResultList();
            return (fetched.size() > pageSize)
                    ? fetched.subList(0, pageSize)
                    : fetched;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while fetching orders", e);
        }
    }

    @Override
    public void update(Order order) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(order);
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
                Order o = em.find(Order.class, id);
                if (o != null) {
                    em.remove(o);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean hasPendingForTableAndType(int tableId, OrderType type) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(o) FROM Order o " +
                            "WHERE o.restaurantTable.id = :tid " +
                            "  AND o.orderType      = :type " +
                            "  AND o.status         = :status",
                    Long.class
            );
            q.setParameter("tid", tableId);
            q.setParameter("type", type);
            q.setParameter("status", OrderStatus.PENDING);
            return q.getSingleResult() > 0;
        }
    }
}
