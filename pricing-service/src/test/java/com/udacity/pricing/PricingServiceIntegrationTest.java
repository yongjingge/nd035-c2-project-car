package com.udacity.pricing;

import com.udacity.pricing.entity.Price;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String baseUrl = "http://localhost:";

    @Test
    public void testGetAllPrices () {
        ResponseEntity<Price> res =
                this.testRestTemplate.getForEntity(baseUrl + port + "/prices", Price.class);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testGetPriceByID () {
        ResponseEntity<Price> res =
                this.testRestTemplate.getForEntity(baseUrl + port + "/prices/2", Price.class);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody().getPrice().intValue(), 4129);
    }
}
