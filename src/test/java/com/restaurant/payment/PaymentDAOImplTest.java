package com.restaurant;

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

    private final String findAllJpql = "SELECT p FROM Payment p";
    private final String findByOrderIdJpql = "SELECT p FROM Payment p WHERE p.order.id = :oid";
    private final String findByStatusJpql = "SELECT p FROM Payment p WHERE p.status = :st";
    private final String findByMethodJpql = "SELECT p FROM Payment p WHERE p.method = :mth";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Payment payment = new Payment();
        dao.add(payment);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(payment);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        Payment payment = new Payment();
        doThrow(new RuntimeException("persist failed")).when(em).persist(payment);

        assertThrows(RuntimeException.class, () -> dao.add(payment));

        verify(tx).begin();
        verify(em).persist(payment);
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
    void findAll_shouldReturnListAndClose() {
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
    void findByOrderId_shouldReturnFirstResultAndClose() {
        int orderId = 7;
        Payment expected = new Payment();
        when(em.createQuery(findByOrderIdJpql, Payment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("oid", orderId)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(expected));

        Payment result = dao.findByOrderId(orderId);

        assertSame(expected, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByOrderIdJpql, Payment.class);
        verify(typedQuery).setParameter("oid", orderId);
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByOrderId_whenNoResult_shouldReturnNullAndClose() {
        int orderId = 8;
        when(em.createQuery(findByOrderIdJpql, Payment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("oid", orderId)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        Payment result = dao.findByOrderId(orderId);

        assertNull(result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByOrderIdJpql, Payment.class);
        verify(typedQuery).setParameter("oid", orderId);
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByStatus_shouldReturnListAndClose() {
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
    void findByMethod_shouldReturnListAndClose() {
        PaymentMethod method = PaymentMethod.CREDIT_CARD;
        List<Payment> list = List.of(new Payment());
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
        Payment payment = new Payment();
        dao.update(payment);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(payment);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        Payment payment = new Payment();
        when(em.find(Payment.class, 99)).thenReturn(payment);

        dao.delete(99);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Payment.class, 99);
        verify(em).remove(payment);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(Payment.class, 100)).thenReturn(null);

        dao.delete(100);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Payment.class, 100);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}
