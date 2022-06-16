package web.comand.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static web.constant.PageConstance.*;

@RunWith(MockitoJUnitRunner.class)
public class PhantomCommandTest {

    @InjectMocks
    PhantomController phantomCommand;

    @Mock
    HttpServletRequest httpServletRequest;

    @Test
    public void execute() {
        String actual = phantomCommand.doGet(httpServletRequest);

        assertEquals(REDIRECT_COMMAND + ERROR_404_COMMAND, actual);
    }
}