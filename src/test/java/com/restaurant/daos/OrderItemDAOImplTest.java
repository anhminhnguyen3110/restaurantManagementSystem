package com.restaurant.daos;

import com.restaurant.constants.OrderItemStatus;
import com.restaurant.daos.impl.OrderItemDAOImpl;
import com.restaurant.dtos.orderItem.GetOrderItemDto;
import com.restaurant.models.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderItemDAOImplTest {
    @Mock
    EntityManagerFactory emf;
    @Mock
    EntityManager em;
    @Mock
    EntityTransaction tx;
    @Mock
    CriteriaBuilder cb;
    @Mock
    CriteriaQuery<OrderItem> cq;
    @Mock
    Root<OrderItem> root;
    @Mock
    Fetch<OrderItem, ?> fetchOrder;
    @Mock
    Fetch<OrderItem, ?> fetchMenuItem;
    @Mock
    Path<Object> path;
    @Mock
    Expression<String> expr;
    @Mock
    Predicate predicate;
    @Mock
    jakarta.persistence.criteria.Order jpaOrder;
    @Mock
    TypedQuery<OrderItem> query;
    @Mock
    TypedQuery<Long> countQ;
    @InjectMocks
    OrderItemDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(OrderItem.class)).thenReturn(cq);
        when(cq.from(OrderItem.class)).thenReturn(root);
        when(root.fetch(eq("order"), eq(JoinType.LEFT))).thenReturn((Fetch) fetchOrder);
        when(root.fetch(eq("menuItem"), eq(JoinType.INNER))).thenReturn((Fetch) fetchMenuItem);
        when(root.get(anyString())).thenReturn(path);
        when(path.get(anyString())).thenReturn(path);
        when(cb.equal(any(Expression.class), any())).thenReturn(predicate);
        when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
        when(cb.lower(any(Expression.class))).thenReturn(expr);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        when(cq.where(any(Predicate.class))).thenReturn(cq);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(cb.asc(path)).thenReturn(jpaOrder);
        when(cb.desc(path)).thenReturn(jpaOrder);
        when(cq.orderBy(any(jakarta.persistence.criteria.Order.class))).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(countQ);
        when(countQ.setParameter(anyString(), any())).thenReturn(countQ);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        OrderItem oi = new OrderItem();
        dao.add(oi);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).persist(oi);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistFails_rollsBackAndClose() {
        OrderItem oi = new OrderItem();
        doThrow(RuntimeException.class).when(em).persist(oi);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.add(oi));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_returnsAndClose() {
        OrderItem oi = new OrderItem();
        when(em.find(OrderItem.class, 10)).thenReturn(oi);
        assertSame(oi, dao.getById(10));
        verify(em).close();
    }

    @Test
    void find_withNoFilters_andDefaults() {
        GetOrderItemDto dto = new GetOrderItemDto();
        dto.setPage(0);
        dto.setSize(3);
        dao.find(dto);
        verify(cb).desc(path);
        verify(cq).orderBy(jpaOrder);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(3);
        verify(em).close();
    }

    @Test
    void find_withFilters_andPaging() {
        GetOrderItemDto dto = new GetOrderItemDto();
        dto.setId(5);
        dto.setMenuItemName("Pizza");
        dto.setStatus(OrderItemStatus.READY);
        dto.setOrderId(7);
        dto.setRestaurantId(2);
        dto.setSortBy("status");
        dto.setSortDir("asc");
        dto.setPage(1);
        dto.setSize(4);
        when(query.getResultList()).thenReturn(List.of(new OrderItem()));
        List<OrderItem> result = dao.find(dto);
        verify(cb).equal(path, 5);
        verify(cb).like(expr, "%pizza%");
        verify(cb).equal(path, OrderItemStatus.READY);
        verify(cb).equal(path, 7);
        verify(cb).equal(path, 2);
        verify(cq).where(predicate);
        verify(cb).asc(path);
        verify(cq).orderBy(jpaOrder);
        verify(query).setFirstResult(4);
        verify(query).setMaxResults(4);
        assertEquals(1, result.size());
        verify(em).close();
    }

    @Test
    void find_wrapsException_andClose() {
        when(em.getCriteriaBuilder()).thenThrow(IllegalStateException.class);
        assertThrows(RuntimeException.class, () -> dao.find(new GetOrderItemDto()));
        verify(em).close();
    }

    @Test
    void update_existing_commitsAndClose() {
        OrderItem item = new OrderItem();
        item.setId(11);
        item.setQuantity(3);
        item.setCustomization("Extra");
        item.setStatus(OrderItemStatus.SERVED);
        OrderItem managed = new OrderItem();
        when(em.find(OrderItem.class, 11)).thenReturn(managed);
        dao.update(item);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(OrderItem.class, 11);
        in.verify(tx).commit();
        assertEquals(3, managed.getQuantity());
        assertEquals("Extra", managed.getCustomization());
        assertEquals(OrderItemStatus.SERVED, managed.getStatus());
        verify(em).close();
    }

    @Test
    void update_nonExisting_rollsBackAndClose() {
        OrderItem item = new OrderItem();
        item.setId(12);
        when(em.find(OrderItem.class, 12)).thenReturn(null);
        when(tx.isActive()).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> dao.update(item));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_existing_removesCommitsAndClose() {
        OrderItem oi = new OrderItem();
        when(em.find(OrderItem.class, 15)).thenReturn(oi);
        dao.delete(15);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).remove(oi);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_notExisting_commitsAndClose() {
        when(em.find(OrderItem.class, 16)).thenReturn(null);
        dao.delete(16);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_exception_rollsBackAndClose() {
        doThrow(RuntimeException.class).when(em).find(OrderItem.class, 17);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.delete(17));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void existsByOrderAndMenuItem_trueAndFalse() {
        when(countQ.getSingleResult()).thenReturn(1L, 0L);
        assertTrue(dao.existsByOrderAndMenuItem(2, 3, "cust"));
        assertFalse(dao.existsByOrderAndMenuItem(2, 3, "cust"));
        verify(countQ, times(2)).setParameter("oid", 2);
        verify(countQ, times(2)).setParameter("mid", 3);
        verify(countQ, times(2)).setParameter("cust", "cust");
        verify(em, times(2)).close();
    }
}
