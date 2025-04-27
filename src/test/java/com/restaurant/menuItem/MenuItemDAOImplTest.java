package com.restaurant.menuItem;

import com.restaurant.daos.impl.MenuItemDAOImpl;
import com.restaurant.models.MenuItem;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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

    private final String findJpql = "SELECT m FROM MenuItem m";
    private final String findByNameJpql = "SELECT m FROM MenuItem m WHERE m.name = :name";
    private final String findByMenuJpql = "SELECT m FROM MenuItem m WHERE m.menu.id = :menuId";

    @BeforeEach
    void setUp() {
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        MenuItem item = new MenuItem();
        dao.add(item);

        InOrder inOrder = inOrder(emf, em, tx);
        inOrder.verify(emf).createEntityManager();
        inOrder.verify(em).getTransaction();
        inOrder.verify(tx).begin();
        inOrder.verify(em).persist(item);
        inOrder.verify(tx).commit();
        verify(tx, never()).rollback();
        inOrder.verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        MenuItem item = new MenuItem();
        doThrow(new RuntimeException("persist fail")).when(em).persist(item);
        when(tx.isActive()).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.add(item));
        assertEquals("persist fail", ex.getMessage());

        verify(tx).begin();
        verify(em).persist(item);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        MenuItem expected = new MenuItem();
        when(em.find(MenuItem.class, 123)).thenReturn(expected);

        MenuItem actual = dao.getById(123);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(MenuItem.class, 123);
        verify(em).close();
    }

    @Test
    void find_shouldQueryReturnListAndClose() {
        List<MenuItem> list = List.of(new MenuItem(), new MenuItem());
        when(em.createQuery(findJpql, MenuItem.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<MenuItem> result = dao.find();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findJpql, MenuItem.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByName_shouldReturnFirstAndClose() {
        MenuItem m1 = new MenuItem();
        MenuItem m2 = new MenuItem();
        when(em.createQuery(findByNameJpql, MenuItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "Burger")).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(m1, m2));

        MenuItem result = dao.findByName("Burger");

        assertSame(m1, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByNameJpql, MenuItem.class);
        verify(typedQuery).setParameter("name", "Burger");
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByName_whenNone_shouldReturnNullAndClose() {
        when(em.createQuery(findByNameJpql, MenuItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("name", "X")).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        MenuItem result = dao.findByName("X");

        assertNull(result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByNameJpql, MenuItem.class);
        verify(typedQuery).setParameter("name", "X");
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByMenu_shouldQueryReturnListAndClose() {
        List<MenuItem> list = List.of(new MenuItem(), new MenuItem(), new MenuItem());
        when(em.createQuery(findByMenuJpql, MenuItem.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("menuId", 77)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<MenuItem> result = dao.findByMenu(77);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByMenuJpql, MenuItem.class);
        verify(typedQuery).setParameter("menuId", 77);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        MenuItem item = new MenuItem();
        dao.update(item);

        InOrder inOrder = inOrder(emf, em, tx);
        inOrder.verify(emf).createEntityManager();
        inOrder.verify(em).getTransaction();
        inOrder.verify(tx).begin();
        inOrder.verify(em).merge(item);
        inOrder.verify(tx).commit();
        verify(tx, never()).rollback();
        inOrder.verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        MenuItem item = new MenuItem();
        doThrow(new PersistenceException("merge fail")).when(em).merge(item);
        when(tx.isActive()).thenReturn(true);

        PersistenceException ex = assertThrows(PersistenceException.class, () -> dao.update(item));
        assertEquals("merge fail", ex.getMessage());

        verify(tx).begin();
        verify(em).merge(item);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFound_commitAndClose() {
        MenuItem mi = new MenuItem();
        when(em.find(MenuItem.class, 999)).thenReturn(mi);

        dao.delete(999);

        InOrder inOrder = inOrder(emf, em, tx);
        inOrder.verify(emf).createEntityManager();
        inOrder.verify(em).getTransaction();
        inOrder.verify(tx).begin();
        inOrder.verify(em).find(MenuItem.class, 999);
        inOrder.verify(em).remove(mi);
        inOrder.verify(tx).commit();
        verify(tx, never()).rollback();
        inOrder.verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFound_butStillCommitAndClose() {
        when(em.find(MenuItem.class, 1000)).thenReturn(null);

        dao.delete(1000);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(MenuItem.class, 1000);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(tx, never()).rollback();
        verify(em).close();
    }
}