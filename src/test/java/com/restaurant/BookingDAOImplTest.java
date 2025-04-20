class BookingDAOImplTest {

//    private Connection mockConnection;
//    private PreparedStatement mockStmt;
//    private ResultSet mockRs;
//    private BookingDAOImpl bookingDAO;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        mockConnection = mock(Connection.class);
//        mockStmt = mock(PreparedStatement.class);
//        mockRs = mock(ResultSet.class);
//        bookingDAO = new BookingDAOImpl(mockConnection);
//    }
//
//    @Test
//    void testAddBooking() throws Exception {
//        LocalDateTime start = LocalDateTime.of(2025, 5, 1, 18, 0);
//        Booking booking = new Booking(1, "John Doe", "1234567890",
//                start, BookingDuration.TWO_HOURS, 4);
//
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStmt);
//
//        bookingDAO.addBooking(booking);
//
//        verify(mockStmt).setString(1, "John Doe");
//        verify(mockStmt).setString(2, "1234567890");
//        verify(mockStmt).setTimestamp(eq(3), any(Timestamp.class));
//        verify(mockStmt).setInt(4, BookingDuration.TWO_HOURS.getMinutes());
//        verify(mockStmt).setInt(5, 4);
//        verify(mockStmt).executeUpdate();
//    }
//
//    @Test
//    void testGetBookingByPhone() throws Exception {
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStmt);
//        when(mockStmt.executeQuery()).thenReturn(mockRs);
//
//        when(mockRs.next()).thenReturn(true);
//        when(mockRs.getInt("id")).thenReturn(1);
//        when(mockRs.getString("customer_name")).thenReturn("Jane Doe");
//        when(mockRs.getString("phone_number")).thenReturn("9998887777");
//        when(mockRs.getTimestamp("start_time"))
//                .thenReturn(Timestamp.valueOf(LocalDateTime.of(2025, 5, 2, 19, 0)));
//        when(mockRs.getInt("duration_minutes"))
//                .thenReturn(BookingDuration.ONE_AND_HALF_HOUR.getMinutes());
//        when(mockRs.getInt("table_id")).thenReturn(2);
//
//        Booking result = bookingDAO.getBookingByPhone("9998887777");
//
//        assertNotNull(result);
//        assertEquals("Jane Doe", result.getCustomerName());
//        assertEquals("9998887777", result.getPhoneNumber());
//        assertEquals(BookingDuration.ONE_AND_HALF_HOUR, result.getDuration());
//    }
//
//    @Test
//    void testUpdateBooking() throws Exception {
//        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 20, 0);
//        Booking booking = new Booking(1, "Updated Name", "9999999999",
//                start, BookingDuration.TWO_AND_HALF_HOUR, 5);
//
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStmt);
//
//        bookingDAO.updateBooking(booking);
//
//        verify(mockStmt).setString(1, "Updated Name");
//        verify(mockStmt).setString(2, "9999999999");
//        verify(mockStmt).setTimestamp(eq(3), any(Timestamp.class));
//        verify(mockStmt).setInt(4, BookingDuration.TWO_AND_HALF_HOUR.getMinutes());
//        verify(mockStmt).setInt(5, 5);
//        verify(mockStmt).setInt(6, 1);
//        verify(mockStmt).executeUpdate();
//    }
//
//    @Test
//    void testDeleteBooking() throws Exception {
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStmt);
//
//        bookingDAO.deleteBooking(1);
//
//        verify(mockStmt).setInt(1, 1);
//        verify(mockStmt).executeUpdate();
//    }
//
//    @Test
//    void testGetBookingById_NotFound() throws Exception {
//        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockStmt);
//        when(mockStmt.executeQuery()).thenReturn(mockRs);
//        when(mockRs.next()).thenReturn(false);
//
//        Booking result = bookingDAO.getBookingById(999);
//
//        assertNull(result);
//    }
}