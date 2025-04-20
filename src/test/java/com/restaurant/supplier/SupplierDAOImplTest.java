package com.restaurant;

import com.restaurant.daos.impl.SupplierDAOImpl;
import com.restaurant.models.Supplier;
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
class SupplierDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<Supplier> typedQuery;

    @InjectMocks
    private SupplierDAOImpl dao;

    private final String findAllJpql = "SELECT s FROM Supplier s";
    private final String findByNameJpql = "SELECT s FROM Supplier s WHERE s.name = :name";
    private final String findByAddressJpql = "SELECT s FROM Supplier s WHERE s.address = :address";
    private final String findByEmailJpql = "SELECT s FROM Supplier s WHERE s.email = :email";
    private final String findByPhoneJpql = "SELECT s FROM Supplier s WHERE s.phone = :phone";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Supplier supplier = new Supplier();
        dao.add(supplier);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(supplier);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        Supplier supplier = new Supplier();
        doThrow(new RuntimeException("persist failed")).when(em).persist(supplier);

        assertThrows(RuntimeException.class, () -> dao.add(supplier));

        verify(tx).begin();
        verify(em).persist(supplier);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Supplier expected = new Supplier();
        when(em.find(Supplier.class, 7)).thenReturn(expected);

        Supplier actual = dao.getById(7);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Supplier.class, 7);
        verify(em).close();
    }

    @Test
    void findAll_shouldReturnListAndClose() {
        List<Supplier> list = List.of(new Supplier(), new Supplier());
        when(em.createQuery(findAllJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Supplier> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, Supplier.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByName_shouldReturnFirstResultAndClose() {
        Supplier first = new Supplier();
        when(em.createQuery(findByNameJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "Acme")).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(first));

        Supplier actual = dao.findByName("Acme");

        assertSame(first, actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByNameJpql, Supplier.class);
        verify(typedQuery).setParameter("name", "Acme");
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByName_whenNoResult_shouldReturnNullAndClose() {
        when(em.createQuery(findByNameJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "None")).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        Supplier actual = dao.findByName("None");

        assertNull(actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByNameJpql, Supplier.class);
        verify(typedQuery).setParameter("name", "None");
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByAddress_shouldReturnListAndClose() {
        List<Supplier> list = List.of(new Supplier());
        when(em.createQuery(findByAddressJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("address", "123 St")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Supplier> result = dao.findByAddress("123 St");

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByAddressJpql, Supplier.class);
        verify(typedQuery).setParameter("address", "123 St");
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByEmail_shouldReturnListAndClose() {
        List<Supplier> list = List.of(new Supplier());
        when(em.createQuery(findByEmailJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("email", "a@b.com")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Supplier> result = dao.findByEmail("a@b.com");

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByEmailJpql, Supplier.class);
        verify(typedQuery).setParameter("email", "a@b.com");
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByPhone_shouldReturnListAndClose() {
        List<Supplier> list = List.of(new Supplier());
        when(em.createQuery(findByPhoneJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("phone", "555-0002")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Supplier> result = dao.findByPhone("555-0002");

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByPhoneJpql, Supplier.class);
        verify(typedQuery).setParameter("phone", "555-0002");
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Supplier supplier = new Supplier();
        dao.update(supplier);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(supplier);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        Supplier supplier = new Supplier();
        when(em.find(Supplier.class, 55)).thenReturn(supplier);

        dao.delete(55);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Supplier.class, 55);
        verify(em).remove(supplier);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(Supplier.class, 66)).thenReturn(null);

        dao.delete(66);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Supplier.class, 66);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}