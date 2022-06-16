package web.comand.impl;

import dto.validation.IDValidator;
import logiclayer.service.BillService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import web.util.Pagination;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static constants.TestConstant.getAdverser;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;
import static web.constant.AttributeConstants.SESSION_USER;
import static web.constant.PageConstance.*;

@RunWith(MockitoJUnitRunner.class)
public class UserStatisticControllerTest {

    @InjectMocks
    UserStatisticController userStatisticController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    IDValidator idValidator;
    @Mock
    BillService billService;
    @Mock
    Pagination pagination;
    @Mock
    private HttpSession session;

    @Before
    public void setUp() {
        when(httpServletRequest.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(getAdverser());
    }

    @Test
    public void execute() {
        String actual = userStatisticController.doGet(httpServletRequest);

        verify(billService, times(1)).getBillHistoryByUserId(anyLong(), anyInt(), anyInt());
        verify(billService, times(1)).countAllBillsByUserId(anyLong());
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(2)).getSession();
        verify(session, times(2)).getAttribute(SESSION_USER);
        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_STATISTIC_FILE_NAME, actual);
    }
}