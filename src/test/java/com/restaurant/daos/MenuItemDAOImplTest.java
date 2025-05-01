package com.restaurant.daos;

import com.restaurant.daos.impl.MenuItemDAOImpl;
import com.restaurant.dtos.menuItem.GetMenuItemsDto;
import com.restaurant.models.MenuItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
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
class MenuItemDAOImplTest {

    @Mock
    private EntityManagerFactory emf;
    @Mock
    private EntityManager em;
    @Mock
    private EntityTransaction tx;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private CriteriaQuery<MenuItem> cq;
    @Mock
    private Root<MenuItem> root;
    @Mock
    private Fetch menuFetch;
    @Mock
    private Fetch restaurantFetch;
    @Mock
    private Fetch orderItemsFetch;
    @Mock
    private Predicate predicate;
    @Mock
    private TypedQuery<MenuItem> typedQuery;
    @Mock
    private TypedQuery<Long> countQuery;
    @Mock
    private Path path;
    @InjectMocks
    private MenuItemDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(MenuItem.class)).thenReturn(cq);
        when(cq.from(MenuItem.class)).thenReturn(root);
        when(root.fetch("menu", JoinType.LEFT)).thenReturn(menuFetch);
        when(menuFetch.fetch("restaurant", JoinType.LEFT)).thenReturn(restaurantFetch);
        when(root.fetch("orderItems", JoinType.LEFT)).thenReturn(orderItemsFetch);
        when(root.get(anyString())).thenReturn(path);
        when(path.get(anyString())).thenReturn(path);
        when(cb.lower(path)).thenReturn(mock(Expression.class));
        when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
        when(cb.ge(path, 100.0)).thenReturn(predicate);
        when(cb.le(path, 200.0)).thenReturn(predicate);
        when(cb.equal(path, 5)).thenReturn(predicate);
        when(cb.equal(path, 6)).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        when(cq.select(root)).thenReturn(cq);
        when(cq.distinct(true)).thenReturn(cq);
        when(cq.where(predicate)).thenReturn(cq);
        when(cb.asc(path)).thenReturn(mock(Order.class));
        when(cb.desc(path)).thenReturn(mock(Order.class));
        when(cq.orderBy(anyList())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(1L);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        MenuItem item = new MenuItem();
        dao.add(item);
        InOrder o = inOrder(emf, em, tx);
        o.verify(emf).createEntityManager();
        o.verify(em).getTransaction();
        o.verify(tx).begin();
        o.verify(em).persist(item);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        MenuItem item = new MenuItem();
        doThrow(RuntimeException.class).when(em).persist(item);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.add(item));
        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).persist(item);
        o.verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_returnsAndCloses() {
        MenuItem m = new MenuItem();
        when(emf.createEntityManager()).thenReturn(em);
        when(em.find(MenuItem.class, 9)).thenReturn(m);
        MenuItem result = dao.getById(9);
        assertSame(m, result);
        verify(em).close();
    }

    @Test
    void find_withVariousFilters_andDescSort() {
        GetMenuItemsDto dto = new GetMenuItemsDto();
        dto.setName("Pizza");
        dto.setMoreThanPrice(100.0);
        dto.setLessThanPrice(200.0);
        dto.setMenuId(5);
        dto.setRestaurantId(6);
        dto.setSortBy("price");
        dto.setSortDir("desc");
        dto.setPage(1);
        dto.setSize(4);
        List<MenuItem> result = dao.find(dto);
        assertTrue(result.isEmpty());
        verify(cb).like(any(Expression.class), eq("%pizza%"));
        verify(cb).ge(path, 100.0);
        verify(cb).le(path, 200.0);
        verify(cb).equal(path, 5);
        verify(cb).equal(path, 6);
        verify(cb).desc(path);
        verify(typedQuery).setFirstResult(4);
        verify(typedQuery).setMaxResults(4);
        verify(em).close();
    }

    @Test
    void find_withNoFilters_defaultSortAndPagination() {
        GetMenuItemsDto dto = new GetMenuItemsDto();
        dto.setPage(0);
        dto.setSize(3);
        dto.setMoreThanPrice(0.0);
        dto.setLessThanPrice(Double.MAX_VALUE);
        List<MenuItem> result = dao.find(dto);
        assertTrue(result.isEmpty());
        verify(cb).desc(path);
        verify(typedQuery).setFirstResult(0);
        verify(typedQuery).setMaxResults(3);
        verify(em).close();
    }

    @Test
    void find_whenException_shouldWrapAndClose() {
        when(em.getCriteriaBuilder()).thenThrow(IllegalStateException.class);
        GetMenuItemsDto dto = new GetMenuItemsDto();
        dto.setPage(0);
        dto.setSize(1);
        dto.setMoreThanPrice(0.0);
        dto.setLessThanPrice(Double.MAX_VALUE);
        assertThrows(RuntimeException.class, () -> dao.find(dto));
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        MenuItem item = new MenuItem();
        dao.update(item);
        InOrder o = inOrder(emf, em, tx);
        o.verify(emf).createEntityManager();
        o.verify(em).getTransaction();
        o.verify(tx).begin();
        o.verify(em).merge(item);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        MenuItem item = new MenuItem();
        doThrow(RuntimeException.class).when(em).merge(item);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.update(item));
        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).merge(item);
        o.verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_whenExists_removesAndCommit() {
        MenuItem m = new MenuItem();
        when(emf.createEntityManager()).thenReturn(em);
        when(em.find(MenuItem.class, 7)).thenReturn(m);
        dao.delete(7);
        InOrder o = inOrder(em, tx);
        o.verify(tx).begin();
        o.verify(em).find(MenuItem.class, 7);
        o.verify(em).remove(m);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenNotExists_commitsWithoutRemove() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.find(MenuItem.class, 8)).thenReturn(null);
        dao.delete(8);
        InOrder o = inOrder(em, tx);
        o.verify(tx).begin();
        o.verify(em).find(MenuItem.class, 8);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenException_shouldRollbackAndClose() {
        doThrow(RuntimeException.class).when(em).find(MenuItem.class, 10);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.delete(10));
        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).find(MenuItem.class, 10);
        o.verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void existsByName_withoutExclude_true() {
        when(countQuery.getSingleResult()).thenReturn(2L);
        assertTrue(dao.existsByName("X"));
        verify(countQuery).setParameter("name", "X");
        verify(em).close();
    }

    @Test
    void existsByName_withoutExclude_false() {
        when(countQuery.getSingleResult()).thenReturn(0L);
        assertFalse(dao.existsByName("Y"));
        verify(countQuery).setParameter("name", "Y");
        verify(em).close();
    }

    @Test
    void existsByName_withExclude_true() {
        when(countQuery.getSingleResult()).thenReturn(3L);
        assertTrue(dao.existsByName("Z", 11));
        verify(countQuery).setParameter("name", "Z");
        verify(countQuery).setParameter("eid", 11);
        verify(em).close();
    }

    @Test
    void existsByName_withExclude_false() {
        when(countQuery.getSingleResult()).thenReturn(0L);
        assertFalse(dao.existsByName("W", 12));
        verify(countQuery).setParameter("name", "W");
        verify(countQuery).setParameter("eid", 12);
        verify(em).close();
    }

    @Test
    void findByRestaurantId_returnsListAndCloses() {
        TypedQuery<MenuItem> q = mock(TypedQuery.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.createQuery(anyString(), eq(MenuItem.class))).thenReturn(q);
        when(q.setParameter("restaurantId", 5)).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new MenuItem()));
        List<MenuItem> list = dao.findByRestaurantId(5);
        assertEquals(1, list.size());
        verify(em).close();
    }
}
