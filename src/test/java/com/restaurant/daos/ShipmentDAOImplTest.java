package com.restaurant.daos;

import com.restaurant.constants.ShipmentService;
import com.restaurant.constants.ShipmentStatus;
import com.restaurant.daos.impl.ShipmentDAOImpl;
import com.restaurant.dtos.shipment.GetShipmentDto;
import com.restaurant.models.Shipment;
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
class ShipmentDAOImplTest {

    @Mock
    EntityManagerFactory emf;
    @Mock
    EntityManager em;
    @Mock
    EntityTransaction tx;
    @Mock
    CriteriaBuilder cb;
    @Mock
    CriteriaQuery<Shipment> cq;
    @Mock
    Root<Shipment> root;
    @Mock
    Fetch<Shipment, ?> fetchOrder;
    @Mock
    Fetch<Shipment, ?> fetchShipper;
    @Mock
    Fetch<Shipment, ?> fetchCustomer;
    @Mock
    Path<?> path;
    @Mock
    Expression<String> expr;
    @Mock
    Predicate predicate;
    @Mock
    TypedQuery<Shipment> query;
    @Mock
    TypedQuery<Long> countQ;
    @InjectMocks
    ShipmentDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Shipment.class)).thenReturn(cq);
        when(cq.from(Shipment.class)).thenReturn(root);
        when(root.fetch("order", JoinType.LEFT)).thenReturn((Fetch) fetchOrder);
        when(root.fetch("shipper", JoinType.LEFT)).thenReturn((Fetch) fetchShipper);
        when(root.fetch("customer", JoinType.LEFT)).thenReturn((Fetch) fetchCustomer);
        when(root.get(anyString())).thenReturn((Path) path);
        when(path.get(anyString())).thenReturn((Path) path);
        when(cb.equal(any(Expression.class), any())).thenReturn(predicate);
        when(cb.lower(any(Expression.class))).thenReturn(expr);
        when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
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
        Shipment s = new Shipment();
        dao.add(s);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).persist(s);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistFails_rollsBackAndClose() {
        Shipment s = new Shipment();
        doThrow(RuntimeException.class).when(em).persist(s);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.add(s));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_returnsAndClose() {
        Shipment s = new Shipment();
        when(em.find(Shipment.class, 42)).thenReturn(s);
        Shipment result = dao.getById(42);
        assertSame(s, result);
        verify(em).close();
    }

    @Test
    void find_withNoFilters_andDefaults() {
        GetShipmentDto dto = new GetShipmentDto();
        dto.setPage(0);
        dto.setSize(5);
        dao.find(dto);
        verify(root).fetch("order", JoinType.LEFT);
        verify(root).fetch("shipper", JoinType.LEFT);
        verify(root).fetch("customer", JoinType.LEFT);
        verify(cq, never()).where(any(Predicate[].class));
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(5);
        verify(em).close();
    }

    @Test
    void find_withFilters_andPaging() {
        GetShipmentDto dto = new GetShipmentDto();
        dto.setServiceType(ShipmentService.INTERNAL);
        dto.setOrderId(7);
        dto.setShipperName("FastShip");
        dto.setCustomerName("Alice");
        dto.setStatus(ShipmentStatus.SHIPPING);
        dto.setTrackingNumber("XYZ123");
        dto.setPage(1);
        dto.setSize(3);

        when(query.getResultList()).thenReturn(List.of(new Shipment()));
        List<Shipment> result = dao.find(dto);

        verify(cb).equal(any(Expression.class), eq(dto.getServiceType()));
        verify(cb).equal(any(Expression.class), eq(dto.getOrderId()));
        verify(cb, times(3)).lower(any(Expression.class));
        verify(cb, times(3)).like(any(Expression.class), anyString());
        verify(cb).equal(any(Expression.class), eq(dto.getStatus()));
        verify(cq).where(any(Predicate[].class));
        verify(query).setFirstResult(3);
        verify(query).setMaxResults(3);
        assertEquals(1, result.size());
        verify(em).close();
    }

    @Test
    void find_wrapsException_andClose() {
        when(em.getCriteriaBuilder()).thenThrow(IllegalStateException.class);
        assertThrows(RuntimeException.class, () -> dao.find(new GetShipmentDto()));
        verify(em).close();
    }

    @Test
    void update_mergesCommitAndClose() {
        Shipment s = new Shipment();
        dao.update(s);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).merge(s);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeFails_rollsBackAndClose() {
        Shipment s = new Shipment();
        doThrow(RuntimeException.class).when(em).merge(s);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.update(s));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_existing_removesCommitAndClose() {
        Shipment s = new Shipment();
        when(em.find(Shipment.class, 15)).thenReturn(s);
        dao.delete(15);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(Shipment.class, 15);
        in.verify(em).remove(s);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_nonExisting_commitsAndClose() {
        when(em.find(Shipment.class, 16)).thenReturn(null);
        dao.delete(16);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(Shipment.class, 16);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_exception_rollsBackAndClose() {
        doThrow(RuntimeException.class).when(em).find(Shipment.class, 17);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.delete(17));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void existsPendingByOrder_trueAndFalse() {
        when(countQ.getSingleResult()).thenReturn(1L, 0L);
        assertTrue(dao.existsPendingByOrder(9));
        assertFalse(dao.existsPendingByOrder(9));
        verify(countQ, times(2)).setParameter("oid", 9);
        verify(countQ, times(2)).setParameter("st", ShipmentStatus.SHIPPING);
        verify(em, times(2)).close();
    }
}
