package etorg.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderTest {

    private Order order;

    @BeforeEach
    public void DefineOrder() {
        order = new Order();
        order.setOrderId(3355);
        order.setComment("Ola");
    }

    @Test
    public void TestOrder() {
        order.getComment();
        assertTrue(order.getComment().equals("Ola"));
    }
}
