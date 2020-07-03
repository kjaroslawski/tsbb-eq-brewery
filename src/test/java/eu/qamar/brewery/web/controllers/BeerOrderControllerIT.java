package eu.qamar.brewery.web.controllers;

import eu.qamar.brewery.domain.Customer;
import eu.qamar.brewery.repositories.CustomerRepository;
import eu.qamar.brewery.web.model.BeerOrderPagedList;
import eu.qamar.brewery.web.model.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerOrderControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("List Orders IT")
    void listOrdersTest() throws Exception {
        //given
        Customer customer = customerRepository.findAll().get(0);

        //then
        BeerOrderPagedList beerOrderPagedList = restTemplate.getForObject("/api/v1/customers/{customerId}/orders",
                                                                          BeerOrderPagedList.class, customer.getId());
        log.debug("IT listOrders result: {}", beerOrderPagedList.getContent().get(0));
        assertThat(beerOrderPagedList.getContent()).hasSize(1);
        assertEquals("testOrder1", beerOrderPagedList.getContent().get(0).getCustomerRef());
        assertEquals(OrderStatusEnum.NEW, beerOrderPagedList.getContent().get(0).getOrderStatus());
        assertThat(OrderStatusEnum.NEW).isEqualByComparingTo(beerOrderPagedList.getContent().get(0).getOrderStatus());
        assertThat(beerOrderPagedList.getContent().get(0).getBeerOrderLines()).hasSize(0);
    }

}