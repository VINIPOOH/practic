package web.comand.impl;

import dal.entity.User;
import dto.UserStatisticDto;
import logiclayer.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static constants.TestConstant.getAdverser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static web.constant.PageConstance.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminUsersControllerTest {

    @InjectMocks
    AdminUsersController adminUsersController;

    @Mock
    UserService userService;

    @Mock
    HttpServletRequest httpServletRequest;

    @Test
    public void execute() {
        User user = getAdverser();
        UserStatisticDto userStatisticDto = getUserStatisticDto(user);
        List<UserStatisticDto> users = Collections.singletonList(userStatisticDto);
        when(userService.getAllUsers()).thenReturn(users);

        String actual = adminUsersController.doGet(httpServletRequest);

        verify(userService, times(1)).getAllUsers();
        assertEquals(MAIN_WEB_FOLDER + ADMIN_FOLDER + USERS_JSP, actual);
    }

    private UserStatisticDto getUserStatisticDto(User user) {
        return UserStatisticDto.builder()
                .roleType(user.getRoleType().name())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}