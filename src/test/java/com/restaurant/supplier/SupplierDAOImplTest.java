package com.restaurant.supplier;

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
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Supplier s = new Supplier();
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
        Supplier s = new Supplier();
        doThrow(new RuntimeException("oops")).when(em).persist(s);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(s));

        verify(tx).begin();
        verify(em).persist(s);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Supplier expected = new Supplier();
        when(em.find(Supplier.class, 21)).thenReturn(expected);

        Supplier actual = dao.getById(21);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Supplier.class, 21);
        verify(em).close();
    }

    @Test
    void findAll_shouldQueryReturnListAndClose() {
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
    void findByName_shouldReturnFirstAndClose() {
        String name = "Acme";
        Supplier expected = new Supplier();
        when(em.createQuery(findByNameJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", name)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(expected));

        Supplier actual = dao.findByName(name);

        assertSame(expected, actual);
        verify(em).createQuery(findByNameJpql, Supplier.class);
        verify(typedQuery).setParameter("name", name);
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByName_whenEmpty_shouldReturnNull() {
        String name = "Unknown";
        when(em.createQuery(findByNameJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", name)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        assertNull(dao.findByName(name));
        verify(em).close();
    }

    @Test
    void findByAddress_shouldQueryReturnListAndClose() {
        String addr = "123 Rd";
        List<Supplier> list = List.of(new Supplier());
        when(em.createQuery(findByAddressJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("address", addr)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Supplier> result = dao.findByAddress(addr);

        assertEquals(list, result);
        verify(em).createQuery(findByAddressJpql, Supplier.class);
        verify(typedQuery).setParameter("address", addr);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByEmail_shouldQueryReturnListAndClose() {
        String email = "a@b.com";
        List<Supplier> list = List.of();
        when(em.createQuery(findByEmailJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("email", email)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Supplier> result = dao.findByEmail(email);

        assertEquals(list, result);
        verify(em).createQuery(findByEmailJpql, Supplier.class);
        verify(typedQuery).setParameter("email", email);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByPhone_shouldQueryReturnListAndClose() {
        String phone = "555";
        List<Supplier> list = List.of(new Supplier());
        when(em.createQuery(findByPhoneJpql, Supplier.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("phone", phone)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Supplier> result = dao.findByPhone(phone);

        assertEquals(list, result);
        verify(em).createQuery(findByPhoneJpql, Supplier.class);
        verify(typedQuery).setParameter("phone", phone);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Supplier s = new Supplier();
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
        Supplier s = new Supplier();
        doThrow(new RuntimeException("err")).when(em).merge(s);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.update(s));

        verify(tx).begin();
        verify(em).merge(s);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        int id = 7;
        Supplier s = new Supplier();
        when(em.find(Supplier.class, id)).thenReturn(s);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Supplier.class, id);
        verify(em).remove(s);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        int id = 8;
        when(em.find(Supplier.class, id)).thenReturn(null);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Supplier.class, id);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenFindThrows_shouldRollbackAndClose() {
        int id = 9;
        doThrow(new RuntimeException("boom")).when(em).find(Supplier.class, id);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(id));

        verify(tx).begin();
        verify(em).find(Supplier.class, id);
        verify(tx).rollback();
        verify(em).close();
    }
}