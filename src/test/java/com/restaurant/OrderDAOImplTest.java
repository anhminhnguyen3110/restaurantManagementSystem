class OrderDAOImplTest {
//    private Connection mockConnection;
//    private PreparedStatement mockStmt;
//    private ResultSet mockRs;
//    private OrderDAOImpl orderDAO;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        mockConnection = mock(Connection.class);
//        mockStmt = mock(PreparedStatement.class);
//        mockRs = mock(ResultSet.class);
//
//        orderDAO = new OrderDAOImpl(mockConnection);
//    }
//
//    @Test
//    void testAddOrder() throws Exception {
//        Order order = new Order(1, 100, Arrays.asList(1, 2, 3), "PENDING", 45.0);
//
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStmt);
//
//        orderDAO.addOrder(order);
//
//        verify(mockStmt).setInt(1, 100);
//        verify(mockStmt).setString(2, "1,2,3");
//        verify(mockStmt).setString(3, "PENDING");
//        verify(mockStmt).setDouble(4, 45.0);
//        verify(mockStmt).executeUpdate();
//    }
//
//    @Test
//    void testGetOrderById() throws Exception {
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStmt);
//        when(mockStmt.executeQuery()).thenReturn(mockRs);
//
//        when(mockRs.next()).thenReturn(true);
//        when(mockRs.getInt("id")).thenReturn(1);
//        when(mockRs.getInt("booking_id")).thenReturn(100);
//        when(mockRs.getString("menu_item_ids")).thenReturn("1,2");
//        when(mockRs.getString("status")).thenReturn("COMPLETED");
//        when(mockRs.getDouble("total_price")).thenReturn(29.99);
//
//        Order result = orderDAO.getOrderById(1);
//
//        assertNotNull(result);
//        assertEquals(1, result.getId());
//        assertEquals(100, result.getBookingId());
//        assertEquals(Arrays.asList(1, 2), result.getMenuItemIds());
//        assertEquals("COMPLETED", result.getStatus());
//        assertEquals(29.99, result.getTotalPrice(), 0.01);
//    }
//
//    @Test
//    void testUpdateOrderStatus() throws Exception {
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStmt);
//
//        orderDAO.updateOrderStatus(1, "PROCESSING");
//
//        verify(mockStmt).setString(1, "PROCESSING");
//        verify(mockStmt).setInt(2, 1);
//        verify(mockStmt).executeUpdate();
//    }
//
//    @Test
//    void testDeleteOrder() throws Exception {
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStmt);
//
//        orderDAO.deleteOrder(1);
//
//        verify(mockStmt).setInt(1, 1);
//        verify(mockStmt).executeUpdate();
//    }
//
//    @Test
//    void testGetOrderById_NotFound() throws Exception {
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStmt);
//        when(mockStmt.executeQuery()).thenReturn(mockRs);
//        when(mockRs.next()).thenReturn(false);
//
//        Order result = orderDAO.getOrderById(404);
//
//        assertNull(result);
//    }
}
