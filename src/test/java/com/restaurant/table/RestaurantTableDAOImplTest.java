package com.restaurant.table;

import com.restaurant.daos.impl.RestaurantTableDAOImpl;
import com.restaurant.models.RestaurantTable;
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
class RestaurantTableDAOImplTest {

    @Mock private EntityManagerFactory emf;
    @Mock private EntityManager em;
    @Mock private EntityTransaction tx;
    @Mock private TypedQuery<RestaurantTable> typedQuery;

    @InjectMocks
    private RestaurantTableDAOImpl dao;

    private final String findAllJpql       = "SELECT t FROM RestaurantTable t";
    private final String findByNumberJpql  =
            "SELECT t FROM RestaurantTable t WHERE t.number = :number";
    private final String findByCapacityJpql =
            "SELECT t FROM RestaurantTable t WHERE t.capacity = :capacity";

    @BeforeEach
    void setUp() {
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        RestaurantTable t = new RestaurantTable();
        dao.add(t);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(t);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        RestaurantTable t = new RestaurantTable();
        doThrow(new RuntimeException("oops")).when(em).persist(t);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(t));

        verify(tx).begin();
        verify(em).persist(t);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        RestaurantTable expected = new RestaurantTable();
        when(em.find(RestaurantTable.class, 10)).thenReturn(expected);

        RestaurantTable actual = dao.getById(10);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(RestaurantTable.class, 10);
        verify(em).close();
    }

    @Test
    void findAll_shouldQueryReturnListAndClose() {
        List<RestaurantTable> list = List.of(new RestaurantTable(), new RestaurantTable());
        when(em.createQuery(findAllJpql, RestaurantTable.class))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<RestaurantTable> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, RestaurantTable.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByNumber_shouldReturnFirstAndClose() {
        int number = 5;
        RestaurantTable expected = new RestaurantTable();
        when(em.createQuery(findByNumberJpql, RestaurantTable.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("number", number)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(expected));

        RestaurantTable actual = dao.findByNumber(number);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByNumberJpql, RestaurantTable.class);
        verify(typedQuery).setParameter("number", number);
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByNumber_whenEmpty_shouldReturnNull() {
        int number = 99;
        when(em.createQuery(findByNumberJpql, RestaurantTable.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("number", number)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        assertNull(dao.findByNumber(number));
        verify(em).close();
    }

    @Test
    void findByCapacity_shouldQueryReturnListAndClose() {
        int cap = 4;
        List<RestaurantTable> list = List.of(new RestaurantTable());
        when(em.createQuery(findByCapacityJpql, RestaurantTable.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("capacity", cap)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<RestaurantTable> result = dao.findByCapacity(cap);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByCapacityJpql, RestaurantTable.class);
        verify(typedQuery).setParameter("capacity", cap);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        RestaurantTable t = new RestaurantTable();
        dao.update(t);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(t);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        RestaurantTable t = new RestaurantTable();
        doThrow(new RuntimeException("err")).when(em).merge(t);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.update(t));

        verify(tx).begin();
        verify(em).merge(t);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        int id = 7;
        RestaurantTable t = new RestaurantTable();
        when(em.find(RestaurantTable.class, id)).thenReturn(t);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(RestaurantTable.class, id);
        verify(em).remove(t);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        int id = 8;
        when(em.find(RestaurantTable.class, id)).thenReturn(null);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(RestaurantTable.class, id);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenFindThrows_shouldRollbackAndClose() {
        int id = 21;
        doThrow(new RuntimeException("boom")).when(em).find(RestaurantTable.class, id);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(id));

        verify(tx).begin();
        verify(em).find(RestaurantTable.class, id);
        verify(tx).rollback();
        verify(em).close();
    }
}