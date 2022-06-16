package bl.service.impl;

import dal.dao.DeliveryDao;
import dal.dao.WayDao;
import dal.dto.DeliveryCostAndTimeDto;
import dal.entity.Delivery;
import dto.DeliveryInfoRequestDto;
import dto.DeliveryInfoToGetDto;
import dto.PriceAndTimeOnDeliveryDto;
import logiclayer.service.impl.DeliveryServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static constants.TestConstant.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeliveryServiceImplTest {

    @InjectMocks
    DeliveryServiceImpl deliveryService;

    @Mock
    WayDao wayDao;

    @Mock
    DeliveryDao deliveryDao;

    @Test
    public void getDeliveryCostAndTimeDto() {
        DeliveryCostAndTimeDto deliveryCostAndTimeDto = DeliveryCostAndTimeDto.builder()
                .costInCents(1)
                .timeOnWayInHours(1)
                .build();
        PriceAndTimeOnDeliveryDto expected = getPriceAndTimeOnDeliveryDto();
        DeliveryInfoRequestDto deliveryInfoRequestDto = getDeliveryInfoRequestDto();
        when(wayDao.findByLocalitySandIdAndLocalityGetId(anyLong(), anyLong(), anyInt())).thenReturn(Optional.of(deliveryCostAndTimeDto));

        Optional<PriceAndTimeOnDeliveryDto> result = deliveryService.getDeliveryCostAndTimeDto(deliveryInfoRequestDto);

        verify(wayDao, times(1)).findByLocalitySandIdAndLocalityGetId(anyLong(), anyLong(), anyInt());
        assertEquals(expected, result.get());
    }

    @Test
    public void getDeliveryInfoToGetRu() {
        Delivery delivery = getDelivery();
        delivery.setBill(getBill());
        DeliveryInfoToGetDto deliveryInfoToGetDto = getDeliveryInfoToGetDto();
        deliveryInfoToGetDto.setLocalityGetName(delivery.getWay().getLocalityGet().getNameRu());
        deliveryInfoToGetDto.setLocalitySandName(delivery.getWay().getLocalitySand().getNameRu());
        when(deliveryDao.getDeliveryInfoToGet(anyLong(), any(Locale.class))).thenReturn(Collections.singletonList(delivery));

        List<DeliveryInfoToGetDto> result = deliveryService.getInfoToGetDeliveriesByUserID(1, getLocaleRu());

        verify(deliveryDao, times(1)).getDeliveryInfoToGet(anyLong(), any(Locale.class));
        assertEquals(deliveryInfoToGetDto, result.get(0));
        assertEquals(getDeliveres().size(), result.size());
    }

    @Test
    public void getDeliveryInfoToGetEn() {
        Delivery delivery = getDelivery();
        delivery.setBill(getBill());
        DeliveryInfoToGetDto deliveryInfoToGetDto = getDeliveryInfoToGetDto();
        deliveryInfoToGetDto.setLocalityGetName(delivery.getWay().getLocalityGet().getNameEn());
        deliveryInfoToGetDto.setLocalitySandName(delivery.getWay().getLocalitySand().getNameEn());
        when(deliveryDao.getDeliveryInfoToGet(anyLong(), any(Locale.class))).thenReturn(Collections.singletonList(delivery));

        List<DeliveryInfoToGetDto> result = deliveryService.getInfoToGetDeliveriesByUserID(1, getLocaleEn());

        verify(deliveryDao, times(1)).getDeliveryInfoToGet(anyLong(), any(Locale.class));
        assertEquals(deliveryInfoToGetDto, result.get(0));
        assertEquals(getDeliveres().size(), result.size());
    }

    @Test
    public void confirmGettingDelivery() {
        when(deliveryDao.confirmGettingDelivery(anyLong(), anyLong())).thenReturn(true);

        boolean result = deliveryService.confirmGettingDelivery(1, 1);

        assertTrue(result);
    }

    private PriceAndTimeOnDeliveryDto getPriceAndTimeOnDeliveryDto() {
        return PriceAndTimeOnDeliveryDto.builder()
                .costInCents(1)
                .timeOnWayInHours(1)
                .build();
    }

    private DeliveryInfoRequestDto getDeliveryInfoRequestDto() {
        return DeliveryInfoRequestDto.builder()
                .localitySandID(1)
                .localityGetID(1)
                .deliveryWeight(1)
                .build();
    }

}