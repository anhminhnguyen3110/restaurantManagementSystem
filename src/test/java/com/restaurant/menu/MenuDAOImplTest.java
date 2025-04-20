package com.restaurant;

import com.restaurant.daos.impl.MenuDAOImpl;
import com.restaurant.models.Menu;
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
class MenuDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<Menu> typedQuery;

    @InjectMocks
    private MenuDAOImpl dao;

    private final String findAllJpql = "SELECT m FROM Menu m";
    private final String findByRestaurantJpql = "SELECT m FROM Menu m WHERE m.restaurant.id = :rid";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Menu menu = new Menu();
        dao.add(menu);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(menu);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        Menu menu = new Menu();
        doThrow(new RuntimeException("fail")).when(em).persist(menu);

        assertThrows(RuntimeException.class, () -> dao.add(menu));

        verify(tx).begin();
        verify(em).persist(menu);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Menu expected = new Menu();
        when(em.find(Menu.class, 5)).thenReturn(expected);

        Menu actual = dao.getById(5);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Menu.class, 5);
        verify(em).close();
    }

    @Test
    void findAll_shouldReturnListAndClose() {
        List<Menu> list = List.of(new Menu(), new Menu());
        when(em.createQuery(findAllJpql, Menu.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Menu> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, Menu.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByRestaurant_shouldReturnListAndClose() {
        List<Menu> list = List.of(new Menu());
        when(em.createQuery(findByRestaurantJpql, Menu.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("rid", 7)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Menu> result = dao.findByRestaurant(7);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByRestaurantJpql, Menu.class);
        verify(typedQuery).setParameter("rid", 7);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Menu menu = new Menu();
        dao.update(menu);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(menu);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        Menu menu = new Menu();
        when(em.find(Menu.class, 3)).thenReturn(menu);

        dao.delete(3);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Menu.class, 3);
        verify(em).remove(menu);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(Menu.class, 4)).thenReturn(null);

        dao.delete(4);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Menu.class, 4);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}