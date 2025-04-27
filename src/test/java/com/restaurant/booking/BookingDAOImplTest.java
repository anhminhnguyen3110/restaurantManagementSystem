package com.restaurant.booking;

import com.restaurant.daos.impl.BookingDAOImpl;
import com.restaurant.models.Booking;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<Booking> typedQuery;

    @Mock
    private Query nativeQuery;

    @InjectMocks
    private BookingDAOImpl dao;

    private final String findByPhoneJpql =
            "SELECT b FROM Booking b JOIN b.customer c WHERE c.phoneNumber = :phone";
    private final String findJpql = "SELECT b FROM Booking b";
    private final String nativeSql =
            "SELECT * FROM bookings b " +
                    "WHERE b.start_time < :to " +
                    "  AND TIMESTAMPADD(MINUTE, " +
                    "      CASE b.duration " +
                    "        WHEN 'HALF_HOUR' THEN 30 " +
                    "        WHEN 'ONE_HOUR' THEN 60 " +
                    "        WHEN 'ONE_AND_HALF_HOUR' THEN 90 " +
                    "        WHEN 'TWO_HOURS' THEN 120 " +
                    "        WHEN 'TWO_AND_HALF_HOUR' THEN 150 " +
                    "        WHEN 'THREE_HOURS' THEN 180 " +
                    "        ELSE 210 " +
                    "      END, b.start_time) > :from";

    @BeforeEach
    void setUp() {
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Booking booking = new Booking();
        dao.add(booking);
        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(booking);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenExceptionShouldRollbackAndClose() {
        Booking booking = new Booking();
        doThrow(new RuntimeException("fail")).when(em).persist(booking);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(booking));

        verify(tx).begin();
        verify(em).persist(booking);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Booking expected = new Booking();
        when(em.find(Booking.class, 42)).thenReturn(expected);

        Booking actual = dao.getById(42);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Booking.class, 42);
        verify(em).close();
    }

    @Test
    void findByCustomerPhone_shouldQueryReturnListAndClose() {
        List<Booking> list = List.of(new Booking(), new Booking());
        when(em.createQuery(findByPhoneJpql, Booking.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("phone", "555-1234")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Booking> result = dao.findByCustomerPhone("555-1234");

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findByPhoneJpql, Booking.class);
        verify(typedQuery).setParameter("phone", "555-1234");
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void find_shouldQueryReturnListAndClose() {
        List<Booking> list = List.of(new Booking());
        when(em.createQuery(findJpql, Booking.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Booking> result = dao.find();

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findJpql, Booking.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findInRange_shouldNativeQueryReturnListAndClose() {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        List<Booking> list = List.of(new Booking(), new Booking(), new Booking());

        when(em.createNativeQuery(nativeSql, Booking.class)).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("from", from)).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("to", to)).thenReturn(nativeQuery);
        when(nativeQuery.getResultList()).thenReturn(list);

        List<Booking> result = dao.findInRange(from, to);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createNativeQuery(nativeSql, Booking.class);
        verify(nativeQuery).setParameter("from", from);
        verify(nativeQuery).setParameter("to", to);
        verify(nativeQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findInRange_whenExceptionShouldClose() {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(1);

        when(em.createNativeQuery(nativeSql, Booking.class)).thenThrow(new RuntimeException("query failed"));

        assertThrows(RuntimeException.class, () -> dao.findInRange(from, to));

        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Booking booking = new Booking();
        dao.update(booking);
        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(booking);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_whenExceptionShouldRollbackAndClose() {
        Booking booking = new Booking();
        doThrow(new RuntimeException("fail")).when(em).merge(booking);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.update(booking));

        verify(tx).begin();
        verify(em).merge(booking);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        Booking booking = new Booking();
        when(em.find(Booking.class, 99)).thenReturn(booking);

        dao.delete(99);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Booking.class, 99);
        verify(em).remove(booking);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(Booking.class, 100)).thenReturn(null);

        dao.delete(100);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Booking.class, 100);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenExceptionShouldRollbackAndClose() {
        Booking booking = new Booking();
        when(em.find(Booking.class, 99)).thenReturn(booking);
        doThrow(new RuntimeException("fail")).when(em).remove(booking);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(99));

        verify(tx).begin();
        verify(em).find(Booking.class, 99);
        verify(em).remove(booking);
        verify(tx).rollback();
        verify(em).close();
    }
}