package com.restaurant.daos.impl;

import com.restaurant.constants.OrderItemStatus;
import com.restaurant.daos.OrderItemDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.orderItem.GetOrderItemDto;
import com.restaurant.models.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Injectable
public class OrderItemDAOImpl implements OrderItemDAO {
    @Inject
    private EntityManagerFactory emf;

    public OrderItemDAOImpl() {
        // Default constructor for DI
    }

    public OrderItemDAOImpl(EntityManagerFactory emf) {
        // Testing-purpose constructor
        this();
        this.emf = emf;
    }

    @Override
    public void add(OrderItem item) {
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
    public OrderItem getById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(OrderItem.class, id);
        }
    }

    @Override
    public List<OrderItem> find(GetOrderItemDto dto) {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OrderItem> cq = cb.createQuery(OrderItem.class);
            Root<OrderItem> root = cq.from(OrderItem.class);
            root.fetch("order", JoinType.LEFT);
            root.fetch("menuItem", JoinType.INNER);

            List<Predicate> preds = new ArrayList<>();
            if (dto.getId() > 0) {
                preds.add(cb.equal(root.get("id"), dto.getId()));
            }
            if (dto.getMenuItemName() != null && !dto.getMenuItemName().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("menuItem").get("name")),
                        "%" + dto.getMenuItemName().toLowerCase() + "%"
                ));
            }
            if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
                preds.add(cb.equal(
                        root.get("status"),
                        OrderItemStatus.valueOf(dto.getStatus())
                ));
            }
            if (dto.getOrderId() > 0) {
                preds.add(cb.equal(
                        root.get("order").get("id"),
                        dto.getOrderId()
                ));
            }
            if (dto.getRestaurantId() > 0) {
                preds.add(cb.equal(
                        root.get("order").get("restaurant").get("id"),
                        dto.getRestaurantId()
                ));
            }
            if (!preds.isEmpty()) {
                cq.where(cb.and(preds.toArray(new Predicate[0])));
            }

            // determine sort path (default "id")
            Path<?> sortPath = root.get(
                    (dto.getSortBy() != null && !dto.getSortBy().isBlank())
                            ? dto.getSortBy()
                            : "id"
            );
            cq.orderBy("desc".equalsIgnoreCase(dto.getSortDir())
                    ? cb.desc(sortPath)
                    : cb.asc(sortPath)
            );

            TypedQuery<OrderItem> q = em.createQuery(cq);
            q.setFirstResult(dto.getPage() * dto.getSize());
            q.setMaxResults(dto.getSize());
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error fetching order items", e);
        }
    }

    @Override
    public void update(OrderItem item) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                OrderItem managed = em.find(OrderItem.class, item.getId());
                if (managed == null) {
                    throw new IllegalArgumentException("No OrderItem with id=" + item.getId());
                }
                if (item.getQuantity() != 0) {
                    managed.setQuantity(item.getQuantity());
                }
                if (item.getCustomization() != null) {
                    managed.setCustomization(item.getCustomization());
                }
                if (item.getStatus() != null) {
                    managed.setStatus(item.getStatus());
                }
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
                OrderItem oi = em.find(OrderItem.class, id);
                if (oi != null) {
                    em.remove(oi);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean existsByOrderAndMenuItem(int orderId, int menuItemId, String customization) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(oi) FROM OrderItem oi " +
                            "WHERE oi.order.id = :oid " +
                            "  AND oi.menuItem.id = :mid " +
                            "  AND oi.customization = :cust",
                    Long.class
            );
            q.setParameter("oid", orderId);
            q.setParameter("mid", menuItemId);
            q.setParameter("cust", customization);
            return q.getSingleResult() > 0;
        }
    }
}
