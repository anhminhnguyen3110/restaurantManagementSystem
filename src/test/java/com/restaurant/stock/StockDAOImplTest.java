package com.restaurant.stock;

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
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
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
    void add_whenPersistThrows_shouldRollbackAndClose() {
        Stock stock = new Stock();
        doThrow(new RuntimeException("fail")).when(em).persist(stock);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.add(stock));

        verify(tx).begin();
        verify(em).persist(stock);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void getById_shouldFindReturnAndClose() {
        Stock expected = new Stock();
        when(em.find(Stock.class, 5)).thenReturn(expected);

        Stock actual = dao.getById(5);

        assertSame(expected, actual);
        verify(emf).createEntityManager();
        verify(em).find(Stock.class, 5);
        verify(em).close();
    }

    @Test
    void findAll_shouldQueryReturnListAndClose() {
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
    void findBySupplier_shouldQueryReturnListAndClose() {
        Supplier sup = new Supplier();
        List<Stock> list = List.of(new Stock());
        when(em.createQuery(findBySupplierJpql, Stock.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("supplier", sup)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(list);

        List<Stock> result = dao.findBySupplier(sup);

        assertEquals(list, result);
        verify(emf).createEntityManager();
        verify(em).createQuery(findBySupplierJpql, Stock.class);
        verify(typedQuery).setParameter("supplier", sup);
        verify(typedQuery).getResultList();
        verify(em).close();
    }

    @Test
    void findLowStock_shouldQueryReturnListAndClose() {
        List<Stock> list = List.of();
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
    void update_whenMergeThrows_shouldRollbackAndClose() {
        Stock stock = new Stock();
        doThrow(new RuntimeException("err")).when(em).merge(stock);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.update(stock));

        verify(tx).begin();
        verify(em).merge(stock);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void delete_shouldRemoveWhenFoundCommitAndClose() {
        int id = 11;
        Stock stock = new Stock();
        when(em.find(Stock.class, id)).thenReturn(stock);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Stock.class, id);
        verify(em).remove(stock);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_shouldNotRemoveWhenNotFoundButStillCommitAndClose() {
        int id = 12;
        when(em.find(Stock.class, id)).thenReturn(null);

        dao.delete(id);

        verify(emf).createEntityManager();
        verify(em).getTransaction();
        verify(tx).begin();
        verify(em).find(Stock.class, id);
        verify(em, never()).remove(any());
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_whenFindThrows_shouldRollbackAndClose() {
        int id = 13;
        doThrow(new RuntimeException("boom")).when(em).find(Stock.class, id);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> dao.delete(id));

        verify(tx).begin();
        verify(em).find(Stock.class, id);
        verify(tx).rollback();
        verify(em).close();
    }
}
