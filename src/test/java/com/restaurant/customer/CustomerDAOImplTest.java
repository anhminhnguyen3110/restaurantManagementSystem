package com.restaurant.customer;

import com.restaurant.daos.impl.CustomerDAOImpl;
import com.restaurant.models.Customer;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose_withoutRollback() {
        Customer cust = new Customer();

        dao.add(cust);

        InOrder ord = inOrder(emf, em, tx);
        ord.verify(emf).createEntityManager();
        ord.verify(em).getTransaction();
        ord.verify(tx).begin();
        ord.verify(em).persist(cust);
        ord.verify(tx).commit();
        verify(tx, never()).rollback();
        ord.verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        Customer cust = new Customer();
        doThrow(new RuntimeException("persist failed")).when(em).persist(cust);
        when(tx.isActive()).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.add(cust));
        assertEquals("persist failed", ex.getMessage());

        verify(tx).begin();
        verify(em).persist(cust);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Customer expected = new Customer();
        when(em.find(Customer.class, 7)).thenReturn(expected);

        Customer actual = dao.getById(7);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Customer.class, 7);
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
    void findByPhoneNumber_whenFound_shouldReturnFirstAndClose() {
        Customer c1 = new Customer();
        Customer c2 = new Customer();
        when(em.createQuery(findByPhoneJpql, Customer.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("phone", "123")).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(c1, c2));

        Customer result = dao.findByPhoneNumber("123");

        assertSame(c1, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByPhoneJpql, Customer.class);
        verify(typedQuery).setParameter("phone", "123");
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByPhoneNumber_whenNotFound_shouldReturnNullAndClose() {
        when(em.createQuery(findByPhoneJpql, Customer.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("phone", "999")).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        Customer result = dao.findByPhoneNumber("999");

        assertNull(result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByPhoneJpql, Customer.class);
        verify(typedQuery).setParameter("phone", "999");
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose_withoutRollback() {
        Customer cust = new Customer();

        dao.update(cust);

        InOrder ord = inOrder(emf, em, tx);
        ord.verify(emf).createEntityManager();
        ord.verify(em).getTransaction();
        ord.verify(tx).begin();
        ord.verify(em).merge(cust);
        ord.verify(tx).commit();
        verify(tx, never()).rollback();
        ord.verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        Customer cust = new Customer();
        doThrow(new PersistenceException("merge failed")).when(em).merge(cust);
        when(tx.isActive()).thenReturn(true);

        PersistenceException ex = assertThrows(PersistenceException.class, () -> dao.update(cust));
        assertEquals("merge failed", ex.getMessage());

        verify(tx).begin();
        verify(em).merge(cust);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFound_commitAndClose() {
        Customer cust = new Customer();
        when(em.find(Customer.class, 55)).thenReturn(cust);

        dao.delete(55);

        InOrder ord = inOrder(emf, em, tx);
        ord.verify(emf).createEntityManager();
        ord.verify(em).getTransaction();
        ord.verify(tx).begin();
        ord.verify(em).find(Customer.class, 55);
        ord.verify(em).remove(cust);
        ord.verify(tx).commit();
        verify(tx, never()).rollback();
        ord.verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFound_butStillCommitAndClose() {
        when(em.find(Customer.class, 66)).thenReturn(null);

        dao.delete(66);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Customer.class, 66);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(tx, never()).rollback();
        verify(em).close();
    }
}