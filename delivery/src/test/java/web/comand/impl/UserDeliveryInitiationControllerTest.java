package web.comand.impl;

import dal.entity.Locality;
import dto.DeliveryInfoRequestDto;
import dto.DeliveryOrderCreateDto;
import dto.LocaliseLocalityDto;
import dto.PriceAndTimeOnDeliveryDto;
import dto.validation.Validator;
import logiclayer.exeption.FailCreateDeliveryException;
import logiclayer.exeption.UnsupportableWeightFactorException;
import logiclayer.service.BillService;
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

import static constants.TestConstant.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static web.constant.AttributeConstants.SESSION_LANG;
import static web.constant.PageConstance.*;

@RunWith(MockitoJUnitRunner.class)

public class UserDeliveryInitiationControllerTest {

    private static final String DELIVERY_WEIGHT = "deliveryWeight";
    private static final String LOCALITY_GET_ID = "localityGetID";
    private static final String LOCALITY_SAND_ID = "localitySandID";
    private static final String ADDRESSEE_EMAIL = "addresseeEmail";
    @InjectMocks
    UserDeliveryInitiationController userDeliveryInitiationController;
    @Mock
    BillService billService;
    @Mock
    LocalityService localityService;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    Validator deliveryOrderCreateDtoValidator;
    @Mock
    private HttpSession session;

    @Before
    public void setUp() throws Exception {
        when(httpServletRequest.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());
        when(httpServletRequest.getParameter(DELIVERY_WEIGHT)).thenReturn("1");
        when(httpServletRequest.getParameter(LOCALITY_GET_ID)).thenReturn("1");
        when(httpServletRequest.getParameter(LOCALITY_SAND_ID)).thenReturn("1");
        when(httpServletRequest.getParameter(ADDRESSEE_EMAIL)).thenReturn("1");
        when(httpServletRequest.getParameter(LOCALITY_SAND_ID)).thenReturn("1");
        when(session.getAttribute("user")).thenReturn(getAdverser());
    }

    @Test
    public void performGet() {
        Locality locality = getLocalitySend();
        LocaliseLocalityDto localiseLocalityDto = getLocaliseLocalityDtoEn(locality);
        List<LocaliseLocalityDto> localities = Collections.singletonList(localiseLocalityDto);
        when(localityService.getLocaliseLocalities(any(Locale.class))).thenReturn(localities);

        String result = userDeliveryInitiationController.doGet(httpServletRequest);

        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(1)).getSession();
        verify(session, times(1)).getAttribute(SESSION_LANG);
        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_DELIVERY_INITIATION_FILE_NAME, result);
    }


    @Test
    public void performPostAllCorrect() throws UnsupportableWeightFactorException, FailCreateDeliveryException {
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());
        when(deliveryOrderCreateDtoValidator.isValid(any(HttpServletRequest.class))).thenReturn(true);

        String result = userDeliveryInitiationController.doPost(httpServletRequest);

        verify(httpServletRequest, times(1)).getParameter(DELIVERY_WEIGHT);
        verify(httpServletRequest, times(1)).getParameter(LOCALITY_GET_ID);
        verify(httpServletRequest, times(1)).getParameter(LOCALITY_SAND_ID);
        verify(httpServletRequest, times(1)).getParameter(ADDRESSEE_EMAIL);
        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(2)).getSession();
        verify(deliveryOrderCreateDtoValidator, times(1)).isValid(any(HttpServletRequest.class));
        verify(session, times(1)).getAttribute(SESSION_LANG);
        verify(billService, times(1)).initializeBill(any(DeliveryOrderCreateDto.class), anyLong());
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_DELIVERY_INITIATION_FILE_NAME, result);
    }

    //
    @Test
    public void performPostIncorrectInputIncorrect() throws UnsupportableWeightFactorException, FailCreateDeliveryException {
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());
        when(deliveryOrderCreateDtoValidator.isValid(any(HttpServletRequest.class))).thenReturn(false);

        String result = userDeliveryInitiationController.doPost(httpServletRequest);

        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(1)).getSession();
        verify(deliveryOrderCreateDtoValidator, times(1)).isValid(any(HttpServletRequest.class));
        verify(session, times(1)).getAttribute(SESSION_LANG);
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_DELIVERY_INITIATION_FILE_NAME, result);
    }

    @Test
    public void performPostIncorrectDataUnsupportableWeightFactorException() throws UnsupportableWeightFactorException, FailCreateDeliveryException {
        doThrow(UnsupportableWeightFactorException.class).when(billService).initializeBill(any(DeliveryOrderCreateDto.class), anyLong());
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());
        when(deliveryOrderCreateDtoValidator.isValid(any(HttpServletRequest.class))).thenReturn(true);

        String result = userDeliveryInitiationController.doPost(httpServletRequest);

        verify(httpServletRequest, times(1)).getParameter(DELIVERY_WEIGHT);
        verify(httpServletRequest, times(1)).getParameter(LOCALITY_GET_ID);
        verify(httpServletRequest, times(1)).getParameter(LOCALITY_SAND_ID);
        verify(httpServletRequest, times(1)).getParameter(ADDRESSEE_EMAIL);
        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(2)).getSession();
        verify(deliveryOrderCreateDtoValidator, times(1)).isValid(any(HttpServletRequest.class));
        verify(session, times(1)).getAttribute(SESSION_LANG);
        verify(billService, times(1)).initializeBill(any(DeliveryOrderCreateDto.class), anyLong());
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_DELIVERY_INITIATION_FILE_NAME, result);
    }

    @Test
    public void performPostIncorrectDataFailCreateDeliveryException() throws UnsupportableWeightFactorException, FailCreateDeliveryException {
        doThrow(FailCreateDeliveryException.class).when(billService).initializeBill(any(DeliveryOrderCreateDto.class), anyLong());
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());
        when(deliveryOrderCreateDtoValidator.isValid(any(HttpServletRequest.class))).thenReturn(true);

        String result = userDeliveryInitiationController.doPost(httpServletRequest);

        verify(httpServletRequest, times(1)).getParameter(DELIVERY_WEIGHT);
        verify(httpServletRequest, times(1)).getParameter(LOCALITY_GET_ID);
        verify(httpServletRequest, times(1)).getParameter(LOCALITY_SAND_ID);
        verify(httpServletRequest, times(1)).getParameter(ADDRESSEE_EMAIL);
        verify(localityService, times(1)).getLocaliseLocalities(any(Locale.class));
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(2)).getSession();
        verify(deliveryOrderCreateDtoValidator, times(1)).isValid(any(HttpServletRequest.class));
        verify(session, times(1)).getAttribute(SESSION_LANG);
        verify(billService, times(1)).initializeBill(any(DeliveryOrderCreateDto.class), anyLong());
        verify(httpServletRequest, times(2)).setAttribute(anyString(), any(Object.class));
        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_DELIVERY_INITIATION_FILE_NAME, result);
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