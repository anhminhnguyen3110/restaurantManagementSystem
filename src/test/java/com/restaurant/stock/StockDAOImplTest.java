package com.restaurant;

import com.restaurant.daos.impl.StockDAOImpl;
import com.restaurant.models.Stock;
import com.restaurant.models.Supplier;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockDAOImplTest {

    @Mock
    private EntityManagerFactory emf;

    @Mock
    private EntityManager em;

    @Mock
    private EntityTransaction tx;

    @Mock
    private TypedQuery<Stock> typedQuery;

    @InjectMocks
    private StockDAOImpl dao;

    private final String findAllJpql = "SELECT s FROM Stock s";
    private final String findBySupplierJpql = "SELECT s FROM Stock s WHERE s.supplier = :supplier";
    private final String findLowStockJpql = "SELECT s FROM Stock s WHERE s.quantity <= s.minThreshold";

    @BeforeEach
    void setUp() {
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void add_shouldPersistCommitAndClose() {
        Stock stock = new Stock();
        dao.add(stock);
        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).persist(stock);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void add_whenException_shouldRollbackAndClose() {
        Stock stock = new Stock();
        doThrow(new RuntimeException("persist failed")).when(em).persist(stock);
        assertThrows(RuntimeException.class, () -> dao.add(stock));
        verify(tx).begin();
        verify(em).persist(stock);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Stock expected = new Stock();
        when(em.find(Stock.class, 11)).thenReturn(expected);
        Stock actual = dao.getById(11);
        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Stock.class, 11);
        verify(em).close();
    }

    @Test
    void findAll_shouldReturnListAndClose() {
        List<Stock> list = List.of(new Stock(), new Stock());
        when(em.createQuery(findAllJpql, Stock.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);
        List<Stock> result = dao.findAll();
        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findAllJpql, Stock.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findBySupplier_shouldReturnListAndClose() {
        Supplier supplier = new Supplier();
        List<Stock> list = List.of(new Stock());
        when(em.createQuery(findBySupplierJpql, Stock.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("supplier", supplier)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);
        List<Stock> result = dao.findBySupplier(supplier);
        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findBySupplierJpql, Stock.class);
        verify(typedQuery).setParameter("supplier", supplier);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findLowStock_shouldReturnListAndClose() {
        List<Stock> list = List.of(new Stock());
        when(em.createQuery(findLowStockJpql, Stock.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);
        List<Stock> result = dao.findLowStock();
        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findLowStockJpql, Stock.class);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void update_shouldMergeCommitAndClose() {
        Stock stock = new Stock();
        dao.update(stock);
        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).merge(stock);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        Stock stock = new Stock();
        when(em.find(Stock.class, 22)).thenReturn(stock);
        dao.delete(22);
        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Stock.class, 22);
        verify(em).remove(stock);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        when(em.find(Stock.class, 33)).thenReturn(null);
        dao.delete(33);
        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Stock.class, 33);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }
}
