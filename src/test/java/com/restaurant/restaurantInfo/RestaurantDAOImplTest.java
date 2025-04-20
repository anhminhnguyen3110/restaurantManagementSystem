package com.restaurant;

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

    private final String findAllJpql = "SELECT r FROM Restaurant r";
    private final String findByNameJpql = "SELECT r FROM Restaurant r WHERE r.name = :name";
    private final String findByAddressJpql = "SELECT r FROM Restaurant r WHERE r.address = :address";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Restaurant restaurant = new Restaurant();
        dao.add(restaurant);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(restaurant);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        Restaurant restaurant = new Restaurant();
        doThrow(new RuntimeException("persist failed")).when(em).persist(restaurant);

        assertThrows(RuntimeException.class, () -> dao.add(restaurant));

        verify(tx).begin();
        verify(em).persist(restaurant);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Restaurant expected = new Restaurant();
        when(em.find(Restaurant.class, 15)).thenReturn(expected);

        Restaurant actual = dao.getById(15);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Restaurant.class, 15);
        verify(em).close();
    }

    @Test
    void findAll_shouldReturnListAndClose() {
        List<Restaurant> list = List.of(new Restaurant(), new Restaurant());
        when(em.createQuery(findAllJpql, Restaurant.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Restaurant> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, Restaurant.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByName_shouldReturnFirstResultAndClose() {
        Restaurant first = new Restaurant();
        when(em.createQuery(findByNameJpql, Restaurant.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "Chez Pierre")).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(first));

        Restaurant actual = dao.findByName("Chez Pierre");

        assertSame(first, actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByNameJpql, Restaurant.class);
        verify(typedQuery).setParameter("name", "Chez Pierre");
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByName_whenNoResult_shouldReturnNullAndClose() {
        when(em.createQuery(findByNameJpql, Restaurant.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "Nonexistent")).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        Restaurant actual = dao.findByName("Nonexistent");

        assertNull(actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByNameJpql, Restaurant.class);
        verify(typedQuery).setParameter("name", "Nonexistent");
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByAddress_shouldReturnListAndClose() {
        List<Restaurant> list = List.of(new Restaurant());
        when(em.createQuery(findByAddressJpql, Restaurant.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("address", "123 Main St")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Restaurant> result = dao.findByAddress("123 Main St");

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByAddressJpql, Restaurant.class);
        verify(typedQuery).setParameter("address", "123 Main St");
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Restaurant restaurant = new Restaurant();
        dao.update(restaurant);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(restaurant);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        Restaurant restaurant = new Restaurant();
        when(em.find(Restaurant.class, 27)).thenReturn(restaurant);

        dao.delete(27);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Restaurant.class, 27);
        verify(em).remove(restaurant);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(Restaurant.class, 28)).thenReturn(null);

        dao.delete(28);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Restaurant.class, 28);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}