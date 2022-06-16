package bl.service.impl;

import dal.conection.pool.impl.TransactionalManagerImpl;
import dal.dao.BillDao;
import dal.dao.DeliveryDao;
import dal.dao.UserDao;
import dal.entity.Bill;
import dal.exeption.AskedDataIsNotCorrect;
import dto.BillDto;
import dto.BillInfoToPayDto;
import logiclayer.exeption.FailCreateDeliveryException;
import logiclayer.exeption.OperationFailException;
import logiclayer.exeption.UnsupportableWeightFactorException;
import logiclayer.service.impl.BillServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static constants.TestConstant.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BillServiceImplTest {

    @InjectMocks
    BillServiceImpl billService;

    @Mock
    BillDao billDao;
    @Mock
    UserDao userDao;
    @Mock
    DeliveryDao deliveryDao;
    @Mock
    TransactionalManagerImpl connectionManager;


    @Test
    public void initializeBillAllCorrect() throws UnsupportableWeightFactorException, FailCreateDeliveryException, AskedDataIsNotCorrect, SQLException {
        when(deliveryDao.createDelivery(anyString(), anyLong(), anyLong(), anyInt())).thenReturn(0L);
        when(billDao.createBill(anyLong(), anyLong(), anyLong(), anyLong(), anyInt())).thenReturn(true);

        billService.initializeBill(getDeliveryOrderCreateDto(), getUserId());

        verify(deliveryDao, times(1)).createDelivery(anyString(), anyLong(), anyLong(), anyInt());
        verify(billDao, times(1)).createBill(anyLong(), anyLong(), anyLong(), anyLong(), anyInt());
    }

    @Test(expected = UnsupportableWeightFactorException.class)
    public void initializeBillIncorrectWeight() throws AskedDataIsNotCorrect, SQLException, UnsupportableWeightFactorException, FailCreateDeliveryException {
        when(deliveryDao.createDelivery(anyString(), anyLong(), anyLong(), anyInt())).thenReturn(0L);
        when(billDao.createBill(anyLong(), anyLong(), anyLong(), anyLong(), anyInt())).thenReturn(false);

        billService.initializeBill(getDeliveryOrderCreateDto(), getUserId());

        fail();
    }

    @Test(expected = FailCreateDeliveryException.class)
    public void initializeBillCreateDeliveryException() throws UnsupportableWeightFactorException, FailCreateDeliveryException, AskedDataIsNotCorrect, SQLException {
        when(deliveryDao.createDelivery(anyString(), anyLong(), anyLong(), anyInt())).thenReturn(0L);
        when(billDao.createBill(anyLong(), anyLong(), anyLong(), anyLong(), anyInt())).thenThrow(SQLException.class);

        billService.initializeBill(getDeliveryOrderCreateDto(), getUserId());

        fail();
    }

    @Test(expected = FailCreateDeliveryException.class)
    public void initializeBillCreateBillIncorrectDeliveryData() throws UnsupportableWeightFactorException, FailCreateDeliveryException, AskedDataIsNotCorrect {
        when(deliveryDao.createDelivery(anyString(), anyLong(), anyLong(), anyInt())).thenThrow(AskedDataIsNotCorrect.class);

        billService.initializeBill(getDeliveryOrderCreateDto(), getUserId());

        fail();
    }

    @Test
    public void payForDeliveryAllCorrect() throws AskedDataIsNotCorrect, SQLException, OperationFailException {
        when(billDao.getBillCostIfItIsNotPaid(anyLong(), anyLong())).thenReturn(1L);
        when(billDao.murkBillAsPayed(anyLong())).thenReturn(true);
        when(userDao.withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong())).thenReturn(true);


        billService.payForDelivery(getUserId(), getBillId());

        verify(billDao, times(1)).getBillCostIfItIsNotPaid(anyLong(), anyLong());
        verify(billDao, times(1)).murkBillAsPayed(anyLong());
        verify(userDao, times(1)).withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong());

    }

    @Test(expected = OperationFailException.class)
    public void payForDeliveryUserHaveNotMoney() throws AskedDataIsNotCorrect, SQLException, OperationFailException {
        when(billDao.getBillCostIfItIsNotPaid(anyLong(), anyLong())).thenReturn(1L);
        when(userDao.withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong())).thenReturn(false);


        billService.payForDelivery(getUserId(), getBillId());

        verify(billDao, times(1)).getBillCostIfItIsNotPaid(anyLong(), anyLong());
        verify(billDao, times(0)).murkBillAsPayed(anyLong());
        verify(userDao, times(1)).withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong());
    }

    @Test(expected = OperationFailException.class)
    public void payForDeliveryIncorrectUserData() throws AskedDataIsNotCorrect, SQLException, OperationFailException {
        when(billDao.getBillCostIfItIsNotPaid(anyLong(), anyLong())).thenReturn(1L);
        when(userDao.withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong())).thenThrow(SQLException.class);

        billService.payForDelivery(getUserId(), getBillId());

        verify(billDao, times(1)).getBillCostIfItIsNotPaid(anyLong(), anyLong());
        verify(billDao, times(0)).murkBillAsPayed(anyLong());
        verify(userDao, times(1)).withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong());
    }

    @Test(expected = OperationFailException.class)
    public void payForDeliveryIncorrectBillData() throws AskedDataIsNotCorrect, SQLException, OperationFailException {
        when(billDao.getBillCostIfItIsNotPaid(anyLong(), anyLong())).thenThrow(AskedDataIsNotCorrect.class);
        billService.payForDelivery(getUserId(), getBillId());

        verify(billDao, times(1)).getBillCostIfItIsNotPaid(anyLong(), anyLong());
        verify(billDao, times(0)).murkBillAsPayed(anyLong());
        verify(userDao, times(0)).withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong());
    }

    @Test(expected = OperationFailException.class)
    public void payForDeliveryIncorrectBillDatInDb() throws AskedDataIsNotCorrect, SQLException, OperationFailException {
        when(billDao.getBillCostIfItIsNotPaid(anyLong(), anyLong())).thenReturn(0L);
        when(billDao.murkBillAsPayed(anyLong())).thenReturn(false);
        when(userDao.withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong())).thenReturn(true);

        billService.payForDelivery(getUserId(), getBillId());

        verify(billDao, times(1)).getBillCostIfItIsNotPaid(anyLong(), anyLong());
        verify(billDao, times(1)).murkBillAsPayed(anyLong());
        verify(userDao, times(1)).withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong());
    }

    @Test(expected = OperationFailException.class)
    public void payForDeliveryProblemWithDb() throws AskedDataIsNotCorrect, SQLException, OperationFailException {
        when(billDao.getBillCostIfItIsNotPaid(anyLong(), anyLong())).thenReturn(0L);
        when(billDao.murkBillAsPayed(anyLong())).thenThrow(SQLException.class);
        when(userDao.withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong())).thenReturn(true);

        billService.payForDelivery(getUserId(), getBillId());

        verify(billDao, times(1)).getBillCostIfItIsNotPaid(anyLong(), anyLong());
        verify(billDao, times(1)).murkBillAsPayed(anyLong());
        verify(userDao, times(1)).withdrawUserBalanceOnSumIfItPossible(anyLong(), anyLong());
    }

    @Test
    public void getInfoToPayBillsByUserIDEn() {
        BillInfoToPayDto billInfoToPayDto = getBillInfoToPayDto();
        Bill bill = getBill();
        List<Bill> bills = Collections.singletonList(bill);
        billInfoToPayDto.setLocalityGetName(bill.getDelivery().getWay().getLocalityGet().getNameEn());
        billInfoToPayDto.setLocalitySandName(bill.getDelivery().getWay().getLocalitySand().getNameEn());
        when(billDao.getInfoToPayBillByUserId(anyLong(), any(Locale.class))).thenReturn(bills);

        List<BillInfoToPayDto> result = billService.getInfoToPayBillsByUserID(getUserId(), getLocaleEn());

        verify(billDao, times(1)).getInfoToPayBillByUserId(anyLong(), any(Locale.class));
        assertEquals(getBills().size(), result.size());
        assertEquals(billInfoToPayDto, result.get(0));
    }

    @Test
    public void getInfoToPayBillsByUserIDRu() {
        BillInfoToPayDto billInfoToPayDto = getBillInfoToPayDto();
        Bill bill = getBill();
        List<Bill> bills = Collections.singletonList(bill);
        billInfoToPayDto.setLocalityGetName(bill.getDelivery().getWay().getLocalityGet().getNameRu());
        billInfoToPayDto.setLocalitySandName(bill.getDelivery().getWay().getLocalitySand().getNameRu());
        when(billDao.getInfoToPayBillByUserId(anyLong(), any(Locale.class))).thenReturn(bills);

        List<BillInfoToPayDto> result = billService.getInfoToPayBillsByUserID(getUserId(), getLocaleRu());

        verify(billDao, times(1)).getInfoToPayBillByUserId(anyLong(), any(Locale.class));
        assertEquals(getBills().size(), result.size());
        assertEquals(billInfoToPayDto, result.get(0));
    }

    @Test
    public void countAllBillsByUserId() {
        long expectedResult = 3;
        when(billDao.countAllBillsByUserId(anyLong())).thenReturn(expectedResult);

        long result = billService.countAllBillsByUserId(1L);

        verify(billDao, times(1)).countAllBillsByUserId(anyLong());
        assertEquals(expectedResult, result);
    }

    @Test
    public void getBillHistoryByUserId() {
        List<Bill> bills = getBills();
        when(billDao.getHistoricBillsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(bills);

        List<BillDto> result = billService.getBillHistoryByUserId(1, 1, 1);

        verify(billDao, times(1)).getHistoricBillsByUserId(anyLong(), anyInt(), anyInt());
        assertEquals(getBillDto(), result.get(0));
        assertEquals(bills.size(), result.size());

    }


}