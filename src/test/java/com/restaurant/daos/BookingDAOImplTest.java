package com.restaurant.daos;

import com.restaurant.constants.BookingStatus;
import com.restaurant.constants.BookingTimeSlot;
import com.restaurant.daos.impl.BookingDAOImpl;
import com.restaurant.dtos.booking.GetBookingsDto;
import com.restaurant.models.Booking;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingDAOImplTest {

    @Mock
    private EntityManagerFactory emf;
    @Mock
    private EntityManager em;
    @Mock
    private EntityTransaction tx;
    @Mock
    private CriteriaBuilder cb;
    @Mock
    private CriteriaQuery<Booking> cq;
    @Mock
    private Root<Booking> root;
    @Mock
    private Fetch fetchCustomer;
    @Mock
    private Fetch fetchTable;
    @Mock
    private Predicate predicate;
    @Mock
    private TypedQuery<Booking> typedQuery;
    @Mock
    private Path path;
    @InjectMocks
    private BookingDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Booking.class)).thenReturn(cq);
        when(cq.from(Booking.class)).thenReturn(root);
        when(root.fetch("customer", JoinType.LEFT)).thenReturn(fetchCustomer);
        when(root.fetch("table", JoinType.LEFT)).thenReturn(fetchTable);
        when(root.get(anyString())).thenReturn(path);
        when(path.get(anyString())).thenReturn(path);
        when(cb.lower(path)).thenReturn(mock(Expression.class));
        when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
        when(cb.equal(any(Expression.class), any())).thenReturn(predicate);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(cb.asc(path)).thenReturn(mock(Order.class));
        when(cb.desc(path)).thenReturn(mock(Order.class));
        when(cq.orderBy(anyList())).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Booking b = new Booking();
        dao.add(b);
        InOrder o = inOrder(emf, em, tx);
        o.verify(emf).createEntityManager();
        o.verify(em).getTransaction();
        o.verify(tx).begin();
        o.verify(em).persist(b);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenPersistThrows_shouldRollbackAndClose() {
        Booking b = new Booking();
        doThrow(RuntimeException.class).when(em).persist(b);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.add(b));
        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).persist(b);
        o.verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_returnsAndCloses() {
        Booking b = new Booking();
        when(emf.createEntityManager()).thenReturn(em);
        when(em.find(Booking.class, 5)).thenReturn(b);
        Booking result = dao.getById(5);
        assertSame(b, result);
        verify(em).close();
    }

    @Test
    void find_withVariousFilters_andSortByRestaurantDesc() {
        GetBookingsDto dto = new GetBookingsDto();
        dto.setCustomerName("Alice");
        dto.setPhoneNumber("123");
        dto.setTableNumber(7);
        dto.setStatus(BookingStatus.BOOKED);
        dto.setDate(LocalDate.of(2025, 5, 1));
        dto.setStartTime(BookingTimeSlot.SLOT_12_00);
        dto.setEndTime(BookingTimeSlot.SLOT_13_00);
        dto.setSortBy("restaurant");
        dto.setSortDir("desc");
        dto.setPage(1);
        dto.setSize(2);

        List<Booking> result = dao.find(dto);

        assertEquals(Collections.emptyList(), result);
        verify(cb).like(any(Expression.class), eq("%alice%"));
        verify(cb).like(any(Expression.class), eq("%123%"));
        verify(cb).equal(path, 7);
        verify(cb).equal(path, BookingStatus.BOOKED);
        verify(cb).equal(path, dto.getDate());
        verify(cb).equal(path, dto.getStartTime());
        verify(cb).equal(path, dto.getEndTime());
        verify(cb).desc(path);
        verify(typedQuery).setFirstResult(2);
        verify(typedQuery).setMaxResults(2);
        verify(em).close();
    }

    @Test
    void find_withNoFilters_defaultPagination() {
        GetBookingsDto dto = new GetBookingsDto();
        dto.setPage(0);
        dto.setSize(3);

        dao.find(dto);

        verify(typedQuery).setFirstResult(0);
        verify(typedQuery).setMaxResults(3);
        verify(em).close();
    }

    @Test
    void find_whenException_shouldWrapAndClose() {
        when(em.getCriteriaBuilder()).thenThrow(IllegalStateException.class);
        GetBookingsDto dto = new GetBookingsDto();
        dto.setPage(0);
        dto.setSize(1);
        assertThrows(RuntimeException.class, () -> dao.find(dto));
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Booking b = new Booking();
        dao.update(b);
        InOrder o = inOrder(emf, em, tx);
        o.verify(emf).createEntityManager();
        o.verify(em).getTransaction();
        o.verify(tx).begin();
        o.verify(em).merge(b);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenMergeThrows_shouldRollbackAndClose() {
        Booking b = new Booking();
        doThrow(RuntimeException.class).when(em).merge(b);
        when(tx.isActive()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> dao.update(b));
        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).merge(b);
        o.verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_whenExists_removesAndCommit() {
        Booking b = new Booking();
        when(emf.createEntityManager()).thenReturn(em);
        when(em.find(Booking.class, 9)).thenReturn(b);

        dao.delete(9);

        InOrder o = inOrder(em, tx);
        o.verify(tx).begin();
        o.verify(em).find(Booking.class, 9);
        o.verify(em).remove(b);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenNotExists_commitsWithoutRemove() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.find(Booking.class, 10)).thenReturn(null);

        dao.delete(10);

        InOrder o = inOrder(em, tx);
        o.verify(tx).begin();
        o.verify(em).find(Booking.class, 10);
        o.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenException_shouldRollbackAndClose() {
        doThrow(RuntimeException.class).when(em).find(Booking.class, 11);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(11));

        InOrder o = inOrder(tx, em);
        o.verify(tx).begin();
        o.verify(em).find(Booking.class, 11);
        o.verify(tx).rollback();
        verify(em).close();
    }
}
