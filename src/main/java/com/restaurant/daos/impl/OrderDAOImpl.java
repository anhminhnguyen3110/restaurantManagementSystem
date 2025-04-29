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

    @Override
    public Order add(Order order) {
        EntityManager em = emf.createEntityManager();
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
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public Order getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Order.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> find(GetOrderDto dto) {
        EntityManager em = emf.createEntityManager();
        try {
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
                        cb.function("DATE", String.class, root.get("createdAt")),
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

            if (dto.getSortBy() != null && !dto.getSortBy().isBlank()) {
                if ("desc".equalsIgnoreCase(dto.getSortDir())) {
                    cq.orderBy(cb.desc(root.get(dto.getSortBy())));
                } else {
                    cq.orderBy(cb.asc(root.get(dto.getSortBy())));
                }
            } else {
                cq.orderBy(cb.asc(root.get("id")));
            }

            TypedQuery<Order> q = em.createQuery(cq);

            int pageSize = dto.getSize();
            int page     = dto.getPage();

            q.setFirstResult(page * pageSize);
            q.setMaxResults(pageSize + 1);

            List<Order> fetched = q.getResultList();

            boolean hasNext = fetched.size() > pageSize;

            List<Order> pageContent = hasNext
                    ? fetched.subList(0, pageSize)
                    : fetched;

            return pageContent;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Order order) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(order);
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
            Order o = em.find(Order.class, id);
            if (o != null) em.remove(o);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    @Override
    public boolean hasPendingForTableAndType(int tableId, OrderType type) {
        EntityManager em = emf.createEntityManager();
        try {
            Long cnt = em.createQuery(
                            "SELECT COUNT(o) FROM Order o WHERE o.restaurantTable.id=:tid AND o.orderType=:t AND o.status=:status",
                            Long.class)
                    .setParameter("tid", tableId)
                    .setParameter("t", type)
                    .setParameter("status", OrderStatus.PENDING)
                    .getSingleResult();
            return cnt > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Order> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
        } finally {
            em.close();
        }
    }
}