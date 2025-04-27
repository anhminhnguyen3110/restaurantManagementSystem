package com.restaurant.menu;

import com.restaurant.daos.impl.MenuDAOImpl;
import com.restaurant.models.Menu;
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

    private final String findJpql = "SELECT m FROM Menu m";
    private final String findByRestJpql =
            "SELECT m FROM Menu m WHERE m.restaurant.id = :rid";

    @BeforeEach
    void setUp() {
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Menu menu = new Menu();
        dao.add(menu);

        InOrder inOrder = inOrder(emf, em, tx);
        inOrder.verify(emf).createEntityManager();
        inOrder.verify(em).getTransaction();
        inOrder.verify(tx).begin();
        inOrder.verify(em).persist(menu);
        inOrder.verify(tx).commit();
        verify(tx, never()).rollback();
        inOrder.verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        Menu menu = new Menu();
        doThrow(new RuntimeException("persist fail")).when(em).persist(menu);
        when(tx.isActive()).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.add(menu));
        assertEquals("persist fail", ex.getMessage());

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
    void find_shouldQueryReturnListAndClose() {
        List<Menu> list = List.of(new Menu(), new Menu());
        when(em.createQuery(findJpql, Menu.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Menu> result = dao.find();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findJpql, Menu.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByRestaurant_shouldQueryReturnListAndClose() {
        List<Menu> list = List.of(new Menu(), new Menu(), new Menu());
        when(em.createQuery(findByRestJpql, Menu.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("rid", 42)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Menu> result = dao.findByRestaurant(42);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByRestJpql, Menu.class);
        verify(typedQuery).setParameter("rid", 42);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Menu menu = new Menu();
        dao.update(menu);

        InOrder inOrder = inOrder(emf, em, tx);
        inOrder.verify(emf).createEntityManager();
        inOrder.verify(em).getTransaction();
        inOrder.verify(tx).begin();
        inOrder.verify(em).merge(menu);
        inOrder.verify(tx).commit();
        verify(tx, never()).rollback();
        inOrder.verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        Menu menu = new Menu();
        doThrow(new PersistenceException("merge fail")).when(em).merge(menu);
        when(tx.isActive()).thenReturn(true);

        PersistenceException ex = assertThrows(PersistenceException.class, () -> dao.update(menu));
        assertEquals("merge fail", ex.getMessage());

        verify(tx).begin();
        verify(em).merge(menu);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFound_commitAndClose() {
        Menu menu = new Menu();
        when(em.find(Menu.class, 99)).thenReturn(menu);

        dao.delete(99);

        InOrder inOrder = inOrder(emf, em, tx);
        inOrder.verify(emf).createEntityManager();
        inOrder.verify(em).getTransaction();
        inOrder.verify(tx).begin();
        inOrder.verify(em).find(Menu.class, 99);
        inOrder.verify(em).remove(menu);
        inOrder.verify(tx).commit();
        verify(tx, never()).rollback();
        inOrder.verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFound_butStillCommitAndClose() {
        when(em.find(Menu.class, 100)).thenReturn(null);

        dao.delete(100);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Menu.class, 100);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(tx, never()).rollback();
        verify(em).close();
    }
}