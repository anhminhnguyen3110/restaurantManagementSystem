package com.restaurant.daos;

import com.restaurant.constants.PaymentMethod;
import com.restaurant.constants.PaymentStatus;
import com.restaurant.daos.impl.PaymentDAOImpl;
import com.restaurant.dtos.payment.GetPaymentDto;
import com.restaurant.models.Payment;
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
class PaymentDAOImplTest {

    @Mock
    EntityManagerFactory emf;
    @Mock
    EntityManager em;
    @Mock
    EntityTransaction tx;
    @Mock
    CriteriaBuilder cb;
    @Mock
    CriteriaQuery<Payment> cq;
    @Mock
    Root<Payment> root;
    @Mock
    Fetch<Object, Object> fetchOrder;
    @Mock
    Path<Object> path;
    @Mock
    Predicate predicate;
    @Mock
    TypedQuery<Payment> query;
    @Mock
    TypedQuery<Long> countQ;
    @InjectMocks
    PaymentDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Payment.class)).thenReturn(cq);
        when(cq.from(Payment.class)).thenReturn(root);
        when(root.fetch(eq("order"), eq(JoinType.LEFT))).thenReturn(fetchOrder);
        when(root.get(anyString())).thenReturn(path);
        when(path.get(anyString())).thenReturn(path);
        when(cb.equal(any(Expression.class), any())).thenReturn(predicate);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(countQ);
        when(countQ.setParameter(anyString(), any())).thenReturn(countQ);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Payment p = new Payment();
        dao.add(p);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).persist(p);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistFails_rollsBackAndCloses() {
        Payment p = new Payment();
        doThrow(RuntimeException.class).when(em).persist(p);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.add(p));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_returnsAndCloses() {
        Payment p = new Payment();
        when(em.find(Payment.class, 5)).thenReturn(p);
        Payment result = dao.getById(5);
        assertSame(p, result);
        verify(em).close();
    }

    @Test
    void find_withNoFilters_andDefaults() {
        GetPaymentDto dto = new GetPaymentDto();
        dto.setPage(0);
        dto.setSize(3);
        List<Payment> result = dao.find(dto);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(3);
        assertTrue(result.isEmpty());
        verify(em).close();
    }

    @Test
    void find_withFilters_andPaging() {
        GetPaymentDto dto = new GetPaymentDto();
        dto.setOrderId(2);
        dto.setMethod(PaymentMethod.CASH);
        dto.setStatus(PaymentStatus.PENDING);
        dto.setPage(1);
        dto.setSize(4);
        Payment p = new Payment();
        when(query.getResultList()).thenReturn(List.of(p));
        List<Payment> result = dao.find(dto);
        verify(cb).equal(root.get("order").get("id"), 2);
        verify(cb).equal(root.get("method"), PaymentMethod.CASH);
        verify(cb).equal(root.get("status"), PaymentStatus.PENDING);
        verify(cq).where(any(Predicate[].class));
        verify(query).setFirstResult(4);
        verify(query).setMaxResults(4);
        assertEquals(1, result.size());
        verify(em).close();
    }

    @Test
    void find_wrapsException_andCloses() {
        when(em.getCriteriaBuilder()).thenThrow(IllegalStateException.class);
        assertThrows(RuntimeException.class, () -> dao.find(new GetPaymentDto()));
        verify(em).close();
    }

    @Test
    void existsByOrder_trueAndFalse() {
        when(countQ.getSingleResult()).thenReturn(1L);
        assertTrue(dao.existsByOrder(7));
        when(countQ.getSingleResult()).thenReturn(0L);
        assertFalse(dao.existsByOrder(7));
        verify(em, times(2)).close();
    }
}
