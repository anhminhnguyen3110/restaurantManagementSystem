package com.restaurant.daos;

import com.restaurant.constants.BookingTimeSlot;
import com.restaurant.daos.impl.RestaurantTableDAOImpl;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableForBookingDto;
import com.restaurant.models.RestaurantTable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RestaurantTableDAOImplTest {

    @Mock
    EntityManagerFactory emf;
    @Mock
    EntityManager em;
    @Mock
    EntityTransaction tx;
    @Mock
    TypedQuery<RestaurantTable> qFind;
    @Mock
    TypedQuery<RestaurantTable> qBooking;
    @Mock
    TypedQuery<RestaurantTable> qAvailable;
    @Mock
    TypedQuery<Long> countQ;
    @InjectMocks
    RestaurantTableDAOImpl dao;

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        when(em.createQuery(startsWith("SELECT t FROM RestaurantTable t WHERE t.restaurant.id = :rid"), eq(RestaurantTable.class))).thenReturn(qFind);
        when(em.createQuery(contains("Booking b"), eq(RestaurantTable.class))).thenReturn(qBooking);
        when(em.createQuery(eq("SELECT t FROM RestaurantTable t WHERE t.restaurant.id = :rid AND t.available = true"), eq(RestaurantTable.class))).thenReturn(qAvailable);
        when(qFind.setParameter(eq("rid"), anyInt())).thenReturn(qFind);
        when(qBooking.setParameter(eq("rid"), anyInt())).thenReturn(qBooking);
        when(qBooking.setParameter(eq("date"), any(LocalDate.class))).thenReturn(qBooking);
        when(qBooking.setParameter(eq("startTime"), any())).thenReturn(qBooking);
        when(qBooking.setParameter(eq("endTime"), any())).thenReturn(qBooking);
        when(qAvailable.setParameter(eq("rid"), anyInt())).thenReturn(qAvailable);
        when(em.createQuery(startsWith("SELECT COUNT(t)"), eq(Long.class))).thenReturn(countQ);
        when(countQ.setParameter(anyString(), any())).thenReturn(countQ);
    }

    @Test
    void add_shouldPersistAndClose() {
        RestaurantTable rt = new RestaurantTable();
        dao.add(rt);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).persist(rt);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void find_byRestaurant_returnsAndClose() {
        GetRestaurantTableDto dto = new GetRestaurantTableDto();
        dto.setRestaurantId(3);
        List<RestaurantTable> list = List.of(new RestaurantTable());
        when(qFind.getResultList()).thenReturn(list);
        List<RestaurantTable> result = dao.find(dto);
        verify(qFind).setParameter("rid", 3);
        assertEquals(list, result);
        verify(em).close();
    }

    @Test
    void findForBooking_filtersAndClose() {
        GetRestaurantTableForBookingDto dto = new GetRestaurantTableForBookingDto();
        dto.setRestaurantId(4);
        dto.setDate(LocalDate.of(2025, 5, 1));
        dto.setStartTime(BookingTimeSlot.SLOT_10_00);
        dto.setEndTime(BookingTimeSlot.SLOT_11_00);
        List<RestaurantTable> list = List.of(new RestaurantTable());
        when(qBooking.getResultList()).thenReturn(list);
        List<RestaurantTable> result = dao.findForBooking(dto);
        verify(qBooking).setParameter("rid", 4);
        verify(qBooking).setParameter("date", dto.getDate());
        verify(qBooking).setParameter("startTime", dto.getStartTime());
        verify(qBooking).setParameter("endTime", dto.getEndTime());
        assertEquals(list, result);
        verify(em).close();
    }

    @Test
    void getById_returnsAndClose() {
        RestaurantTable rt = new RestaurantTable();
        when(em.find(RestaurantTable.class, 7)).thenReturn(rt);
        RestaurantTable result = dao.getById(7);
        assertSame(rt, result);
        verify(em).close();
    }

    @Test
    void update_mergesAndClose() {
        RestaurantTable rt = new RestaurantTable();
        dao.update(rt);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).merge(rt);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_existing_removesAndClose() {
        RestaurantTable rt = new RestaurantTable();
        when(em.find(RestaurantTable.class, 9)).thenReturn(rt);
        dao.delete(9);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(RestaurantTable.class, 9);
        in.verify(em).remove(rt);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_nonExisting_commitsAndClose() {
        when(em.find(RestaurantTable.class, 10)).thenReturn(null);
        dao.delete(10);
        InOrder in = inOrder(em, tx);
        in.verify(em).getTransaction();
        in.verify(tx).begin();
        in.verify(em).find(RestaurantTable.class, 10);
        in.verify(tx).commit();
        verify(em).close();
    }

    @Test
    void existsByRestaurantIdAndStartPosition_checksWithoutExclude() {
        when(countQ.getSingleResult()).thenReturn(1L);
        assertTrue(dao.existsByRestaurantIdAndStartPosition(2, 5, 6, null));
        verify(countQ).setParameter("rid", 2);
        verify(countQ).setParameter("startX", 5);
        verify(countQ).setParameter("startY", 6);
        verify(em).close();
    }

    @Test
    void existsByRestaurantIdAndStartPosition_checksWithExclude() {
        when(countQ.getSingleResult()).thenReturn(0L);
        assertFalse(dao.existsByRestaurantIdAndStartPosition(2, 5, 6, 8));
        verify(countQ).setParameter("rid", 2);
        verify(countQ).setParameter("startX", 5);
        verify(countQ).setParameter("startY", 6);
        verify(countQ).setParameter("eid", 8);
        verify(em).close();
    }

    @Test
    void existsByRestaurantIdAndEndPosition() {
        when(countQ.getSingleResult()).thenReturn(1L);
        assertTrue(dao.existsByRestaurantIdAndEndPosition(3, 7, 8, null));
        verify(countQ).setParameter("rid", 3);
        verify(countQ).setParameter("endX", 7);
        verify(countQ).setParameter("endY", 8);
        verify(em).close();
    }

    @Test
    void existsByRestaurantIdAndNumber() {
        when(countQ.getSingleResult()).thenReturn(0L);
        assertFalse(dao.existsByRestaurantIdAndNumber(4, 2, 5));
        verify(countQ).setParameter("rid", 4);
        verify(countQ).setParameter("num", 2);
        verify(countQ).setParameter("eid", 5);
        verify(em).close();
    }

    @Test
    void findTablesForOrder_returnsAndClose() {
        List<RestaurantTable> list = List.of(new RestaurantTable());
        when(qAvailable.getResultList()).thenReturn(list);
        List<RestaurantTable> result = dao.findTablesForOrder(6);
        verify(qAvailable).setParameter("rid", 6);
        assertEquals(list, result);
        verify(em).close();
    }
}
