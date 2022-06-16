package web.comand.impl;

import logiclayer.exeption.NoSuchUserException;
import logiclayer.exeption.ToMachMoneyException;
import logiclayer.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static constants.TestConstant.getAdverser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static web.constant.PageConstance.*;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileControllerTest {
    private static final String MONEY = "money";
    @InjectMocks
    UserProfileController userProfileController;
    @Mock
    UserService userService;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpSession session;

    @Before
    public void setUp() throws Exception {
        when(httpServletRequest.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(getAdverser());
        when(httpServletRequest.getParameter(MONEY)).thenReturn("1");
    }

    @Test
    public void performGet() {
        String actual = userProfileController.doGet(httpServletRequest);

        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_PROFILE_FILE_NAME, actual);
    }

    @Test
    public void performPost() throws NoSuchUserException, ToMachMoneyException {

        String actual = userProfileController.doPost(httpServletRequest);

        verify(httpServletRequest, times(2)).getParameter(MONEY);
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        verify(httpServletRequest, times(1)).getSession();
        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_PROFILE_FILE_NAME, actual);
    }

    @Test
    public void performPostInputMoneyZero() throws NoSuchUserException {
        when(httpServletRequest.getParameter(MONEY)).thenReturn("0");

        String actual = userProfileController.doPost(httpServletRequest);

        verify(httpServletRequest, times(1)).getParameter(MONEY);
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        assertEquals(MAIN_WEB_FOLDER + USER_FOLDER + USER_PROFILE_FILE_NAME, actual);
    }

    @Test(expected = RuntimeException.class)
    public void performPostNoSuchUserException() throws NoSuchUserException, ToMachMoneyException {
        doThrow(NoSuchUserException.class).when(userService).replenishAccountBalance(anyLong(), anyLong());

        userProfileController.doPost(httpServletRequest);

        fail();
    }
}