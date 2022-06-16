package web.comand.impl;

import dal.entity.Locality;
import dto.DeliveryInfoRequestDto;
import dto.LocaliseLocalityDto;
import dto.PriceAndTimeOnDeliveryDto;
import logiclayer.service.DeliveryService;
import logiclayer.service.LocalityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static constants.TestConstant.getLocaleEn;
import static constants.TestConstant.getLocalitySend;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static web.constant.AttributeConstants.SESSION_LANG;
import static web.constant.PageConstance.COUNTER_FILE_NAME;
import static web.constant.PageConstance.MAIN_WEB_FOLDER;

@RunWith(MockitoJUnitRunner.class)
public class CounterControllerTest {

    private static final String DELIVERY_WEIGHT = "deliveryWeight";
    private static final String LOCALITY_GET_ID = "localityGetID";
    private static final String LOCALITY_SAND_ID = "localitySandID";
    @InjectMocks
    CounterController counterController;
    @Mock
    DeliveryService deliveryService;
    @Mock
    LocalityService localityService;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    private HttpSession session;

    @Before
    public void setUp() throws Exception {
        when(httpServletRequest.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());
        when(httpServletRequest.getParameter(DELIVERY_WEIGHT)).thenReturn("1");
        when(httpServletRequest.getParameter(LOCALITY_GET_ID)).thenReturn("1");
        when(httpServletRequest.getParameter(LOCALITY_SAND_ID)).thenReturn("1");
    }

    @Test
    public void performGet() {
        Locality locality = getLocalitySend();
        LocaliseLocalityDto localiseLocalityDto = getLocaliseLocalityDtoEn(locality);
        List<LocaliseLocalityDto> localities = Collections.singletonList(localiseLocalityDto);
        when(localityService.getLocaliseLocalities(any(Locale.class))).thenReturn(localities);

        String result = counterController.doGet(httpServletRequest);

        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(1)).getSession();
        verify(session, times(1)).getAttribute(SESSION_LANG);
        assertEquals(MAIN_WEB_FOLDER + COUNTER_FILE_NAME, result);
    }


    @Test
    public void performPostAllCorrect() {
        DeliveryInfoRequestDto deliveryInfoRequestDto = getDeliveryInfoRequestDto();
        PriceAndTimeOnDeliveryDto priceAndTimeOnDeliveryDto = getPriceAndTimeOnDeliveryDto();
        when(deliveryService.getDeliveryCostAndTimeDto(any(DeliveryInfoRequestDto.class))).thenReturn(Optional.of(priceAndTimeOnDeliveryDto));
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());


        String result = counterController.doPost(httpServletRequest);

        verify(httpServletRequest, times(2)).getParameter(DELIVERY_WEIGHT);
        verify(httpServletRequest, times(2)).getParameter(LOCALITY_GET_ID);
        verify(httpServletRequest, times(2)).getParameter(LOCALITY_SAND_ID);
        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(1)).getSession();
        verify(session, times(1)).getAttribute(SESSION_LANG);
        verify(deliveryService, times(1)).getDeliveryCostAndTimeDto(any(DeliveryInfoRequestDto.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        assertEquals(MAIN_WEB_FOLDER + COUNTER_FILE_NAME, result);
    }

    @Test
    public void performPostIncorrectInputDeliveryWeightZero() {
        PriceAndTimeOnDeliveryDto priceAndTimeOnDeliveryDto = getPriceAndTimeOnDeliveryDto();
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());
        when(httpServletRequest.getParameter(LOCALITY_GET_ID)).thenReturn("0");

        String result = counterController.doPost(httpServletRequest);

        verify(httpServletRequest, times(1)).getParameter(DELIVERY_WEIGHT);
        verify(httpServletRequest, times(1)).getParameter(LOCALITY_GET_ID);
        verify(httpServletRequest, times(0)).getParameter(LOCALITY_SAND_ID);
        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(1)).getSession();
        verify(httpServletRequest, times(1)).getParameter(DELIVERY_WEIGHT);
        verify(deliveryService, times(0)).getDeliveryCostAndTimeDto(any(DeliveryInfoRequestDto.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        assertEquals(MAIN_WEB_FOLDER + COUNTER_FILE_NAME, result);
    }

    @Test
    public void performPostIncorrectInputDeliverySendZero() {
        PriceAndTimeOnDeliveryDto priceAndTimeOnDeliveryDto = getPriceAndTimeOnDeliveryDto();
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());
        when(httpServletRequest.getParameter(LOCALITY_SAND_ID)).thenReturn("0");

        String result = counterController.doPost(httpServletRequest);

        verify(httpServletRequest, times(1)).getParameter(DELIVERY_WEIGHT);
        verify(httpServletRequest, times(1)).getParameter(LOCALITY_GET_ID);
        verify(httpServletRequest, times(1)).getParameter(LOCALITY_SAND_ID);
        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(1)).getSession();
        verify(httpServletRequest, times(1)).getParameter(DELIVERY_WEIGHT);
        verify(deliveryService, times(0)).getDeliveryCostAndTimeDto(any(DeliveryInfoRequestDto.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        assertEquals(MAIN_WEB_FOLDER + COUNTER_FILE_NAME, result);
    }

    @Test
    public void performPostIncorrectInputGetZero() {
        PriceAndTimeOnDeliveryDto priceAndTimeOnDeliveryDto = getPriceAndTimeOnDeliveryDto();
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());
        when(httpServletRequest.getParameter(DELIVERY_WEIGHT)).thenReturn("0");

        String result = counterController.doPost(httpServletRequest);

        verify(httpServletRequest, times(1)).getParameter(DELIVERY_WEIGHT);
        verify(httpServletRequest, times(0)).getParameter(LOCALITY_GET_ID);
        verify(httpServletRequest, times(0)).getParameter(LOCALITY_SAND_ID);
        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(1)).getSession();
        verify(httpServletRequest, times(1)).getParameter(DELIVERY_WEIGHT);
        verify(deliveryService, times(0)).getDeliveryCostAndTimeDto(any(DeliveryInfoRequestDto.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        assertEquals(MAIN_WEB_FOLDER + COUNTER_FILE_NAME, result);
    }

    @Test
    public void performPostIncorrectData() {
        PriceAndTimeOnDeliveryDto priceAndTimeOnDeliveryDto = getPriceAndTimeOnDeliveryDto();
        when(deliveryService.getDeliveryCostAndTimeDto(any(DeliveryInfoRequestDto.class))).thenReturn(Optional.empty());
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());

        String result = counterController.doPost(httpServletRequest);

        verify(httpServletRequest, times(2)).getParameter(DELIVERY_WEIGHT);
        verify(httpServletRequest, times(2)).getParameter(LOCALITY_GET_ID);
        verify(httpServletRequest, times(2)).getParameter(LOCALITY_SAND_ID);
        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(1)).getSession();
        verify(httpServletRequest, times(2)).getParameter(DELIVERY_WEIGHT);
        verify(deliveryService, times(1)).getDeliveryCostAndTimeDto(any(DeliveryInfoRequestDto.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        assertEquals(MAIN_WEB_FOLDER + COUNTER_FILE_NAME, result);
    }


    private DeliveryInfoRequestDto getDeliveryInfoRequestDto() {
        return DeliveryInfoRequestDto.builder()
                .deliveryWeight(10)
                .localityGetID(1)
                .localitySandID(2)
                .build();
    }

    private PriceAndTimeOnDeliveryDto getPriceAndTimeOnDeliveryDto() {
        return PriceAndTimeOnDeliveryDto.builder()
                .costInCents(1)
                .timeOnWayInHours(1)
                .build();
    }

    private LocaliseLocalityDto getLocaliseLocalityDtoEn(Locality locality) {
        return LocaliseLocalityDto.builder()
                .id(locality.getId())
                .name(locality.getNameEn()).build();
    }

    private LocaliseLocalityDto getLocaliseLocalityDtoRu(Locality locality) {
        return LocaliseLocalityDto.builder()
                .id(locality.getId())
                .name(locality.getNameRu()).build();
    }
}