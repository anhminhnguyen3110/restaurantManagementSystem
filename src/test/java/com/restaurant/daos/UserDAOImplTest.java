package com.restaurant.daos;

import com.restaurant.constants.UserRole;
import com.restaurant.daos.impl.UserDAOImpl;
import com.restaurant.dtos.user.GetUserDto;
import com.restaurant.models.User;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserDAOImplTest {

    @Mock
    EntityManagerFactory emf;
    @Mock
    EntityManager em;
    @Mock
    EntityTransaction tx;
    @Mock
    CriteriaBuilder cb;
    @Mock
    CriteriaQuery<User> cq;
    @Mock
    Root<User> root;
    @Mock
    Predicate predicate;
    @Mock
    Expression<String> expr;
    @Mock
    Path<Object> path;
    @Mock
    TypedQuery<User> queryFind;
    @Mock
    TypedQuery<User> queryByUsername;
    @Mock
    TypedQuery<User> queryShippers;
    @Mock
    TypedQuery<Long> countQ;
    @InjectMocks
    UserDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(User.class)).thenReturn(cq);
        when(cq.from(User.class)).thenReturn(root);
        when(root.get(anyString())).thenReturn(path);
        when(cb.lower(any(Expression.class))).thenReturn(expr);
        when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
        when(cb.equal(any(Expression.class), any())).thenReturn(predicate);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(queryFind);
        when(queryFind.setFirstResult(anyInt())).thenReturn(queryFind);
        when(queryFind.setMaxResults(anyInt())).thenReturn(queryFind);
        when(queryFind.getResultList()).thenReturn(new ArrayList<>());
        when(em.createQuery(eq("SELECT u FROM User u WHERE lower(u.username) = :un"), eq(User.class)))
                .thenReturn(queryByUsername);
        when(queryByUsername.setParameter(eq("un"), anyString())).thenReturn(queryByUsername);
        when(queryByUsername.setMaxResults(1)).thenReturn(queryByUsername);
        when(queryByUsername.getResultStream()).thenReturn(Stream.empty());
        when(em.createQuery(eq("SELECT COUNT(u) FROM User u WHERE lower(u.username) = :un"), eq(Long.class)))
                .thenReturn(countQ);
        when(countQ.setParameter(eq("un"), anyString())).thenReturn(countQ);
        when(countQ.getSingleResult()).thenReturn(0L);
        when(em.createQuery(eq("SELECT u FROM User u WHERE u.role = :role"), eq(User.class)))
                .thenReturn(queryShippers);
        when(queryShippers.setParameter("role", UserRole.SHIPPER)).thenReturn(queryShippers);
        when(queryShippers.getResultList()).thenReturn(new ArrayList<>());
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        User u = new User();
        dao.add(u);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).persist(u);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistFails_rollsBackAndCloses() {
        User u = new User();
        doThrow(RuntimeException.class).when(em).persist(u);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.add(u));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_returnsAndCloses() {
        User u = new User();
        when(em.find(User.class, 5)).thenReturn(u);
        User result = dao.getById(5);
        assertSame(u, result);
        verify(em).close();
    }

    @Test
    void find_withNoFilters_andDefaults() {
        GetUserDto dto = new GetUserDto();
        dto.setPage(0);
        dto.setSize(4);
        dao.find(dto);
        verify(queryFind).setFirstResult(0);
        verify(queryFind).setMaxResults(4);
        verify(cq, never()).where(any(Predicate[].class));
        verify(em).close();
    }

    @Test
    void find_withFilters_andPaging() {
        GetUserDto dto = new GetUserDto();
        dto.setUsername("Bob");
        dto.setEmail("bob@example.com");
        dto.setRole(UserRole.MANAGER);
        dto.setName("Robert");
        dto.setPage(1);
        dto.setSize(2);
        List<User> list = List.of(new User());
        when(queryFind.getResultList()).thenReturn(list);
        List<User> result = dao.find(dto);
        verify(cb, times(3)).lower(any(Expression.class));
        verify(cb).like(eq(expr), eq("%bob%"));
        verify(cb).like(eq(expr), eq("%bob@example.com%"));
        verify(cb).equal(any(Expression.class), eq(UserRole.MANAGER));
        verify(cb).like(eq(expr), eq("%robert%"));
        verify(cq).where(any(Predicate[].class));
        verify(queryFind).setFirstResult(2);
        verify(queryFind).setMaxResults(2);
        assertEquals(list, result);
        verify(em).close();
    }

    @Test
    void find_wrapsException_andCloses() {
        when(em.getCriteriaBuilder()).thenThrow(IllegalStateException.class);
        assertThrows(RuntimeException.class, () -> dao.find(new GetUserDto()));
        verify(em).close();
    }

    @Test
    void findByUsername_foundAndNotFound() {
        User u = new User();
        when(queryByUsername.getResultStream()).thenReturn(Stream.of(u));
        User found = dao.findByUsername("alice");
        assertSame(u, found);
        when(queryByUsername.getResultStream()).thenReturn(Stream.empty());
        assertNull(dao.findByUsername("alice"));
        verify(queryByUsername, times(2)).setParameter("un", "alice");
        verify(queryByUsername, times(2)).setMaxResults(1);
        verify(em, times(2)).close();
    }

    @Test
    void update_mergesCommitAndClose() {
        User u = new User();
        dao.update(u);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).merge(u);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeFails_rollsBackAndCloses() {
        User u = new User();
        doThrow(RuntimeException.class).when(em).merge(u);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.update(u));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_existing_removesAndCloses() {
        User u = new User();
        when(em.find(User.class, 7)).thenReturn(u);
        dao.delete(7);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(User.class, 7);
        in.verify(em).remove(u);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_nonExisting_commitsAndCloses() {
        when(em.find(User.class, 8)).thenReturn(null);
        dao.delete(8);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(User.class, 8);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_exception_rollsBackAndCloses() {
        doThrow(RuntimeException.class).when(em).find(User.class, 9);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.delete(9));
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void existsByUsername_trueAndFalse() {
        when(countQ.getSingleResult()).thenReturn(1L, 0L);
        assertTrue(dao.existsByUsername("eve"));
        assertFalse(dao.existsByUsername("eve"));
        verify(countQ, times(2)).setParameter("un", "eve");
        verify(em, times(2)).close();
    }

    @Test
    void findAllShippers_returnsListAndCloses() {
        List<User> shippers = List.of(new User(), new User());
        when(queryShippers.getResultList()).thenReturn(shippers);
        List<User> result = dao.findAllShippers();
        assertEquals(shippers, result);
        verify(queryShippers).setParameter("role", UserRole.SHIPPER);
        verify(em).close();
    }
}
