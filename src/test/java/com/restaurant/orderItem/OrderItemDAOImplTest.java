package com.restaurant.orderItem;

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
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
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
    void add_whenPersistThrows_shouldRollbackAndClose() {
        OrderItem item = new OrderItem();
        doThrow(new RuntimeException("oops")).when(em).persist(item);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(item));

        verify(tx).begin();
        verify(em).persist(item);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        OrderItem expected = new OrderItem();
        when(em.find(OrderItem.class, 5)).thenReturn(expected);

        OrderItem actual = dao.getById(5);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(OrderItem.class, 5);
        verify(em).close();
    }

    @Test
    void findAll_shouldQueryReturnListAndClose() {
        List<OrderItem> list = List.of(new OrderItem());
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
    void findByOrder_shouldQueryReturnListAndClose() {
        int oid = 7;
        List<OrderItem> list = List.of(new OrderItem(), new OrderItem());
        when(em.createQuery(findByOrderJpql, OrderItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("oid", oid)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<OrderItem> result = dao.findByOrder(oid);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByOrderJpql, OrderItem.class);
        verify(typedQuery).setParameter("oid", oid);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByMenuItem_shouldQueryReturnListAndClose() {
        int mid = 9;
        List<OrderItem> list = List.of();
        when(em.createQuery(findByMenuItemJpql, OrderItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("mid", mid)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<OrderItem> result = dao.findByMenuItem(mid);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByMenuItemJpql, OrderItem.class);
        verify(typedQuery).setParameter("mid", mid);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByStatus_shouldQueryReturnListAndClose() {
        OrderItemStatus status = OrderItemStatus.PENDING;
        List<OrderItem> list = List.of(new OrderItem());
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
    void update_whenMergeThrows_shouldRollbackAndClose() {
        OrderItem item = new OrderItem();
        doThrow(new RuntimeException("fail")).when(em).merge(item);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.update(item));

        verify(tx).begin();
        verify(em).merge(item);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        int id = 13;
        OrderItem item = new OrderItem();
        when(em.find(OrderItem.class, id)).thenReturn(item);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(OrderItem.class, id);
        verify(em).remove(item);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        int id = 14;
        when(em.find(OrderItem.class, id)).thenReturn(null);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(OrderItem.class, id);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenFindThrows_shouldRollbackAndClose() {
        int id = 21;
        doThrow(new RuntimeException("oops")).when(em).find(OrderItem.class, id);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(id));

        verify(tx).begin();
        verify(em).find(OrderItem.class, id);
        verify(tx).rollback();
        verify(em).close();
    }
}