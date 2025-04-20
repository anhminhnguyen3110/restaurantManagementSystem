package com.restaurant;

import com.restaurant.constants.OrderItemStatus;
import com.restaurant.daos.impl.OrderItemDAOImpl;
import com.restaurant.models.OrderItem;
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
class OrderItemDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<OrderItem> typedQuery;

    @InjectMocks
    private OrderItemDAOImpl dao;

    private final String findAllJpql = "SELECT oi FROM OrderItem oi";
    private final String findByOrderJpql = "SELECT oi FROM OrderItem oi WHERE oi.order.id = :oid";
    private final String findByMenuItemJpql = "SELECT oi FROM OrderItem oi WHERE oi.menuItem.id = :mid";
    private final String findByStatusJpql = "SELECT oi FROM OrderItem oi WHERE oi.status = :st";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        OrderItem item = new OrderItem();
        dao.add(item);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(item);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        OrderItem item = new OrderItem();
        doThrow(new RuntimeException("persist failed")).when(em).persist(item);

        assertThrows(RuntimeException.class, () -> dao.add(item));

        verify(tx).begin();
        verify(em).persist(item);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        OrderItem expected = new OrderItem();
        when(em.find(OrderItem.class, 10)).thenReturn(expected);

        OrderItem actual = dao.getById(10);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(OrderItem.class, 10);
        verify(em).close();
    }

    @Test
    void findAll_shouldReturnListAndClose() {
        List<OrderItem> list = List.of(new OrderItem(), new OrderItem());
        when(em.createQuery(findAllJpql, OrderItem.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<OrderItem> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, OrderItem.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByOrder_shouldReturnListAndClose() {
        List<OrderItem> list = List.of(new OrderItem());
        when(em.createQuery(findByOrderJpql, OrderItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("oid", 5)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<OrderItem> result = dao.findByOrder(5);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByOrderJpql, OrderItem.class);
        verify(typedQuery).setParameter("oid", 5);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByMenuItem_shouldReturnListAndClose() {
        List<OrderItem> list = List.of(new OrderItem());
        when(em.createQuery(findByMenuItemJpql, OrderItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("mid", 7)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<OrderItem> result = dao.findByMenuItem(7);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByMenuItemJpql, OrderItem.class);
        verify(typedQuery).setParameter("mid", 7);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByStatus_shouldReturnListAndClose() {
        List<OrderItem> list = List.of(new OrderItem());
        OrderItemStatus status = OrderItemStatus.PENDING;
        when(em.createQuery(findByStatusJpql, OrderItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("st", status)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<OrderItem> result = dao.findByStatus(status);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByStatusJpql, OrderItem.class);
        verify(typedQuery).setParameter("st", status);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        OrderItem item = new OrderItem();
        dao.update(item);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(item);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        OrderItem item = new OrderItem();
        when(em.find(OrderItem.class, 3)).thenReturn(item);

        dao.delete(3);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(OrderItem.class, 3);
        verify(em).remove(item);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(OrderItem.class, 4)).thenReturn(null);

        dao.delete(4);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(OrderItem.class, 4);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}