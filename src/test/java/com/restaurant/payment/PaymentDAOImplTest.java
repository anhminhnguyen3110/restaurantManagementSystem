package com.restaurant.payment;

import com.restaurant.constants.PaymentMethod;
import com.restaurant.constants.PaymentStatus;
import com.restaurant.daos.impl.PaymentDAOImpl;
import com.restaurant.models.Payment;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<Payment> typedQuery;

    @InjectMocks
    private PaymentDAOImpl dao;

    private final String findAllJpql      = "SELECT p FROM Payment p";
    private final String findByOrderJpql  = "SELECT p FROM Payment p WHERE p.order.id = :oid";
    private final String findByStatusJpql = "SELECT p FROM Payment p WHERE p.status = :st";
    private final String findByMethodJpql = "SELECT p FROM Payment p WHERE p.method = :mth";

    @BeforeEach
    void setUp() {
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Payment p = new Payment();
        dao.add(p);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(p);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        Payment p = new Payment();
        doThrow(new RuntimeException("oops")).when(em).persist(p);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(p));

        verify(tx).begin();
        verify(em).persist(p);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Payment expected = new Payment();
        when(em.find(Payment.class, 42)).thenReturn(expected);

        Payment actual = dao.getById(42);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Payment.class, 42);
        verify(em).close();
    }

    @Test
    void findAll_shouldQueryReturnListAndClose() {
        List<Payment> list = List.of(new Payment(), new Payment());
        when(em.createQuery(findAllJpql, Payment.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Payment> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, Payment.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByOrderId_shouldReturnFirstAndClose() {
        int oid = 7;
        Payment expected = new Payment();
        when(em.createQuery(findByOrderJpql, Payment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("oid", oid)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(expected));

        Payment actual = dao.findByOrderId(oid);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByOrderJpql, Payment.class);
        verify(typedQuery).setParameter("oid", oid);
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByOrderId_whenEmpty_shouldReturnNull() {
        int oid = 99;
        when(em.createQuery(findByOrderJpql, Payment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("oid", oid)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        assertNull(dao.findByOrderId(oid));

        verify(em).close();
    }

    @Test
    void findByStatus_shouldQueryReturnListAndClose() {
        PaymentStatus status = PaymentStatus.COMPLETED;
        List<Payment> list = List.of(new Payment());
        when(em.createQuery(findByStatusJpql, Payment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("st", status)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Payment> result = dao.findByStatus(status);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByStatusJpql, Payment.class);
        verify(typedQuery).setParameter("st", status);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByMethod_shouldQueryReturnListAndClose() {
        PaymentMethod method = PaymentMethod.CREDIT_CARD;
        List<Payment> list = List.of();
        when(em.createQuery(findByMethodJpql, Payment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("mth", method)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Payment> result = dao.findByMethod(method);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByMethodJpql, Payment.class);
        verify(typedQuery).setParameter("mth", method);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Payment p = new Payment();
        dao.update(p);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(p);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        Payment p = new Payment();
        doThrow(new RuntimeException("fail")).when(em).merge(p);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.update(p));

        verify(tx).begin();
        verify(em).merge(p);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        int id = 13;
        Payment p = new Payment();
        when(em.find(Payment.class, id)).thenReturn(p);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Payment.class, id);
        verify(em).remove(p);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        int id = 14;
        when(em.find(Payment.class, id)).thenReturn(null);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Payment.class, id);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenFindThrows_shouldRollbackAndClose() {
        int id = 21;
        doThrow(new RuntimeException("oops")).when(em).find(Payment.class, id);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(id));

        verify(tx).begin();
        verify(em).find(Payment.class, id);
        verify(tx).rollback();
        verify(em).close();
    }
}
