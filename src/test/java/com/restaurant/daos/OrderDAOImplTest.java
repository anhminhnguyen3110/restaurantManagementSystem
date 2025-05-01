package com.restaurant.daos;

import com.restaurant.constants.OrderStatus;
import com.restaurant.constants.OrderType;
import com.restaurant.daos.impl.OrderDAOImpl;
import com.restaurant.dtos.order.GetOrderDto;
import com.restaurant.models.Order;
import com.restaurant.models.Restaurant;
import com.restaurant.models.RestaurantTable;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderDAOImplTest {

    @Mock
    EntityManagerFactory emf;
    @Mock
    EntityManager em;
    @Mock
    EntityTransaction tx;
    @Mock
    CriteriaBuilder cb;
    @Mock
    CriteriaQuery<Order> cq;
    @Mock
    Root<Order> root;
    @Mock
    Fetch<?, ?> tableFetch;
    @Mock
    Predicate predicate;
    @Mock
    TypedQuery<Order> orderQuery;
    @Mock
    TypedQuery<Long> countQuery;
    @Mock
    Path<?> path;
    @InjectMocks
    OrderDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Order.class)).thenReturn(cq);
        when(cq.from(Order.class)).thenReturn(root);
        when(root.fetch(eq("restaurantTable"), eq(JoinType.LEFT))).thenReturn((Fetch) tableFetch);
        when(root.get(anyString())).thenReturn((Path) path);
        when(cb.equal(any(Expression.class), any())).thenReturn(predicate);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(cq.orderBy(anyList())).thenReturn(cq);
        when(cb.asc(any(Path.class))).thenReturn(mock(jakarta.persistence.criteria.Order.class));
        when(cb.desc(any(Path.class))).thenReturn(mock(jakarta.persistence.criteria.Order.class));
        when(em.createQuery(cq)).thenReturn(orderQuery);
        when(orderQuery.setFirstResult(anyInt())).thenReturn(orderQuery);
        when(orderQuery.setMaxResults(anyInt())).thenReturn(orderQuery);
        when(orderQuery.getResultList()).thenReturn(new ArrayList<>());
    }

    @Test
    void add_mergesDependencies_flushesAndReturnsManaged() {
        Order o = new Order();
        Restaurant r = new Restaurant();
        RestaurantTable t = new RestaurantTable();
        o.setRestaurant(r);
        o.setRestaurantTable(t);
        when(em.merge(r)).thenReturn(r);
        when(em.merge(t)).thenReturn(t);
        when(em.merge(o)).thenReturn(o);

        Order result = dao.add(o);

        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).merge(r);
        in.verify(em).merge(t);
        in.verify(em).merge(o);
        in.verify(em).flush();
        in.verify(tx).commit();
        verify(em).close();

        assertSame(o, result);
    }

    @Test
    void add_whenMergeFails_rollsBackAndPropagates() {
        Order o = new Order();
        Restaurant r = new Restaurant();
        RestaurantTable t = new RestaurantTable();
        o.setRestaurant(r);
        o.setRestaurantTable(t);
        when(em.merge(r)).thenReturn(r);
        when(em.merge(t)).thenReturn(t);
        doThrow(RuntimeException.class).when(em).merge(o);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(o));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_findsAndCloses() {
        Order o = new Order();
        when(em.find(Order.class, 42)).thenReturn(o);
        Order result = dao.getById(42);
        assertSame(o, result);
        verify(em).close();
    }

    @Test
    void find_withFilters_pagination_andSublist() {
        GetOrderDto dto = new GetOrderDto();
        RestaurantTable tbl = mock(RestaurantTable.class);
        when(tbl.getId()).thenReturn(7);
        dto.setRestaurantTable(tbl);
        dto.setOrderType(OrderType.DELIVERY);
        dto.setTotalPrice(123.45);
        dto.setDate(LocalDate.of(2025, 5, 1));
        dto.setStatus(OrderStatus.COMPLETED);
        dto.setRestaurantId(9);
        dto.setSortBy("totalPrice");
        dto.setSortDir("desc");
        dto.setPage(2);
        dto.setSize(3);

        List<Order> fetched = List.of(new Order(), new Order(), new Order(), new Order());
        when(orderQuery.getResultList()).thenReturn(fetched);

        List<Order> result = dao.find(dto);

        verify(cb).equal(root.get("restaurantTable").get("id"), 7);
        verify(cb).equal(root.get("orderType"), OrderType.DELIVERY);
        verify(cb).equal(root.get("totalPrice"), 123.45);
        verify(cb).equal(root.get("status"), OrderStatus.COMPLETED);
        verify(cb).equal(root.get("restaurant").get("id"), 9);
        verify(cb).desc(path);
        verify(orderQuery).setFirstResult(6);
        verify(orderQuery).setMaxResults(4);
        assertEquals(3, result.size());
        verify(em).close();
    }

    @Test
    void find_withNoFilters_defaultsAndNoSublist() {
        GetOrderDto dto = new GetOrderDto();
        dto.setPage(0);
        dto.setSize(2);
        List<Order> fetched = List.of(new Order());
        when(orderQuery.getResultList()).thenReturn(fetched);

        List<Order> result = dao.find(dto);

        verify(cb).desc(path);
        verify(orderQuery).setFirstResult(0);
        verify(orderQuery).setMaxResults(3);
        assertEquals(1, result.size());
        verify(em).close();
    }

    @Test
    void find_whenCriteriaBuilderFails_wrapsException() {
        when(em.getCriteriaBuilder()).thenThrow(IllegalStateException.class);
        assertThrows(RuntimeException.class, () -> dao.find(new GetOrderDto()));
        verify(em).close();
    }

    @Test
    void update_mergesCommitsAndCloses() {
        Order o = new Order();
        dao.update(o);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).merge(o);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeFails_rollsBack() {
        Order o = new Order();
        doThrow(RuntimeException.class).when(em).merge(o);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.update(o));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_whenExists_removesCommitsAndCloses() {
        Order o = new Order();
        when(em.find(Order.class, 5)).thenReturn(o);
        dao.delete(5);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(Order.class, 5);
        in.verify(em).remove(o);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenNotExists_commitsWithoutRemove() {
        when(em.find(Order.class, 6)).thenReturn(null);
        dao.delete(6);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(Order.class, 6);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenException_rollsBack() {
        doThrow(RuntimeException.class).when(em).find(Order.class, 8);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.delete(8));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void hasPendingForTableAndType_trueAndFalse() {
        TypedQuery<Long> q = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(q);
        when(q.setParameter("tid", 3)).thenReturn(q);
        when(q.setParameter("type", OrderType.DINE_IN)).thenReturn(q);
        when(q.setParameter("status", OrderStatus.PENDING)).thenReturn(q);
        when(q.getSingleResult()).thenReturn(5L);
        assertTrue(dao.hasPendingForTableAndType(3, OrderType.DINE_IN));
        when(q.getSingleResult()).thenReturn(0L);
        assertFalse(dao.hasPendingForTableAndType(3, OrderType.DINE_IN));
        verify(em, times(2)).close();
    }
}
