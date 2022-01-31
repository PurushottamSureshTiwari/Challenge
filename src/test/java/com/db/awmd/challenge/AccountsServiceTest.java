package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.BalanceTransfer;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }

  @Test
  public void transferAmountWithSameAccountId()throws Exception{
    BalanceTransfer balanceTransfer = new BalanceTransfer("Id-1234", "Id-1234", new BigDecimal(1000));
    try {
      accountsService.transferAmount(balanceTransfer);
    }catch (DuplicateAccountIdException e){
      assertThat(e.getMessage()).isEqualTo("Both Account are same");
    }
  }

  @Test
  public void transferAmountWithBlankFromAccountId()throws Exception{
    BalanceTransfer balanceTransfer = new BalanceTransfer("", "Id-456", new BigDecimal(1000));
    try {
      accountsService.transferAmount(balanceTransfer);
    }catch (AccountNotExistException e){
      assertThat(e.getMessage()).isEqualTo("From Account doesn't exist");
    }
  }

  @Test
  public void transferAmountWithBlankToAccountId()throws Exception{
    BalanceTransfer balanceTransfer = new BalanceTransfer("Id-1234", "", new BigDecimal(1000));
    try {
      accountsService.transferAmount(balanceTransfer);
    }catch (AccountNotExistException e){
      assertThat(e.getMessage()).isEqualTo("To Account doesn't exist");
    }
  }


  @Test
  public void transferAmountWithInsufficientBalance()throws Exception{
    Account accountFrom = new Account("Id-1234",new BigDecimal(100.00));
    this.accountsService.createAccount(accountFrom);
    Account accountTo = new Account("Id-456", new BigDecimal(200.00));
    this.accountsService.createAccount(accountTo);
    BalanceTransfer balanceTransfer = new BalanceTransfer("Id-1234", "Id-456", new BigDecimal(200));
    try {
      accountsService.transferAmount(balanceTransfer);
    }catch (NegativeBalanceException e){
      assertThat(e.getMessage()).isEqualTo("Can't do amount transfer with negative balance");
    }
  }
}
