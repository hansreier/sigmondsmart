package etorg.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OrderTest {

    private Order order;

    @Before
    public void DefineOrder() {
        order = new Order();
        order.setOrderId(3355);
        order.setComment("Ola");
    }

    @Test
    public void TestOrder() {
        order.getComment();
        Assert.assertTrue(order.getComment().equals("Ola"));
    }


}
