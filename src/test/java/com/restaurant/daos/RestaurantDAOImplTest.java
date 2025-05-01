package com.restaurant.daos;

import com.restaurant.constants.RestaurantStatus;
import com.restaurant.daos.impl.RestaurantDAOImpl;
import com.restaurant.dtos.restaurant.GetRestaurantDto;
import com.restaurant.models.Restaurant;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RestaurantDAOImplTest {

    @Mock
    EntityManagerFactory emf;
    @Mock
    EntityManager em;
    @Mock
    EntityTransaction tx;
    @Mock
    CriteriaBuilder cb;
    @Mock
    CriteriaQuery<Restaurant> cq;
    @Mock
    Root<Restaurant> root;
    @Mock
    Fetch<?, ?> fetchOrder;
    @Mock
    Path<?> path;
    @Mock
    Expression<String> expr;
    @Mock
    Predicate predicate;
    @Mock
    jakarta.persistence.criteria.Order jpaOrder;
    @Mock
    TypedQuery<Restaurant> query;
    @Mock
    TypedQuery<Long> countQ;
    @Mock
    TypedQuery<Restaurant> queryAll;
    @InjectMocks
    RestaurantDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Restaurant.class)).thenReturn(cq);
        when(cq.from(Restaurant.class)).thenReturn(root);
        when(root.fetch(eq("order"), eq(JoinType.LEFT))).thenReturn((Fetch) fetchOrder);

        when(root.get(anyString())).thenReturn((Path) path);
        when(path.get(anyString())).thenReturn((Path) path);

        when(cb.lower(any(Expression.class))).thenReturn(expr);
        when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
        when(cb.equal(any(Expression.class), any())).thenReturn(predicate);

        when(cb.asc(any(Expression.class))).thenReturn(jpaOrder);
        when(cb.desc(any(Expression.class))).thenReturn(jpaOrder);

        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(cq.orderBy(any(jakarta.persistence.criteria.Order[].class))).thenReturn(cq);

        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(countQ);
        when(countQ.setParameter(anyString(), any())).thenReturn(countQ);

        when(em.createQuery(eq("SELECT r FROM Restaurant r"), eq(Restaurant.class))).thenReturn(queryAll);
        when(queryAll.getResultList()).thenReturn(new ArrayList<>());
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Restaurant r = new Restaurant();
        dao.add(r);

        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).persist(r);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistFails_rollsBackAndCloses() {
        Restaurant r = new Restaurant();
        doThrow(RuntimeException.class).when(em).persist(r);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(r));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_returnsAndCloses() {
        Restaurant r = new Restaurant();
        when(em.find(Restaurant.class, 10)).thenReturn(r);

        Restaurant result = dao.getById(10);
        assertSame(r, result);
        verify(em).close();
    }

    @Test
    void find_withNoFilters_andDefaults() {
        GetRestaurantDto dto = new GetRestaurantDto();
        dto.setPage(0);
        dto.setSize(3);

        dao.find(dto);

        verify(cb).desc(any(Expression.class));
        verify(cq).orderBy(jpaOrder);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(3);
        verify(em).close();
    }

    @Test
    void find_withFilters_andPaging() {
        GetRestaurantDto dto = new GetRestaurantDto();
        dto.setName("Foo");
        dto.setAddress("Bar");
        dto.setStatus(RestaurantStatus.ACTIVE);
        dto.setSortBy("name");
        dto.setSortDir("desc");
        dto.setPage(1);
        dto.setSize(5);

        List<Restaurant> sample = List.of(new Restaurant());
        when(query.getResultList()).thenReturn(sample);

        List<Restaurant> result = dao.find(dto);

        verify(cb, times(2)).lower(any(Expression.class));
        verify(cb).like(eq(expr), eq("%foo%"));
        verify(cb).like(eq(expr), eq("%bar%"));
        verify(cb).equal(eq(path), eq(RestaurantStatus.ACTIVE));
        verify(cq).where(any(Predicate[].class));
        verify(cb).desc(any(Expression.class));
        verify(cq).orderBy(jpaOrder);
        verify(query).setFirstResult(5);
        verify(query).setMaxResults(5);
        assertEquals(sample, result);
        verify(em).close();
    }

    @Test
    void find_wrapsException_andCloses() {
        when(em.getCriteriaBuilder()).thenThrow(IllegalStateException.class);
        assertThrows(RuntimeException.class, () -> dao.find(new GetRestaurantDto()));
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Restaurant r = new Restaurant();
        dao.update(r);

        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).merge(r);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeFails_rollsBackAndCloses() {
        Restaurant r = new Restaurant();
        doThrow(RuntimeException.class).when(em).merge(r);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.update(r));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_existing_removesCommitsAndCloses() {
        Restaurant r = new Restaurant();
        when(em.find(Restaurant.class, 20)).thenReturn(r);

        dao.delete(20);

        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(Restaurant.class, 20);
        in.verify(em).remove(r);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_notExisting_commitsAndCloses() {
        when(em.find(Restaurant.class, 21)).thenReturn(null);
        dao.delete(21);

        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(Restaurant.class, 21);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_exception_rollsBackAndCloses() {
        doThrow(RuntimeException.class).when(em).find(Restaurant.class, 22);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(22));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void existsByNameAndAddress_trueAndFalse() {
        when(countQ.getSingleResult()).thenReturn(1L);
        assertTrue(dao.existsByNameAndAddress("Foo", "Bar"));
        when(countQ.getSingleResult()).thenReturn(0L);
        assertFalse(dao.existsByNameAndAddress("Foo", "Bar"));
        verify(em, times(2)).close();
    }

    @Test
    void findAll_returnsListAndCloses() {
        List<Restaurant> all = List.of(new Restaurant(), new Restaurant());
        when(queryAll.getResultList()).thenReturn(all);

        List<Restaurant> result = dao.findAll();
        assertEquals(all, result);
        verify(em).close();
    }
}
