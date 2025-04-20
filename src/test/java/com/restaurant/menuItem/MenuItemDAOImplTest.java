package com.restaurant;

import com.restaurant.daos.impl.MenuItemDAOImpl;
import com.restaurant.models.MenuItem;
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
class MenuItemDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<MenuItem> typedQuery;

    @InjectMocks
    private MenuItemDAOImpl dao;

    private final String findAllJpql = "SELECT m FROM MenuItem m";
    private final String findByNameJpql = "SELECT m FROM MenuItem m WHERE m.name = :name";
    private final String findByMenuJpql = "SELECT m FROM MenuItem m WHERE m.menu.id = :menuId";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        MenuItem item = new MenuItem();
        dao.add(item);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(item);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        MenuItem item = new MenuItem();
        doThrow(new RuntimeException("persist failed")).when(em).persist(item);

        assertThrows(RuntimeException.class, () -> dao.add(item));

        verify(tx).begin();
        verify(em).persist(item);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        MenuItem expected = new MenuItem();
        when(em.find(MenuItem.class, 10)).thenReturn(expected);

        MenuItem actual = dao.getById(10);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(MenuItem.class, 10);
        verify(em).close();
    }

    @Test
    void findAll_shouldReturnListAndClose() {
        List<MenuItem> list = List.of(new MenuItem(), new MenuItem());
        when(em.createQuery(findAllJpql, MenuItem.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<MenuItem> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, MenuItem.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByName_shouldReturnFirstResultAndClose() {
        MenuItem first = new MenuItem();
        List<MenuItem> list = List.of(first, new MenuItem());
        when(em.createQuery(findByNameJpql, MenuItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "Burger")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        MenuItem actual = dao.findByName("Burger");

        assertSame(first, actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByNameJpql, MenuItem.class);
        verify(typedQuery).setParameter("name", "Burger");
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByName_whenNoResult_shouldReturnNullAndClose() {
        when(em.createQuery(findByNameJpql, MenuItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "Nonexistent")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        MenuItem actual = dao.findByName("Nonexistent");

        assertNull(actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByNameJpql, MenuItem.class);
        verify(typedQuery).setParameter("name", "Nonexistent");
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByMenu_shouldReturnListAndClose() {
        List<MenuItem> list = List.of(new MenuItem());
        when(em.createQuery(findByMenuJpql, MenuItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("menuId", 3)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<MenuItem> result = dao.findByMenu(3);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByMenuJpql, MenuItem.class);
        verify(typedQuery).setParameter("menuId", 3);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        MenuItem item = new MenuItem();
        dao.update(item);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(item);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        MenuItem item = new MenuItem();
        when(em.find(MenuItem.class, 5)).thenReturn(item);

        dao.delete(5);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(MenuItem.class, 5);
        verify(em).remove(item);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(MenuItem.class, 6)).thenReturn(null);

        dao.delete(6);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(MenuItem.class, 6);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}