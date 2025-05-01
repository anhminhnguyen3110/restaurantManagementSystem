package com.restaurant.daos;

import com.restaurant.daos.impl.CustomerDAOImpl;
import com.restaurant.models.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerDAOImplTest {

    @Mock
    private EntityManagerFactory emf;
    @Mock
    private EntityManager em;
    @Mock
    private EntityTransaction tx;
    @Mock
    private TypedQuery<Customer> listQuery;
    @Mock
    private TypedQuery<Customer> streamQuery;

    @InjectMocks
    private CustomerDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(em.createQuery("SELECT c FROM Customer c", Customer.class)).thenReturn(listQuery);
        when(em.createQuery("SELECT c FROM Customer c WHERE c.phoneNumber = :phone", Customer.class)).thenReturn(streamQuery);
        when(listQuery.getResultList()).thenReturn(List.of());
        when(streamQuery.setParameter(anyString(), any())).thenReturn(streamQuery);
        when(streamQuery.getResultStream()).thenReturn(Stream.empty());
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Customer c = new Customer();
        dao.add(c);
        InOrder o = inOrder(emf, em, tx);
        o.verify(emf).createEntityManager();
        o.verify(em).getTransaction();
        o.verify(tx).begin();
        o.verify(em).persist(c);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        Customer c = new Customer();
        doThrow(new RuntimeException()).when(em).persist(c);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.add(c));
        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).persist(c);
        o.verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void find_shouldReturnListAndClose() {
        List<Customer> stub = List.of(new Customer(), new Customer());
        when(listQuery.getResultList()).thenReturn(stub);
        List<Customer> result = dao.find();
        assertEquals(stub, result);
        verify(emf).createEntityManager();
        verify(em).createQuery("SELECT c FROM Customer c", Customer.class);
        verify(listQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Customer c = new Customer();
        dao.update(c);
        InOrder o = inOrder(emf, em, tx);
        o.verify(emf).createEntityManager();
        o.verify(em).getTransaction();
        o.verify(tx).begin();
        o.verify(em).merge(c);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenException_shouldRollbackAndClose() {
        Customer c = new Customer();
        doThrow(new RuntimeException()).when(em).merge(c);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.update(c));
        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).merge(c);
        o.verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getByPhoneNumber_whenFound_shouldReturnCustomerAndClose() {
        Customer c = new Customer();
        when(streamQuery.getResultStream()).thenReturn(Stream.of(c, new Customer()));
        Customer result = dao.getByPhoneNumber("12345");
        assertSame(c, result);
        verify(emf).createEntityManager();
        verify(em).createQuery("SELECT c FROM Customer c WHERE c.phoneNumber = :phone", Customer.class);
        verify(streamQuery).setParameter("phone", "12345");
        verify(streamQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void getByPhoneNumber_whenNotFound_shouldReturnNullAndClose() {
        when(streamQuery.getResultStream()).thenReturn(Stream.empty());
        Customer result = dao.getByPhoneNumber("00000");
        assertNull(result);
        verify(streamQuery).setParameter("phone", "00000");
        verify(streamQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Customer c = new Customer();
        when(em.find(Customer.class, 77)).thenReturn(c);
        Customer result = dao.getById(77);
        assertSame(c, result);
        verify(emf).createEntityManager();
        verify(em).find(Customer.class, 77);
        verify(em).close();
    }
}
