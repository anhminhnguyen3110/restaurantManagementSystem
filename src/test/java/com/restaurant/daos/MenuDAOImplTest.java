package com.restaurant.daos;

import com.restaurant.daos.impl.MenuDAOImpl;
import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.models.Menu;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MenuDAOImplTest {

    @Mock
    private EntityManagerFactory emf;
    @Mock
    private EntityManager em;
    @Mock
    private EntityTransaction tx;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private CriteriaQuery<Menu> cq;
    @Mock
    private Root<Menu> root;
    @Mock
    private Fetch fetch;
    @Mock
    private Predicate predicate;
    @Mock
    private TypedQuery<Menu> typedQuery;
    @Mock
    private Path path;
    @InjectMocks
    private MenuDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Menu.class)).thenReturn(cq);
        when(cq.from(Menu.class)).thenReturn(root);
        when(root.fetch("restaurant", JoinType.LEFT)).thenReturn(fetch);
        when(root.get(anyString())).thenReturn(path);
        when(cb.lower(path)).thenReturn(mock(Expression.class));
        when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(cq.orderBy(anyList())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Menu m = new Menu();
        dao.add(m);
        InOrder o = inOrder(emf, em, tx);
        o.verify(emf).createEntityManager();
        o.verify(em).getTransaction();
        o.verify(tx).begin();
        o.verify(em).persist(m);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        Menu m = new Menu();
        doThrow(RuntimeException.class).when(em).persist(m);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.add(m));
        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).persist(m);
        o.verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void find_withNoFilters_usesDefaultSortAndPagination() {
        GetMenuDto dto = new GetMenuDto();
        dto.setName(null);
        dto.setRestaurantName("");
        dto.setSortBy(null);
        dto.setSortDir(null);
        dto.setPage(2);
        dto.setSize(5);
        List<Menu> result = dao.find(dto);
        assertEquals(Collections.emptyList(), result);
        verify(cb).asc(path);
        verify(typedQuery).setFirstResult(10);
        verify(typedQuery).setMaxResults(5);
        verify(em).close();
    }

    @Test
    void find_withNameFilter_andDescSort() {
        GetMenuDto dto = new GetMenuDto();
        dto.setName("Burger");
        dto.setRestaurantName(null);
        dto.setSortBy("name");
        dto.setSortDir("desc");
        dto.setPage(1);
        dto.setSize(3);
        Expression<String> lowered = mock(Expression.class);
        when(cb.lower(path)).thenReturn(lowered);
        when(cb.desc(path)).thenReturn(mock(Order.class));
        List<Menu> result = dao.find(dto);
        assertEquals(Collections.emptyList(), result);
        verify(cb).like(lowered, "%burger%");
        verify(cb).desc(path);
        verify(typedQuery).setFirstResult(3);
        verify(typedQuery).setMaxResults(3);
        verify(em).close();
    }

    @Test
    void find_whenException_shouldWrapAndClose() {
        when(em.getCriteriaBuilder()).thenThrow(IllegalStateException.class);
        GetMenuDto dto = new GetMenuDto();
        dto.setPage(0);
        dto.setSize(1);
        assertThrows(RuntimeException.class, () -> dao.find(dto));
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Menu m = new Menu();
        dao.update(m);
        InOrder o = inOrder(emf, em, tx);
        o.verify(emf).createEntityManager();
        o.verify(em).getTransaction();
        o.verify(tx).begin();
        o.verify(em).merge(m);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        Menu m = new Menu();
        doThrow(RuntimeException.class).when(em).merge(m);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.update(m));
        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).merge(m);
        o.verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_whenEntityExists_removesAndCommit() {
        Menu m = new Menu();
        when(em.find(Menu.class, 3)).thenReturn(m);
        dao.delete(3);
        InOrder o = inOrder(em, tx);
        o.verify(tx).begin();
        o.verify(em).find(Menu.class, 3);
        o.verify(em).remove(m);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenEntityNotExists_commitsWithoutRemove() {
        when(em.find(Menu.class, 4)).thenReturn(null);
        dao.delete(4);
        InOrder o = inOrder(em, tx);
        o.verify(tx).begin();
        o.verify(em).find(Menu.class, 4);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenException_shouldRollbackAndClose() {
        doThrow(RuntimeException.class).when(em).find(Menu.class, 5);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.delete(5));
        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).find(Menu.class, 5);
        o.verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_whenFound_returnsMenuAndClose() {
        TypedQuery<Menu> q = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Menu.class))).thenReturn(q);
        Menu m = new Menu();
        when(q.setParameter("id", 6)).thenReturn(q);
        when(q.getSingleResult()).thenReturn(m);
        Menu result = dao.getById(6);
        assertSame(m, result);
        verify(em).close();
    }

    @Test
    void getById_whenNoResult_returnsNull() {
        TypedQuery<Menu> q = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Menu.class))).thenReturn(q);
        when(q.setParameter("id", 7)).thenReturn(q);
        when(q.getSingleResult()).thenThrow(NoResultException.class);
        Menu result = dao.getById(7);
        assertNull(result);
        verify(em).close();
    }

    @Test
    void existsByNameAndRestaurant_withoutExclude_true() {
        TypedQuery<Long> q = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(q);
        when(q.setParameter("name", "A")).thenReturn(q);
        when(q.setParameter("rid", 8)).thenReturn(q);
        when(q.getSingleResult()).thenReturn(1L);
        assertTrue(dao.existsByNameAndRestaurant("A", 8));
        verify(em).close();
    }

    @Test
    void existsByNameAndRestaurant_withoutExclude_false() {
        TypedQuery<Long> q = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(q);
        when(q.setParameter("name", "B")).thenReturn(q);
        when(q.setParameter("rid", 9)).thenReturn(q);
        when(q.getSingleResult()).thenReturn(0L);
        assertFalse(dao.existsByNameAndRestaurant("B", 9));
        verify(em).close();
    }

    @Test
    void existsByNameAndRestaurant_withExclude_true() {
        TypedQuery<Long> q = mock(TypedQuery.class);
        when(em.createQuery(contains("m.id <> :eid"), eq(Long.class))).thenReturn(q);
        when(q.setParameter("name", "C")).thenReturn(q);
        when(q.setParameter("rid", 10)).thenReturn(q);
        when(q.setParameter("eid", 11)).thenReturn(q);
        when(q.getSingleResult()).thenReturn(2L);
        assertTrue(dao.existsByNameAndRestaurant("C", 10, 11));
        verify(em).close();
    }

    @Test
    void existsByNameAndRestaurant_withExclude_false() {
        TypedQuery<Long> q = mock(TypedQuery.class);
        when(em.createQuery(contains("m.id <> :eid"), eq(Long.class))).thenReturn(q);
        when(q.setParameter("name", "D")).thenReturn(q);
        when(q.setParameter("rid", 12)).thenReturn(q);
        when(q.setParameter("eid", 13)).thenReturn(q);
        when(q.getSingleResult()).thenReturn(0L);
        assertFalse(dao.existsByNameAndRestaurant("D", 12, 13));
        verify(em).close();
    }
}
