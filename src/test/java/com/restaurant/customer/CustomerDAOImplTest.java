package com.restaurant;

import com.restaurant.daos.impl.CustomerDAOImpl;
import com.restaurant.models.Customer;
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
class CustomerDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<Customer> typedQuery;

    @InjectMocks
    private CustomerDAOImpl dao;

    private final String findAllJpql = "SELECT c FROM Customer c";
    private final String findByPhoneJpql = "SELECT c FROM Customer c WHERE c.phoneNumber = :phone";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Customer customer = new Customer();
        dao.add(customer);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(customer);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        Customer customer = new Customer();
        doThrow(new RuntimeException("fail")).when(em).persist(customer);

        assertThrows(RuntimeException.class, () -> dao.add(customer));

        verify(tx).begin();
        verify(em).persist(customer);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Customer expected = new Customer();
        when(em.find(Customer.class, 123)).thenReturn(expected);

        Customer actual = dao.getById(123);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Customer.class, 123);
        verify(em).close();
    }

    @Test
    void findAll_shouldQueryReturnListAndClose() {
        List<Customer> list = List.of(new Customer(), new Customer());
        when(em.createQuery(findAllJpql, Customer.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Customer> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, Customer.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByPhoneNumber_shouldReturnFirstResultAndClose() {
        Customer expected = new Customer();
        when(em.createQuery(findByPhoneJpql, Customer.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("phone", "555-0001")).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(expected));

        Customer actual = dao.findByPhoneNumber("555-0001");

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByPhoneJpql, Customer.class);
        verify(typedQuery).setParameter("phone", "555-0001");
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByPhoneNumber_whenNoResult_shouldReturnNullAndClose() {
        when(em.createQuery(findByPhoneJpql, Customer.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("phone", "000")).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        Customer actual = dao.findByPhoneNumber("000");

        assertNull(actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByPhoneJpql, Customer.class);
        verify(typedQuery).setParameter("phone", "000");
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Customer customer = new Customer();
        dao.update(customer);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(customer);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        Customer customer = new Customer();
        when(em.find(Customer.class, 77)).thenReturn(customer);

        dao.delete(77);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Customer.class, 77);
        verify(em).remove(customer);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(Customer.class, 88)).thenReturn(null);

        dao.delete(88);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Customer.class, 88);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}