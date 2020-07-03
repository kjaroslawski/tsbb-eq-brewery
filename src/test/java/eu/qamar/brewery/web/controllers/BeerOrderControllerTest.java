package eu.qamar.brewery.web.controllers;

import eu.qamar.brewery.services.BeerOrderService;
import eu.qamar.brewery.web.model.BeerDto;
import eu.qamar.brewery.web.model.BeerOrderDto;
import eu.qamar.brewery.web.model.BeerOrderLineDto;
import eu.qamar.brewery.web.model.BeerOrderPagedList;
import eu.qamar.brewery.web.model.BeerStyleEnum;
import eu.qamar.brewery.web.model.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    @MockBean
    BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;

    BeerDto validBeer;
    BeerOrderDto validOrder;
    BeerOrderPagedList beerOrderPagedList;

    @AfterEach
    void tearDown() {
        reset(beerOrderService);
    }

    @BeforeEach
    void setUp() {
        validBeer = BeerDto.builder().id(UUID.randomUUID())
                           .version(1)
                           .beerName("Beer1")
                           .beerStyle(BeerStyleEnum.PALE_ALE)
                           .price(new BigDecimal("12.99"))
                           .quantityOnHand(4)
                           .upc(123456789012L)
                           .createdDate(OffsetDateTime.now())
                           .lastModifiedDate(OffsetDateTime.now())
                           .build();

        validOrder = BeerOrderDto.builder()
                                 .id(UUID.randomUUID())
                                 .customerId(UUID.randomUUID())
                                 .customerRef("123")
                                 .beerOrderLines(Collections.emptyList())
                                 .orderStatus(OrderStatusEnum.NEW)
                                 .beerOrderLines(List.of(BeerOrderLineDto.builder()
                                                                         .beerId(validBeer.getId())
                                                                         .build()))
                                 .createdDate(OffsetDateTime.now())
                                 .lastModifiedDate(OffsetDateTime.now())
                                 .version(2)
                                 .build();
        List<BeerOrderDto> beerOrderDtos = new ArrayList<>();
        beerOrderDtos.add(validOrder);
        beerOrderPagedList = new BeerOrderPagedList(beerOrderDtos);
    }

    @Test
    void listOrdersTest() throws Exception {
        //given
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        given(beerOrderService.listOrders(any(), any())).willReturn(beerOrderPagedList);
        //then
        MvcResult result = mockMvc.perform(get("/api/v1/customers/{customerId}/orders", UUID.randomUUID()))
                                  .andExpect(status().isOk())
                                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(jsonPath("$.content[0].orderStatus", is(beerOrderPagedList.getContent()
                                                                                                       .get(0)
                                                                                                       .getOrderStatus()
                                                                                                       .toString())))
                                  .andExpect(jsonPath("$.content[0].version", is(2)))
                                  .andExpect(jsonPath("$.content[0].beerOrderLines[0].beerId", is(validBeer.getId().toString())))
                                  .andExpect(jsonPath("$.content[0].createdDate",
                                                      Is.is(dateTimeFormatter.format(validOrder.getCreatedDate()))))
                                  .andReturn();
        verify(beerOrderService, atMostOnce()).listOrders(any(), any());
        log.debug("Result: {}", result.getResponse().getContentAsString());
    }

    @Test
    void getOrderTest() throws Exception {
        //given
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        given(beerOrderService.getOrderById(any(), any())).willReturn(validOrder);
        //then
        MvcResult result = mockMvc.perform(
                get("/api/v1/customers/{customerId}/orders/{orderId}", UUID.randomUUID(), UUID.randomUUID()))
                                  .andExpect(status().isOk())
                                  .andExpect(jsonPath("$.orderStatus", is(validOrder.getOrderStatus().toString())))
                                  .andExpect(jsonPath("$.createdDate",
                                                      Is.is(dateTimeFormatter.format(validOrder.getCreatedDate()))))
                                  .andReturn();
        verify(beerOrderService, times(1)).getOrderById(any(), any());
        log.debug("getOrder result: {}", result.getResponse().getContentAsString());
    }
}