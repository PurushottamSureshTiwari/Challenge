package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.BalanceTransfer;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public void transferAmount(BalanceTransfer balanceTransfer) throws Exception {
    ReentrantLock lock = new ReentrantLock();
    boolean acquiredLock = lock.tryLock(2000, TimeUnit.MILLISECONDS);
    if(acquiredLock) {
      try {
        accountsRepository.transferAmount(balanceTransfer);
      }catch(NegativeBalanceException| AccountNotExistException e){
        throw new Exception(e.getMessage());
      }finally {
        lock.unlock();
      }
    }
  }
}
