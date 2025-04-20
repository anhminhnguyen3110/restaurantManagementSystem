package com.restaurant;

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

    private final String findAllJpql = "SELECT s FROM Shipment s";
    private final String findByStatusJpql = "SELECT s FROM Shipment s WHERE s.status = :status";
    private final String findByServiceTypeJpql = "SELECT s FROM Shipment s WHERE s.serviceType = :serviceType";
    private final String findByOrderJpql = "SELECT s FROM Shipment s WHERE s.order = :orderParam";
    private final String findByShipperJpql = "SELECT s FROM Shipment s WHERE s.shipper = :shipper";
    private final String findByCustomerJpql = "SELECT s FROM Shipment s WHERE s.customer = :customer";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Shipment shipment = new Shipment();
        dao.add(shipment);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(shipment);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        Shipment shipment = new Shipment();
        doThrow(new RuntimeException("persist failed")).when(em).persist(shipment);

        assertThrows(RuntimeException.class, () -> dao.add(shipment));

        verify(tx).begin();
        verify(em).persist(shipment);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Shipment expected = new Shipment();
        when(em.find(Shipment.class, 42)).thenReturn(expected);

        Shipment actual = dao.getById(42);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Shipment.class, 42);
        verify(em).close();
    }

    @Test
    void findAll_shouldReturnListAndClose() {
        List<Shipment> list = List.of(new Shipment(), new Shipment());
        when(em.createQuery(findAllJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, Shipment.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByStatus_shouldReturnListAndClose() {
        List<Shipment> list = List.of(new Shipment());
        ShipmentStatus status = ShipmentStatus.SHIPPING;
        when(em.createQuery(findByStatusJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("status", status)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findByStatus(status);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByStatusJpql, Shipment.class);
        verify(typedQuery).setParameter("status", status);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByServiceType_shouldReturnListAndClose() {
        List<Shipment> list = List.of(new Shipment());
        ShipmentService service = ShipmentService.GRAB;
        when(em.createQuery(findByServiceTypeJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("serviceType", service)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findByServiceType(service);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByServiceTypeJpql, Shipment.class);
        verify(typedQuery).setParameter("serviceType", service);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByOrder_shouldReturnListAndClose() {
        List<Shipment> list = List.of(new Shipment());
        Order order = new Order();
        when(em.createQuery(findByOrderJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("orderParam", order)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findByOrder(order);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByOrderJpql, Shipment.class);
        verify(typedQuery).setParameter("orderParam", order);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByShipper_shouldReturnListAndClose() {
        List<Shipment> list = List.of(new Shipment());
        User shipper = new User();
        when(em.createQuery(findByShipperJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("shipper", shipper)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findByShipper(shipper);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByShipperJpql, Shipment.class);
        verify(typedQuery).setParameter("shipper", shipper);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByCustomer_shouldReturnListAndClose() {
        List<Shipment> list = List.of(new Shipment());
        Customer customer = new Customer();
        when(em.createQuery(findByCustomerJpql, Shipment.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("customer", customer)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Shipment> result = dao.findByCustomer(customer);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByCustomerJpql, Shipment.class);
        verify(typedQuery).setParameter("customer", customer);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Shipment shipment = new Shipment();
        dao.update(shipment);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(shipment);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        Shipment shipment = new Shipment();
        when(em.find(Shipment.class, 99)).thenReturn(shipment);

        dao.delete(99);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Shipment.class, 99);
        verify(em).remove(shipment);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(Shipment.class, 100)).thenReturn(null);

        dao.delete(100);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Shipment.class, 100);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}