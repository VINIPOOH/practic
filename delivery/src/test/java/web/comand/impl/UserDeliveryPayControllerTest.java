package web.comand.impl;

import dto.validation.IDValidator;
import logiclayer.exeption.OperationFailException;
import logiclayer.service.BillService;
import logiclayer.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

import static constants.TestConstant.getAdverser;
import static constants.TestConstant.getLocaleEn;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static web.constant.AttributeConstants.SESSION_LANG;
import static web.constant.PageConstance.*;

@RunWith(MockitoJUnitRunner.class)
public class UserDeliveryPayControllerTest {

    @InjectMocks
    UserDeliveryPayController userDeliveryPayController;
    @Mock
    BillService billService;
    @Mock
    UserService userService;
    @Mock
    IDValidator idValidator;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    private HttpSession session;

    @Before
    public void setUp() {
        when(httpServletRequest.getSession()).thenReturn(session);
        when(session.getAttribute(SESSION_LANG)).thenReturn(getLocaleEn());
        when(session.getAttribute("user")).thenReturn(getAdverser());
        when(httpServletRequest.getParameter("Id")).thenReturn("1");
    }

    @Test
    public void performGet() {
        String actual = userDeliveryPayController.doGet(httpServletRequest);

        verify(billService, times(1)).getInfoToPayBillsByUserID(anyLong(), any(Locale.class));
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(2)).getSession();
        verify(session, times(1)).getAttribute(SESSION_LANG);
        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_DELIVERY_PAY_FILE_NAME, actual);
    }

    @Test
    public void performPost() throws OperationFailException {
        when(idValidator.isValid(any(HttpServletRequest.class), anyString())).thenReturn(true);

        String actual = userDeliveryPayController.doPost(httpServletRequest);

        verify(httpServletRequest, times(3)).getSession();
        verify(session, times(1)).getAttribute(SESSION_LANG);
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        verify(billService, times(1)).payForDelivery(anyLong(), anyLong());
        verify(session, times(1)).getAttribute(SESSION_LANG);
        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_DELIVERY_PAY_FILE_NAME, actual);
    }

    @Test(expected = RuntimeException.class)
    public void performPostIncorrectInput() {
        when(idValidator.isValid(any(HttpServletRequest.class), anyString())).thenReturn(false);

        userDeliveryPayController.doPost(httpServletRequest);

        fail();
    }
}