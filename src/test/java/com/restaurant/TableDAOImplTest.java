class TableDAOImplTest {
//
//    private Connection conn;
//    private PreparedStatement ps;
//    private ResultSet rs;
//    private TableDAOImpl dao;
//
//    @BeforeEach
//    void init() throws Exception {
//        conn = mock(Connection.class);
//        ps   = mock(PreparedStatement.class);
//        rs   = mock(ResultSet.class);
//        dao  = new TableDAOImpl(conn);
//    }
//
//    @Test
//    void addTable() throws Exception {
//        RestaurantTable t = new RestaurantTable(0, 8, 4, 3, 1, true);
//        when(conn.prepareStatement(anyString())).thenReturn(ps);
//
//        dao.addTable(t);
//
//        verify(ps).setInt(1, 8);
//        verify(ps).setInt(2, 4);
//        verify(ps).setInt(3, 3);
//        verify(ps).setInt(4, 1);
//        verify(ps).setBoolean(5, true);
//        verify(ps).executeUpdate();
//    }
//
//    @Test
//    void getTableById() throws Exception {
//        when(conn.prepareStatement(anyString())).thenReturn(ps);
//        when(ps.executeQuery()).thenReturn(rs);
//        when(rs.next()).thenReturn(true);
//        when(rs.getInt("id")).thenReturn(2);
//        when(rs.getInt("number")).thenReturn(5);
//        when(rs.getInt("capacity")).thenReturn(6);
//        when(rs.getInt("x_coord")).thenReturn(4);
//        when(rs.getInt("y_coord")).thenReturn(0);
//        when(rs.getBoolean("available")).thenReturn(false);
//
//        RestaurantTable t = dao.getTableById(2);
//
//        assertNotNull(t);
//        assertEquals(4, t.getX());
//        assertEquals(0, t.getY());
//        assertFalse(t.isAvailable());
//    }
//
//    @Test
//    void updateAvailability() throws Exception {
//        when(conn.prepareStatement(anyString())).thenReturn(ps);
//        dao.updateTableAvailability(7, true);
//        verify(ps).setBoolean(1, true);
//        verify(ps).setInt(2, 7);
//        verify(ps).executeUpdate();
//    }
//
//    @Test
//    void deleteTable() throws Exception {
//        when(conn.prepareStatement(anyString())).thenReturn(ps);
//        dao.deleteTable(9);
//        verify(ps).setInt(1, 9);
//        verify(ps).executeUpdate();
//    }
}
