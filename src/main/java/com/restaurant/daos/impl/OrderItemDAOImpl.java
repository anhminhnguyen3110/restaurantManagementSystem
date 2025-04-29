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

    @Override
    public void add(OrderItem item) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(item);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public OrderItem getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(OrderItem.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<OrderItem> find(GetOrderItemDto dto) {
        EntityManager em = emf.createEntityManager();
        try {
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
                preds.add(cb.like(cb.lower(root.get("menuItem").get("name")),
                        "%" + dto.getMenuItemName().toLowerCase() + "%"));
            }
            if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
                preds.add(cb.equal(root.get("status"),
                        OrderItemStatus.valueOf(dto.getStatus())));
            }
            if (dto.getOrderId() > 0) {
                preds.add(cb.equal(root.get("order").get("id"), dto.getOrderId()));
            }
            if (dto.getRestaurantId() > 0) {
                preds.add(cb.equal(
                        root.get("order").get("restaurant").get("id"),
                        dto.getRestaurantId()
                ));
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

            TypedQuery<OrderItem> q = em.createQuery(cq);
            q.setFirstResult(dto.getPage() * dto.getSize());
            q.setMaxResults(dto.getSize());

            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(OrderItem item) {
        EntityManager em = emf.createEntityManager();
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
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }


    @Override
    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            OrderItem oi = em.find(OrderItem.class, id);
            if (oi != null) em.remove(oi);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public boolean existsByOrderAndMenuItem(int orderId, int menuItemId, String customization) {
        EntityManager em = emf.createEntityManager();
        try {
            Long cnt = em.createQuery(
                            "SELECT COUNT(oi) FROM OrderItem oi " +
                                    "WHERE oi.order.id = :oid AND oi.menuItem.id = :mid AND oi.customization = :cust",
                            Long.class)
                    .setParameter("oid", orderId)
                    .setParameter("mid", menuItemId)
                    .setParameter("cust", customization)
                    .getSingleResult();
            return cnt > 0;
        } finally {
            em.close();
        }
    }
}