package eu.qamar.brewery.web.controllers;

import eu.qamar.brewery.services.BeerService;
import eu.qamar.brewery.web.model.BeerDto;
import eu.qamar.brewery.web.model.BeerPagedList;
import eu.qamar.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BeerControllerTest {

    @Mock
    BeerService beerService;

    @InjectMocks
    BeerController beerController;

    MockMvc mockMvc;

    BeerDto validBeer;

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

        mockMvc = MockMvcBuilders.standaloneSetup(beerController).build();
    }

    @Test
    void getBeerById() throws Exception {
        //given
        given(beerService.findBeerById(any())).willReturn(validBeer);
        //then
        mockMvc.perform(get("/api/v1/beer/" + validBeer.getId()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id", is(validBeer.getId().toString())))
               .andExpect(jsonPath("$.beerName", is("Beer1")));
    }

    @DisplayName("List Ops - ")
    @Nested
    public class TestListOperations {

        @Captor
        ArgumentCaptor<String> beerNameCaptor;

        @Captor
        ArgumentCaptor<BeerStyleEnum> beerStyleEnumArgumentCaptor;

        @Captor
        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor;

        BeerPagedList beerPagedList;

        @BeforeEach
        void setUp() {
            List<BeerDto> beers = new ArrayList<>();
            beers.add(validBeer);
            beers.add(BeerDto.builder().id(UUID.randomUUID())
                             .version(1)
                             .beerName("Beer4")
                             .beerStyle(BeerStyleEnum.PALE_ALE)
                             .price(new BigDecimal("12.99"))
                             .quantityOnHand(66)
                             .upc(123123123123L)
                             .createdDate(OffsetDateTime.now())
                             .lastModifiedDate(OffsetDateTime.now())
                             .build());

            beerPagedList = new BeerPagedList(beers, PageRequest.of(1, 1), 2L);

            given(beerService.listBeers(beerNameCaptor.capture(), beerStyleEnumArgumentCaptor.capture(),
                                        pageRequestArgumentCaptor.capture())).willReturn(beerPagedList);
        }

        @DisplayName("Test list beers - no parameters")
        @Test
        void testListBeers() throws Exception {
            mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andExpect(jsonPath("$.content", hasSize(2)))
                   .andExpect(jsonPath("$.content[0].id", is(validBeer.getId().toString())));
        }
    }

}