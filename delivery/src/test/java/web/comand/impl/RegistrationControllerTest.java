package web.comand.impl;

import dto.RegistrationInfoDto;
import dto.validation.RegistrationDtoValidator;
import logiclayer.exeption.OccupiedLoginException;
import logiclayer.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static web.constant.PageConstance.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_REPEAT = "passwordRepeat";
    @InjectMocks
    RegistrationController registrationController;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    RegistrationDtoValidator registrationDtoValidator;
    @Mock
    UserService userService;

    @Before
    public void setUp() {
        when(httpServletRequest.getParameter(USERNAME)).thenReturn("user");
        when(httpServletRequest.getParameter(PASSWORD)).thenReturn("password");
        when(httpServletRequest.getParameter(PASSWORD_REPEAT)).thenReturn("password");
    }

    @Test
    public void performGet() {
        String actual = registrationController.doGet(httpServletRequest);

        assertEquals(MAIN_WEB_FOLDER + ANONYMOUS_FOLDER + REGISTRATION_FILE_NAME, actual);
    }

    @Test
    public void performPost() throws OccupiedLoginException {
        when(registrationDtoValidator.isValid(any(HttpServletRequest.class))).thenReturn(true);
        when(userService.addNewUserToDB(any(RegistrationInfoDto.class))).thenReturn(true);

        String actual = registrationController.doPost(httpServletRequest);

        verify(httpServletRequest, times(1)).getParameter(USERNAME);
        verify(httpServletRequest, times(1)).getParameter(PASSWORD);
        verify(httpServletRequest, times(1)).getParameter(PASSWORD_REPEAT);
        verify(httpServletRequest, times(0)).setAttribute(anyString(), any(Object.class));
        verify(registrationDtoValidator, times(1)).isValid(any(HttpServletRequest.class));
        assertEquals(REDIRECT_COMMAND + ANONYMOUS_FOLDER + LOGIN_REQUEST_COMMAND, actual);
    }

    @Test
    public void performPostIncorrectInput() {
        when(registrationDtoValidator.isValid(any(HttpServletRequest.class))).thenReturn(false);

        String actual = registrationController.doPost(httpServletRequest);

        verify(httpServletRequest, times(0)).getParameter(USERNAME);
        verify(httpServletRequest, times(0)).getParameter(PASSWORD);
        verify(httpServletRequest, times(0)).getParameter(PASSWORD_REPEAT);
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        verify(registrationDtoValidator, times(1)).isValid(any(HttpServletRequest.class));
        assertEquals(MAIN_WEB_FOLDER + ANONYMOUS_FOLDER + REGISTRATION_FILE_NAME, actual);
    }

    @Test
    public void performPostIncorrectData() throws OccupiedLoginException {
        when(registrationDtoValidator.isValid(any(HttpServletRequest.class))).thenReturn(true);
        when(userService.addNewUserToDB(any(RegistrationInfoDto.class))).thenThrow(OccupiedLoginException.class);

        String actual = registrationController.doPost(httpServletRequest);

        verify(httpServletRequest, times(1)).getParameter(USERNAME);
        verify(httpServletRequest, times(1)).getParameter(PASSWORD);
        verify(httpServletRequest, times(1)).getParameter(PASSWORD_REPEAT);
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(Object.class));
        verify(registrationDtoValidator, times(1)).isValid(any(HttpServletRequest.class));
        assertEquals(MAIN_WEB_FOLDER + ANONYMOUS_FOLDER + REGISTRATION_FILE_NAME, actual);
    }
}