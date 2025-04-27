package com.restaurant.user;

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

    private final String findJpql = "SELECT u FROM User u";
    private final String findByUsernameJpql = "SELECT u FROM User u WHERE u.username = :username";
    private final String findByRoleJpql = "SELECT u FROM User u WHERE u.role = :role";

    @BeforeEach
    void setUp() {
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        User u = new User();
        dao.add(u);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(u);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        User u = new User();
        doThrow(new RuntimeException("oops")).when(em).persist(u);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(u));

        verify(tx).begin();
        verify(em).persist(u);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        User expected = new User();
        when(em.find(User.class, 17)).thenReturn(expected);

        User actual = dao.getById(17);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(User.class, 17);
        verify(em).close();
    }

    @Test
    void find_shouldQueryReturnListAndClose() {
        List<User> list = List.of(new User(), new User());
        when(em.createQuery(findJpql, User.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<User> result = dao.find();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findJpql, User.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findByUsername_shouldReturnFirstAndClose() {
        String name = "alice";
        User expected = new User();
        when(em.createQuery(findByUsernameJpql, User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", name)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.of(expected));

        User actual = dao.findByUsername(name);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByUsernameJpql, User.class);
        verify(typedQuery).setParameter("username", name);
        verify(typedQuery).setMaxResults(1);
        verify(typedQuery).getResultStream();
        verify(em).close();
    }

    @Test
    void findByUsername_whenEmpty_shouldReturnNull() {
        String name = "bob";
        when(em.createQuery(findByUsernameJpql, User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", name)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultStream()).thenReturn(Stream.empty());

        assertNull(dao.findByUsername(name));
        verify(em).close();
    }

    @Test
    void findByRole_shouldQueryReturnListAndClose() {
        UserRole role = UserRole.MANAGER;
        List<User> list = List.of(new User());
        when(em.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("role", role)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<User> result = dao.findByRole(role);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery("SELECT u FROM User u WHERE u.role = :role", User.class);
        verify(typedQuery).setParameter("role", role);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        User u = new User();
        dao.update(u);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(u);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        User u = new User();
        doThrow(new RuntimeException("fail")).when(em).merge(u);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.update(u));

        verify(tx).begin();
        verify(em).merge(u);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        int id = 42;
        User u = new User();
        when(em.find(User.class, id)).thenReturn(u);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(User.class, id);
        verify(em).remove(u);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        int id = 99;
        when(em.find(User.class, id)).thenReturn(null);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(User.class, id);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenFindThrows_shouldRollbackAndClose() {
        int id = 7;
        doThrow(new RuntimeException("boom")).when(em).find(User.class, id);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(id));

        verify(tx).begin();
        verify(em).find(User.class, id);
        verify(tx).rollback();
        verify(em).close();
    }
}