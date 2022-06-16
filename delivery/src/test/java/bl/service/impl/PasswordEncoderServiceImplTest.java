package bl.service.impl;

import logiclayer.service.impl.PasswordEncoderServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PasswordEncoderServiceImplTest {

    @InjectMocks
    PasswordEncoderServiceImpl passwordEncoderService;

    @Test
    public void encode() {
        String expected = "5f4dcc3b5aa765d61d8327deb882cf99";

        String result = passwordEncoderService.encode("password");

        assertEquals(expected, result);
    }
}