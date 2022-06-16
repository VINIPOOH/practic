package web.comand.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

import static constants.TestConstant.getAdverser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static web.constant.AttributeConstants.LOGGED_USER_NAMES;
import static web.constant.AttributeConstants.SESSION_USER;
import static web.constant.PageConstance.*;

@RunWith(MockitoJUnitRunner.class)
public class LogOutControllerTest {
    @InjectMocks
    LogOutController logOutController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpSession session;
    @Mock
    ServletContext servletContext;

    @Before
    public void setUp() {
        when(httpServletRequest.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContext);
        when(session.getAttribute(SESSION_USER)).thenReturn(getAdverser());
        when(servletContext.getAttribute(LOGGED_USER_NAMES)).thenReturn(new ConcurrentHashMap<String, HttpSession>());
    }

    @Test
    public void execute() {
        String actual = logOutController.doGet(httpServletRequest);

        verify(httpServletRequest, times(3)).getSession();
        verify(session, times(1)).getAttribute(SESSION_USER);
        verify(session, times(1)).invalidate();
        verify(session, times(1)).getServletContext();
        assertEquals(REDIRECT_COMMAND + ANONYMOUS_FOLDER + LOGIN_REQUEST_COMMAND, actual);
    }
}