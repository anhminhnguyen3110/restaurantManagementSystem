package com.restaurant;

import com.restaurant.constants.OrderType;
import com.restaurant.daos.impl.OrderDAOImpl;
import com.restaurant.models.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<Order> typedQuery;

    @InjectMocks
    private OrderDAOImpl dao;

    private final String findAllJpql = "SELECT o FROM Order o";
    private final String findByStatusJpql = "SELECT o FROM Order o WHERE o.status = :status";
    private final String findByTypeJpql = "SELECT o FROM Order o WHERE o.orderType = :type";
    private final String findByTableJpql = "SELECT o FROM Order o WHERE o.restaurantTable.id = :tid";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Order order = new Order();
        dao.add(order);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(order);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        Order order = new Order();
        doThrow(new RuntimeException("persist failed")).when(em).persist(order);

        assertThrows(RuntimeException.class, () -> dao.add(order));

        verify(tx).begin();
        verify(em).persist(order);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Order expected = new Order();
        when(em.find(Order.class, 10)).thenReturn(expected);

        Order actual = dao.getById(10);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Order.class, 10);
        verify(em).close();
    }

    @Test
    void findAll_shouldReturnListAndClose() {
        List<Order> list = List.of(new Order(), new Order());
        when(em.createQuery(findAllJpql, Order.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Order> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, Order.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByStatus_shouldReturnListAndClose() {
        List<Order> list = List.of(new Order());
        String status = "completed";
        when(em.createQuery(findByStatusJpql, Order.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("status", status)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Order> result = dao.findByStatus(status);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByStatusJpql, Order.class);
        verify(typedQuery).setParameter("status", status);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByType_shouldReturnListAndClose() {
        List<Order> list = List.of(new Order());
        OrderType type = OrderType.DELIVERY;
        when(em.createQuery(findByTypeJpql, Order.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("type", type)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Order> result = dao.findByType(type);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByTypeJpql, Order.class);
        verify(typedQuery).setParameter("type", type);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByTable_shouldReturnListAndClose() {
        List<Order> list = List.of(new Order());
        int tableId = 5;
        when(em.createQuery(findByTableJpql, Order.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("tid", tableId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Order> result = dao.findByTable(tableId);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByTableJpql, Order.class);
        verify(typedQuery).setParameter("tid", tableId);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Order order = new Order();
        dao.update(order);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(order);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        Order order = new Order();
        doThrow(new RuntimeException("merge failed")).when(em).merge(order);

        assertThrows(RuntimeException.class, () -> dao.update(order));

        verify(tx).begin();
        verify(em).merge(order);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        Order order = new Order();
        when(em.find(Order.class, 20)).thenReturn(order);

        dao.delete(20);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Order.class, 20);
        verify(em).remove(order);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(Order.class, 30)).thenReturn(null);

        dao.delete(30);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Order.class, 30);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}