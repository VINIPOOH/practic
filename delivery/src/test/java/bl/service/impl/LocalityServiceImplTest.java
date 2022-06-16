package bl.service.impl;

import dal.dao.LocalityDao;
import dal.entity.Locality;
import dto.LocaliseLocalityDto;
import logiclayer.service.impl.LocalityServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static constants.TestConstant.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocalityServiceImplTest {

    @InjectMocks
    LocalityServiceImpl localityService;
    @Mock
    LocalityDao localityDao;

    @Test
    public void getLocalitiesRu() {
        Locality locality = getLocalityGet();
        List<Locality> localities = Collections.singletonList(locality);
        LocaliseLocalityDto localiseLocalityDto = LocaliseLocalityDto.builder()
                .id(locality.getId())
                .name(locality.getNameRu())
                .build();
        when(localityDao.findAllLocaliseLocalitiesWithoutConnection(any(Locale.class))).thenReturn(localities);

        List<LocaliseLocalityDto> result = localityService.getLocaliseLocalities(getLocaleRu());

        verify(localityDao, times(1)).findAllLocaliseLocalitiesWithoutConnection(any(Locale.class));
        assertEquals(localiseLocalityDto, result.get(0));
        assertEquals(localities.size(), result.size());
    }

    @Test
    public void getLocalitiesEn() {
        Locality locality = getLocalityGet();
        List<Locality> localities = Collections.singletonList(locality);
        LocaliseLocalityDto localiseLocalityDto = LocaliseLocalityDto.builder()
                .id(locality.getId())
                .name(locality.getNameEn())
                .build();
        when(localityDao.findAllLocaliseLocalitiesWithoutConnection(any(Locale.class))).thenReturn(localities);

        List<LocaliseLocalityDto> result = localityService.getLocaliseLocalities(getLocaleEn());

        verify(localityDao, times(1)).findAllLocaliseLocalitiesWithoutConnection(any(Locale.class));
        assertEquals(localiseLocalityDto, result.get(0));
        assertEquals(localities.size(), result.size());
    }
}