class PaymentDAOImplTest {

//    private Connection conn; private PreparedStatement ps; private ResultSet rs;
//    private PaymentDAOImpl dao;
//
//    @BeforeEach
//    void init() throws Exception {
//        conn=mock(Connection.class); ps=mock(PreparedStatement.class); rs=mock(ResultSet.class);
//        dao=new PaymentDAOImpl(conn);
//    }
//
//    @Test
//    void addPayment() throws Exception {
//        Payment p=new Payment(0,7,80.0,"CARD","PENDING", LocalDateTime.now());
//        when(conn.prepareStatement(anyString())).thenReturn(ps);
//
//        dao.addPayment(p);
//
//        verify(ps).setInt(1,7);
//        verify(ps).setDouble(2,80.0);
//        verify(ps).setString(3,"CARD");
//        verify(ps).setString(4,"PENDING");
//        verify(ps).setTimestamp(eq(5), any(Timestamp.class));
//        verify(ps).executeUpdate();
//    }
//
//    @Test
//    void getPaymentById_notFound() throws Exception {
//        when(conn.prepareStatement(anyString())).thenReturn(ps);
//        when(ps.executeQuery()).thenReturn(rs);
//        when(rs.next()).thenReturn(false);
//
//        assertNull(dao.getPaymentById(999));
//    }
//
//    @Test
//    void updatePaymentStatus() throws Exception {
//        when(conn.prepareStatement(anyString())).thenReturn(ps);
//        dao.updatePaymentStatus(3,"PAID");
//        verify(ps).setString(1,"PAID");
//        verify(ps).setInt(2,3);
//        verify(ps).executeUpdate();
//    }
}
