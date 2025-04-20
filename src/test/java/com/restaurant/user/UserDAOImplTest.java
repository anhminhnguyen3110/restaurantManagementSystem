package com.restaurant;

import com.restaurant.constants.UserRole;
import com.restaurant.daos.impl.UserDAOImpl;
import com.restaurant.models.User;
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
class UserDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<User> typedQuery;

    @InjectMocks
    private UserDAOImpl dao;

    private final String findAllJpql = "SELECT u FROM User u";
    private final String findByUsernameJpql = "SELECT u FROM User u WHERE u.username = :username";
    private final String findByRoleJpql = "SELECT u FROM User u WHERE u.role = :role";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        User user = new User();
        dao.add(user);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(user);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        User user = new User();
        doThrow(new RuntimeException("persist failed")).when(em).persist(user);

        assertThrows(RuntimeException.class, () -> dao.add(user));

        verify(tx).begin();
        verify(em).persist(user);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        User expected = new User();
        when(em.find(User.class, 42)).thenReturn(expected);

        User actual = dao.getById(42);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(User.class, 42);
        verify(em).close();
    }

    @Test
    void findAll_shouldReturnListAndClose() {
        List<User> list = List.of(new User(), new User());
        when(em.createQuery(findAllJpql, User.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<User> result = dao.findAll();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, User.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByUsername_shouldReturnFirstResultAndClose() {
        User first = new User();
        when(em.createQuery(findByUsernameJpql, User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", "john")).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(first));

        User actual = dao.findByUsername("john");

        assertSame(first, actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByUsernameJpql, User.class);
        verify(typedQuery).setParameter("username", "john");
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByUsername_whenNoResult_shouldReturnNullAndClose() {
        when(em.createQuery(findByUsernameJpql, User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", "none")).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        User actual = dao.findByUsername("none");

        assertNull(actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByUsernameJpql, User.class);
        verify(typedQuery).setParameter("username", "none");
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByRole_shouldReturnListAndClose() {
        List<User> list = List.of(new User());
        when(em.createQuery(findByRoleJpql, User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("role", UserRole.MANAGER)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<User> result = dao.findByRole(UserRole.MANAGER);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByRoleJpql, User.class);
        verify(typedQuery).setParameter("role", UserRole.MANAGER);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        User user = new User();
        dao.update(user);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(user);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        User user = new User();
        when(em.find(User.class, 99)).thenReturn(user);

        dao.delete(99);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(User.class, 99);
        verify(em).remove(user);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(User.class, 100)).thenReturn(null);

        dao.delete(100);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(User.class, 100);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}