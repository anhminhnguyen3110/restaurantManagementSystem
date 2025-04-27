package com.restaurant.shipment;

import com.restaurant.constants.ShipmentService;
import com.restaurant.constants.ShipmentStatus;
import com.restaurant.daos.impl.ShipmentDAOImpl;
import com.restaurant.models.Customer;
import com.restaurant.models.Order;
import com.restaurant.models.Shipment;
import com.restaurant.models.User;
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
class ShipmentDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<Shipment> typedQuery;

    @InjectMocks
    private ShipmentDAOImpl dao;

    private final String findJpql = "SELECT s FROM Shipment s";
    private final String findByStatusJpql = "SELECT s FROM Shipment s WHERE s.status = :status";
    private final String findByServiceTypeJpql = "SELECT s FROM Shipment s WHERE s.serviceType = :serviceType";
    private final String findByOrderJpql = "SELECT s FROM Shipment s WHERE s.order = :orderParam";
    private final String findByShipperJpql = "SELECT s FROM Shipment s WHERE s.shipper = :shipper";
    private final String findByCustomerJpql = "SELECT s FROM Shipment s WHERE s.customer = :customer";

    @BeforeEach
    void setUp() {
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Shipment s = new Shipment();
        dao.add(s);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(s);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        Shipment s = new Shipment();
        doThrow(new RuntimeException("fail")).when(em).persist(s);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(s));

        verify(tx).begin();
        verify(em).persist(s);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Shipment expected = new Shipment();
        when(em.find(Shipment.class, 123)).thenReturn(expected);

        Shipment actual = dao.getById(123);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Shipment.class, 123);
        verify(em).close();
    }

    @Test
    void find_shouldQueryReturnListAndClose() {
        List<Shipment> list = List.of(new Shipment(), new Shipment());
        when(em.createQuery(findJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.find();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findJpql, Shipment.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByStatus_shouldQueryReturnListAndClose() {
        ShipmentStatus status = ShipmentStatus.SUCCESS;
        List<Shipment> list = List.of(new Shipment());
        when(em.createQuery(findByStatusJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("status", status)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findByStatus(status);

        assertEquals(list, result);
        verify(em).createQuery(findByStatusJpql, Shipment.class);
        verify(typedQuery).setParameter("status", status);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByServiceType_shouldQueryReturnListAndClose() {
        ShipmentService svc = ShipmentService.DIDI;
        List<Shipment> list = List.of();
        when(em.createQuery(findByServiceTypeJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("serviceType", svc)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findByServiceType(svc);

        assertEquals(list, result);
        verify(em).createQuery(findByServiceTypeJpql, Shipment.class);
        verify(typedQuery).setParameter("serviceType", svc);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByOrder_shouldQueryReturnListAndClose() {
        Order order = new Order();
        List<Shipment> list = List.of(new Shipment(), new Shipment());
        when(em.createQuery(findByOrderJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("orderParam", order)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findByOrder(order);

        assertEquals(list, result);
        verify(em).createQuery(findByOrderJpql, Shipment.class);
        verify(typedQuery).setParameter("orderParam", order);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByShipper_shouldQueryReturnListAndClose() {
        User shipper = new User();
        List<Shipment> list = List.of(new Shipment());
        when(em.createQuery(findByShipperJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("shipper", shipper)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findByShipper(shipper);

        assertEquals(list, result);
        verify(em).createQuery(findByShipperJpql, Shipment.class);
        verify(typedQuery).setParameter("shipper", shipper);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByCustomer_shouldQueryReturnListAndClose() {
        Customer cust = new Customer();
        List<Shipment> list = List.of();
        when(em.createQuery(findByCustomerJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("customer", cust)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findByCustomer(cust);

        assertEquals(list, result);
        verify(em).createQuery(findByCustomerJpql, Shipment.class);
        verify(typedQuery).setParameter("customer", cust);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Shipment s = new Shipment();
        dao.update(s);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(s);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        Shipment s = new Shipment();
        doThrow(new RuntimeException("bad")).when(em).merge(s);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.update(s));

        verify(tx).begin();
        verify(em).merge(s);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        int id = 77;
        Shipment s = new Shipment();
        when(em.find(Shipment.class, id)).thenReturn(s);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Shipment.class, id);
        verify(em).remove(s);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        int id = 88;
        when(em.find(Shipment.class, id)).thenReturn(null);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Shipment.class, id);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenFindThrows_shouldRollbackAndClose() {
        int id = 99;
        doThrow(new RuntimeException("oops")).when(em).find(Shipment.class, id);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(id));

        verify(tx).begin();
        verify(em).find(Shipment.class, id);
        verify(tx).rollback();
        verify(em).close();
    }
}