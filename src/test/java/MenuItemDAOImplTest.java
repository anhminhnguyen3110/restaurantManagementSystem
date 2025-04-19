import com.restaurant.daos.impl.MenuItemDAOImpl;
import com.restaurant.models.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuItemDAOImplTest {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    private MenuItemDAOImpl dao;

    @BeforeEach
    void setUp() throws Exception {
        conn = mock(Connection.class);
        ps   = mock(PreparedStatement.class);
        rs   = mock(ResultSet.class);
        dao  = new MenuItemDAOImpl(conn);
    }

    @Test
    void testAddMenuItem() throws Exception {
        MenuItem item = new MenuItem(0,"Pizza","Cheesy",15.5,true);
        when(conn.prepareStatement(anyString())).thenReturn(ps);

        dao.addMenuItem(item);

        verify(ps).setString(1,"Pizza");
        verify(ps).setString(2,"Cheesy");
        verify(ps).setDouble(3,15.5);
        verify(ps).setBoolean(4,true);
        verify(ps).executeUpdate();
    }

    @Test
    void testGetMenuItemById() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("Burger");
        when(rs.getString("description")).thenReturn("Beef");
        when(rs.getDouble("price")).thenReturn(10.0);
        when(rs.getBoolean("available")).thenReturn(false);

        MenuItem item = dao.getMenuItemById(1);

        assertNotNull(item);
        assertEquals("Burger", item.getName());
        assertFalse(item.isAvailable());
    }

    @Test
    void testDeleteMenuItem() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        dao.deleteMenuItem(7);
        verify(ps).setInt(1,7);
        verify(ps).executeUpdate();
    }
}
