package com.restaurant.restaurantInfo;

import com.restaurant.daos.impl.RestaurantDAOImpl;
import com.restaurant.models.Restaurant;
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
class RestaurantDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<Restaurant> typedQuery;

    @InjectMocks
    private RestaurantDAOImpl dao;

    private final String findJpql = "SELECT r FROM Restaurant r";
    private final String findByNameJpql = "SELECT r FROM Restaurant r WHERE r.name = :name";
    private final String findByAddressJpql = "SELECT r FROM Restaurant r WHERE r.address = :address";

    @BeforeEach
    void setUp() {
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }
    @Test
    void add_shouldPersistCommitAndClose() {
        Restaurant r = new Restaurant();
        dao.add(r);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(r);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        Restaurant r = new Restaurant();
        doThrow(new RuntimeException("fail")).when(em).persist(r);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(r));

        verify(tx).begin();
        verify(em).persist(r);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Restaurant expected = new Restaurant();
        when(em.find(Restaurant.class, 55)).thenReturn(expected);

        Restaurant actual = dao.getById(55);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Restaurant.class, 55);
        verify(em).close();
    }

    @Test
    void find_shouldQueryReturnListAndClose() {
        List<Restaurant> list = List.of(new Restaurant(), new Restaurant());
        when(em.createQuery(findJpql, Restaurant.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Restaurant> result = dao.find();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findJpql, Restaurant.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByName_shouldReturnFirstAndClose() {
        String name = "Chez Test";
        Restaurant expected = new Restaurant();
        when(em.createQuery(findByNameJpql, Restaurant.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", name)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(expected));

        Restaurant actual = dao.findByName(name);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByNameJpql, Restaurant.class);
        verify(typedQuery).setParameter("name", name);
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByName_whenEmpty_shouldReturnNull() {
        String name = "Nope";
        when(em.createQuery(findByNameJpql, Restaurant.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", name)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        assertNull(dao.findByName(name));
        verify(em).close();
    }

    @Test
    void findByAddress_shouldQueryReturnListAndClose() {
        String addr = "123 Main St";
        List<Restaurant> list = List.of(new Restaurant());
        when(em.createQuery(findByAddressJpql, Restaurant.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("address", addr)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Restaurant> result = dao.findByAddress(addr);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByAddressJpql, Restaurant.class);
        verify(typedQuery).setParameter("address", addr);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Restaurant r = new Restaurant();
        dao.update(r);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(r);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        Restaurant r = new Restaurant();
        doThrow(new RuntimeException("err")).when(em).merge(r);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.update(r));

        verify(tx).begin();
        verify(em).merge(r);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        int id = 99;
        Restaurant r = new Restaurant();
        when(em.find(Restaurant.class, id)).thenReturn(r);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Restaurant.class, id);
        verify(em).remove(r);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        int id = 100;
        when(em.find(Restaurant.class, id)).thenReturn(null);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Restaurant.class, id);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenFindThrows_shouldRollbackAndClose() {
        int id = 42;
        doThrow(new RuntimeException("boom")).when(em).find(Restaurant.class, id);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(id));

        verify(tx).begin();
        verify(em).find(Restaurant.class, id);
        verify(tx).rollback();
        verify(em).close();
    }
}